/*******************************************************************************
 * Copyright 2009 Art Wild
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package api.wireless.gdata.serializer.xml;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlGDataParser;
import api.wireless.gdata.parser.xml.XmlParserFactory;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
import api.wireless.gdata.util.common.base.StringUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Serializes GData entries to the Atom XML format.
 */
public class XmlEntryGDataSerializer implements GDataSerializer {

	/** The XmlParserFactory that is used to create the XmlSerializer */
	private final XmlParserFactory factory;

	/** The entry being serialized. */
	private final Entry entry;

	/**
	 * Creates a new XmlEntryGDataSerializer that will serialize the provided
	 * entry.
	 * 
	 * @param entry
	 *            The entry that should be serialized.
	 */
	public XmlEntryGDataSerializer(XmlParserFactory factory, Entry entry) {
		this.factory = factory;
		this.entry = entry;
	}

	/**
	 * Returns the entry being serialized.
	 * 
	 * @return The entry being serialized.
	 */
	protected Entry getEntry() {
		return entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see GDataSerializer#getContentType()
	 */
	public String getContentType() {
		return "application/atom+xml";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see GDataSerializer#serialize(java.io.OutputStream)
	 */
	public void serialize(OutputStream out, int format) throws IOException,
			ParseException {
		XmlSerializer serializer = null;
		try {
			serializer = factory.createSerializer();
		} catch (XmlPullParserException e) {
			throw new ParseException("Unable to create XmlSerializer.", e);
		}

		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", new Boolean(false));

		declareEntryNamespaces(serializer);
		serializer.startTag(XmlGDataParser.NAMESPACE_ATOM_URI, "entry");

		// Set eTag
		String eTag = getEntry().getEtag();
		if (eTag != null)
			serializer.attribute(XmlGDataParser.NAMESPACE_GD_URI, "etag", eTag);

		serializeEntryContents(serializer, format);

		serializer.endTag(XmlGDataParser.NAMESPACE_ATOM_URI, "entry");
		serializer.endDocument();
		serializer.flush();
	}

	private final void declareEntryNamespaces(XmlSerializer serializer)
			throws IOException {
		serializer.setPrefix("" /* default ns */,
				XmlGDataParser.NAMESPACE_ATOM_URI);
		serializer.setPrefix(XmlGDataParser.NAMESPACE_GD,
				XmlGDataParser.NAMESPACE_GD_URI);
		declareExtraEntryNamespaces(serializer);
	}

	protected void declareExtraEntryNamespaces(XmlSerializer serializer)
			throws IOException {
		// no-op in this class
	}

	/**
	 * @param serializer
	 * @throws IOException
	 */
	private final void serializeEntryContents(XmlSerializer serializer,
			int format) throws ParseException, IOException {

		if (format != FORMAT_CREATE) {
			serializeId(serializer, entry.getId());
		}

		if (format != FORMAT_CREATE) {
			serializeUpdateDate(serializer, entry.getUpdateDate());
		}

		serializeCategory(serializer, entry.getCategory(), entry
				.getCategoryScheme(), entry.getLabel());

		serializeTitle(serializer, entry.getTitle(), format);

		if (format != FORMAT_CREATE) {
			if (entry instanceof WorksheetEntry) {
				serializeContent(serializer, entry.getContent());
				serializeLink(
						serializer,
						"http://schemas.google.com/spreadsheets/2006#listfeed" /* rel */,
						((WorksheetEntry) entry).getListFeedUri(), "application/atom+xml" /* type */);
				serializeLink(
						serializer,
						"http://schemas.google.com/spreadsheets/2006#cellsfeed" /* rel */,
						((WorksheetEntry) entry).getCellFeedUri(), "application/atom+xml" /* type */);
				serializeLink(serializer, "self", entry.getSelfUri(),
						"application/atom+xml");
				serializeLink(serializer, "edit", entry.getEditUri(),
						"application/atom+xml");
			} else {
				serializeLink(serializer, "edit" /* rel */, entry.getEditUri(),
						null /* type */);
				serializeLink(serializer, "alternate" /* rel */, entry
						.getHtmlUri(), "text/html" /* type */);
			}
		}

		serializeSummary(serializer, entry.getSummary());

		serializeAuthor(serializer, entry.getAuthor(), entry.getEmail());

		if (format == FORMAT_FULL) {
			serializePublicationDate(serializer, entry.getPublicationDate());
		}

		serializeExtraEntryContents(serializer, format);
	}

	private void serializeContentType(XmlSerializer serializer, String tag,
			String contentType, String valueType)
			throws IllegalArgumentException, IllegalStateException, IOException {

		serializer.startTag("", tag);
		serializer.attribute("", "type", valueType);
		serializer.text(contentType);
		serializer.endTag("", tag);
	}

	/**
	 * Hook for subclasses to serialize extra fields within the entry.
	 * 
	 * @param serializer
	 *            The XmlSerializer being used to serialize the entry.
	 * @param format
	 *            The serialization format for the entry.
	 * @throws ParseException
	 *             Thrown if the entry cannot be serialized.
	 * @throws IOException
	 *             Thrown if the entry cannot be written to the underlying
	 *             {@link OutputStream}.
	 */
	protected void serializeExtraEntryContents(XmlSerializer serializer,
			int format) throws ParseException, IOException {
		// no-op in this class.
	}

	// TODO: make these helper methods protected so sublcasses can use them?

	private static void serializeId(XmlSerializer serializer, String id)
			throws IOException {
		if (StringUtil.isEmpty(id)) {
			return;
		}
		serializer.startTag(null /* ns */, "id");
		serializer.text(id);
		serializer.endTag(null /* ns */, "id");
	}

	private static void serializeTitle(XmlSerializer serializer, String title,
			int format) throws IOException {
		if (StringUtil.isEmpty(title)) {
			return;
		}
		serializer.startTag(null /* ns */, "title");
		if (format != 1) {
			serializer.attribute("", "type", "text");
		}
		serializer.text(title);
		serializer.endTag(null /* ns */, "title");
	}

	public static void serializeLink(XmlSerializer serializer, String rel,
			String href, String type) throws IOException {
		if (StringUtil.isEmpty(href)) {
			return;
		}
		serializer.startTag(null /* ns */, "link");
		serializer.attribute(null /* ns */, "rel", rel);
		if (!StringUtil.isEmpty(type))
			serializer.attribute(null /* ns */, "type", type);
		serializer.attribute(null /* ns */, "href", href);

		serializer.endTag(null /* ns */, "link");
	}

	private static void serializeSummary(XmlSerializer serializer,
			String summary) throws IOException {
		if (StringUtil.isEmpty(summary)) {
			return;
		}
		serializer.startTag(null /* ns */, "summary");
		serializer.text(summary);
		serializer.endTag(null /* ns */, "summary");
	}

	private static void serializeContent(XmlSerializer serializer,
			String content) throws IOException {
		if (content == null) {
			return;
		}
		serializer.startTag(null /* ns */, "content");
		serializer.attribute(null /* ns */, "type", "text");
		serializer.text(content);
		serializer.endTag(null /* ns */, "content");
	}

	private static void serializeAuthor(XmlSerializer serializer,
			String author, String email) throws IOException {
		if (StringUtil.isEmpty(author) || StringUtil.isEmpty(email)) {
			return;
		}
		serializer.startTag(null /* ns */, "author");
		serializer.startTag(null /* ns */, "name");
		serializer.text(author);
		serializer.endTag(null /* ns */, "name");
		serializer.startTag(null /* ns */, "email");
		serializer.text(email);
		serializer.endTag(null /* ns */, "email");
		serializer.endTag(null /* ns */, "author");
	}

	private static void serializeCategory(XmlSerializer serializer,
			String category, String categoryScheme, String label)
			throws IOException {
		if (StringUtil.isEmpty(category) && StringUtil.isEmpty(categoryScheme)) {
			return;
		}
		serializer.startTag(null /* ns */, "category");
		if (!StringUtil.isEmpty(categoryScheme)) {
			serializer.attribute(null /* ns */, "scheme", categoryScheme);
		}
		if (!StringUtil.isEmpty(category)) {
			serializer.attribute(null /* ns */, "term", category);
		}
		if (!StringUtil.isEmpty(label)) {
			serializer.attribute(null /* ns */, "label", label);
		}
		serializer.endTag(null /* ns */, "category");
	}

	private static void serializePublicationDate(XmlSerializer serializer,
			String publicationDate) throws IOException {
		if (StringUtil.isEmpty(publicationDate)) {
			return;
		}
		serializer.startTag(null /* ns */, "published");
		serializer.text(publicationDate);
		serializer.endTag(null /* ns */, "published");
	}

	private static void serializeUpdateDate(XmlSerializer serializer,
			String updateDate) throws IOException {
		if (StringUtil.isEmpty(updateDate)) {
			return;
		}
		serializer.startTag(null /* ns */, "updated");
		serializer.text(updateDate);
		serializer.endTag(null /* ns */, "updated");
	}
}
