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
package api.wireless.gdata.parser.xml;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.data.Feed;
import api.wireless.gdata.parser.GDataParser;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.common.base.StringUtil;
import api.wireless.gdata.util.common.base.XmlUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link GDataParser} that uses an {@link XmlPullParser} to parse a GData feed.
 */
// NOTE: we do not perform any validity checks on the XML.
public class XmlGDataParser implements GDataParser {

	/** Namespace URI for Atom */
	public static final String NAMESPACE_ATOM_URI =
		"http://www.w3.org/2005/Atom";

	public static final String NAMESPACE_OPENSEARCH = "openSearch";

	public static final String NAMESPACE_OPENSEARCH_URI = "http://a9.com/-/spec/opensearchrss/1.0/";

	/** Namespace prefix for GData */
	public static final String NAMESPACE_GD = "gd";

	/** Namespace URI for GData */
	public static final String NAMESPACE_GD_URI = "http://schemas.google.com/g/2005";

	/** Document label */
	public static final String DOC_LABEL = "document";
	
	/** Spreadsheet label */
	public static final String SPS_LABEL = "spreadsheet";
	
	/** Pdf label */
	public static final String PDF_LABEL = "pdf";
	
	/** Presentation label */
	public static final String PRS_LABEL = "presentation";
	
	/** File label */
	public static final String FILE_LABEL = "file";
	
	private final InputStream is;
	private final XmlPullParser parser;
	private boolean isInBadState;

	/**
	 * Creates a new XmlGDataParser for a feed in the provided InputStream.
	 * @param is The InputStream that should be parsed.
	 * @throws ParseException Thrown if an XmlPullParser could not be created
	 * or set around this InputStream.
	 */
	public XmlGDataParser(InputStream is, XmlPullParser parser)
	throws ParseException {
		this.is = is;
		this.parser = parser;
		this.isInBadState = false;
		if (this.is != null) {
			try {
				this.parser.setInput(is, null /* encoding */);
			} catch (XmlPullParserException e) {
				throw new ParseException("Could not create XmlGDataParser", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.wireless.gdata.parser.GDataParser#init()
	 */
	public final Feed init() throws ParseException {
		int eventType;
		try {
			eventType = parser.getEventType();
		} catch (XmlPullParserException e) {
			throw new ParseException("Could not parse GData feed.", e);
		}
		if (eventType != XmlPullParser.START_DOCUMENT) {
			throw new ParseException("Attempting to initialize parsing beyond "
					+ "the start of the document.");
		}		

		try {
			eventType = parser.next();
		} catch (XmlPullParserException xppe) {
			throw new ParseException("Could not read next event.", xppe);
		} catch (IOException ioe) {
			throw new ParseException("Could not read next event.", ioe);
		}
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				String name = parser.getName();
				if ("feed".equals(name)) {
					try {
						return parseFeed();
					} catch (XmlPullParserException xppe) {
						throw new ParseException("Unable to parse <feed>.",
								xppe);
					} catch (IOException ioe) {
						throw new ParseException("Unable to parse <feed>.",
								ioe);
					}
				}
				break;
			default:
				// ignore
				break;
			}

			try {
				eventType = parser.next();
			} catch (XmlPullParserException xppe) {
				throw new ParseException("Could not read next event.", xppe);
			} catch (IOException ioe) {
				throw new ParseException("Could not read next event." , ioe);
			}
		}
		throw new ParseException("No <feed> found in document.");
	}

	/**
	 * Returns the {@link XmlPullParser} being used to parse this feed.
	 */
	protected final XmlPullParser getParser() {
		return parser;
	}

	/**
	 * Creates a new {@link Feed} that should be filled with information about
	 * the feed that will be parsed.
	 * @return The {@link Feed} that should be filled.
	 */
	protected Feed createFeed() {
		return new Feed();
	}

	/**
	 * Creates a new {@link Entry} that should be filled with information about
	 * the entry that will be parsed.
	 * @return The {@link Entry} that should be filled.
	 */
	protected Entry createEntry() {
		return new Entry();
	}

	/**
	 * Parses the feed (but not any entries).
	 *
	 * @return A new {@link Feed} containing information about the feed.
	 * @throws XmlPullParserException Thrown if the XML document cannot be
	 * parsed.
	 * @throws IOException Thrown if the {@link InputStream} behind the feed
	 * cannot be read.
	 */
	private final Feed parseFeed() throws XmlPullParserException, IOException {
		Feed feed = createFeed();
		
		// Get ETAG attributes
		if (parser.getAttributeCount() > 0) {
			feed.setEtag(parser.getAttributeValue(NAMESPACE_GD_URI, "etag"));
		}
		
		// look for entries
		int eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				String name = parser.getName();
				if ("startIndex".equals(name)) {
					feed.setStartIndex(StringUtil.parseInt(
							XmlUtil.extractChildText(parser), 0));
				} else if ("title".equals(name)) {
					feed.setTitle(XmlUtil.extractChildText(parser));
				} else if ("id".equals(name)) {
					feed.setId(XmlUtil.extractChildText(parser));
				} else if ("updated".equals(name)) {
					feed.setLastUpdated(XmlUtil.extractChildText(parser));
				} else if ("category".equals(name)) {
					String category =
						parser.getAttributeValue(null /* ns */, "term");
					if (!StringUtil.isEmpty(category)) {
						feed.setCategory(category);
					}
					String categoryScheme =
						parser.getAttributeValue(null /* ns */, "scheme");
					if (!StringUtil.isEmpty(categoryScheme)) {
						feed.setCategoryScheme(categoryScheme);
					}
				} else if ("link".equals(name)) {
					String rel =
						parser.getAttributeValue(null /* ns */, "rel");
					String type =
						parser.getAttributeValue(null /* ns */, "type");
					String href =
						parser.getAttributeValue(null /* ns */, "href");
					if ("next".equals(rel)) {
						feed.setNext(href);					
					} 				
				} else if ("entry".equals(name)) {
					// stop parsing here.
					// TODO: pay attention to depth?
					return feed;
				} else {
					handleExtraElementInFeed(feed);
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		// if we get here, we have a feed with no entries.
		return feed;
	}

	/**
	 * Hook that allows extra (service-specific) elements in a &lt;feed&gt; to
	 * be parsed.
	 * @param feed The {@link Feed} being filled.
	 */
	protected void handleExtraElementInFeed(Feed feed)
	throws XmlPullParserException, IOException {
		// no-op in this class.
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.wireless.gdata.parser.GDataParser#hasMoreData()
	 */
	public boolean hasMoreData() {
		if (isInBadState) {
			return false;
		}
		try {
			int eventType = parser.getEventType();
			return (eventType != XmlPullParser.END_DOCUMENT);
		} catch (XmlPullParserException xppe) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.wireless.gdata.parser.GDataParser#readNextEntry
	 */
	public Entry readNextEntry(Entry entry) throws ParseException, IOException {
		if (!hasMoreData()) return null;

		int eventType;
		try {
			eventType = parser.getEventType();
		} catch (XmlPullParserException e) {
			throw new ParseException("Could not parse entry.", e);
		}

		if (eventType != XmlPullParser.START_TAG) {
			throw new ParseException("Expected event START_TAG: Actual event: "
					+ XmlPullParser.TYPES[eventType]);
		}

		String name = parser.getName();
		if (!"entry".equals(name)) {
			throw new ParseException("Expected <entry>: Actual element: "
					+ "<" + name + ">");
		}

		if (entry == null) {
			entry = createEntry();
		} else {
			entry.clear();
		}

		// This is version 3 protocol
		if (parser.getAttributeCount() > 0) {
			entry.setEtag(parser.getAttributeValue(NAMESPACE_GD_URI, "etag"));
		}

		try {
			parser.next();
			handleEntry(entry);
			entry.validate();
		} catch (ParseException xppe1) {
			try {
				if (hasMoreData()) skipToNextEntry();
			} catch (XmlPullParserException xppe2) {
				// squelch the error -- let the original one stand.
				// set isInBadState to ensure that the next call to hasMoreData() will return false.
				isInBadState = true;
			}
			throw new ParseException("Could not parse <entry>, " + entry, xppe1);
		} catch (XmlPullParserException xppe1) {
			try {
				if (hasMoreData()) skipToNextEntry();
			} catch (XmlPullParserException xppe2) {
				// squelch the error -- let the original one stand.
				// set isInBadState to ensure that the next call to hasMoreData() will return false.
				isInBadState = true;
			}
			throw new ParseException("Could not parse <entry>, " + entry, xppe1);
		}
		return entry;
	}

	/**
	 * Parses a GData entry.  You can either call {@link #init()} or
	 * {@link #parseStandaloneEntry()} for a given feed.
	 *
	 * @return The parsed entry.
	 * @throws ParseException Thrown if the entry could not be parsed.
	 */
	public Entry parseStandaloneEntry() throws ParseException, IOException {	  
		Entry entry = createEntry();

		int eventType;
		try {
			eventType = parser.getEventType();
		} catch (XmlPullParserException e) {
			throw new ParseException("Could not parse GData entry.", e);
		}
		if (eventType != XmlPullParser.START_DOCUMENT) {
			throw new ParseException("Attempting to initialize parsing beyond "
					+ "the start of the document.");
		}

		try {
			eventType = parser.next();
		} catch (XmlPullParserException xppe) {
			throw new ParseException("Could not read next event.", xppe);
		} catch (IOException ioe) {
			throw new ParseException("Could not read next event.", ioe);
		}

		// This is version 2 protocol
		if (parser.getAttributeCount() > 0) {
			entry.setEtag(parser.getAttributeValue(NAMESPACE_GD_URI, "etag"));
		}


		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				String name = parser.getName();
				if ("entry".equals(name)) {
					try {
						parser.next();
						handleEntry(entry);
						return entry;
					} catch (XmlPullParserException xppe) {
						throw new ParseException("Unable to parse <entry>.",
								xppe);
					} catch (IOException ioe) {
						throw new ParseException("Unable to parse <entry>.",
								ioe);
					}
				}
				break;
			default:
				// ignore
				break;
			}

			try {
				eventType = parser.next();
			} catch (XmlPullParserException xppe) {
				throw new ParseException("Could not read next event.", xppe);
			}
		}
		throw new ParseException("No <entry> found in document.");
	}

	/**
	 * Skips the rest of the current entry until the parser reaches the next entry, if any.
	 * Does nothing if the parser is already at the beginning of an entry.
	 */
	protected void skipToNextEntry() throws IOException, XmlPullParserException {
		if (!hasMoreData()) {
			throw new IllegalStateException("you shouldn't call this if hasMoreData() is false");
		}

		int eventType = parser.getEventType();

		// skip ahead until we reach an <entry> tag.
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("entry".equals(parser.getName())) {
					return;
				}
				break;
			}
			eventType = parser.next();
		}
	}

	/**
	 * Parses the current entry in the XML document.  Assumes that the parser
	 * is currently pointing just after an &lt;entry&gt;.
	 *
	 * @param entry The entry that will be filled.
	 * @throws XmlPullParserException Thrown if the XML cannot be parsed.
	 * @throws IOException Thrown if the underlying inputstream cannot be read.
	 */
	protected void handleEntry(Entry entry)
	throws XmlPullParserException, IOException, ParseException {
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				// TODO: make sure these elements are at the expected depth.
				String name = parser.getName();
				if ("entry".equals(name)) {
					// stop parsing here.
					return;
				} else if ("id".equals(name)) {
					entry.setId(XmlUtil.extractChildText(parser));
				} else if ("title".equals(name)) {
					entry.setTitle(XmlUtil.extractChildText(parser));
				} else if ("link".equals(name)) {
					String rel =
						parser.getAttributeValue(null /* ns */, "rel");
					String type =
						parser.getAttributeValue(null /* ns */, "type");
					String href =
						parser.getAttributeValue(null /* ns */, "href");
					if ("edit".equals(rel)) {
						entry.setEditUri(href);
					} else if (("alternate").equals(rel) && ("text/html".equals(type))) {
						entry.setHtmlUri(href);
					} else {
						handleExtraLinkInEntry(rel,
								type,
								href,
								entry);
					}
				} else if ("summary".equals(name)) {
					entry.setSummary(XmlUtil.extractChildText(parser));
				} else if ("content".equals(name)) {
					entry.setContentType(parser.getAttributeValue(null /* ns */, "type"));  
					entry.setContent(parser.getAttributeValue(null /* ns */, "src"));
				} else if ("author".equals(name)) {
					handleAuthor(entry);
				} else if ("category".equals(name)) {
					String category =
						parser.getAttributeValue(null /* ns */, "term");
					if (category != null && category.length() > 0) {
						entry.setCategory(category);
					}
					String categoryScheme =
						parser.getAttributeValue(null /* ns */, "scheme");
					if (categoryScheme != null && category.length() > 0) {
						entry.setCategoryScheme(categoryScheme);
					}
					String label =
						parser.getAttributeValue(null /* ns */, "label");
					if (label != null) {
						if ("starred".equals(label))
							entry.setStarred(true);
						else if (DOC_LABEL.equals(label))
							entry.setLabel(DOC_LABEL);
						else if (SPS_LABEL.equals(label))
							entry.setLabel(SPS_LABEL);
						else if (PDF_LABEL.equals(label))
							entry.setLabel(PDF_LABEL);
						else if (PRS_LABEL.equals(label)){
							entry.setLabel(PRS_LABEL);
							entry.setMime(ContentType.PDF.toString());
						} else if (category.indexOf(FILE_LABEL)>=0){
							entry.setLabel(FILE_LABEL);
							entry.setMime(label);
						}
					}
				} else if ("published".equals(name)) {
					entry.setPublicationDate(
							XmlUtil.extractChildText(parser));
				} else if ("updated".equals(name)) {
					entry.setUpdateDate(XmlUtil.extractChildText(parser));
				} else if ("deleted".equals(name)) {
					entry.setDeleted(true);
				} else {
					handleExtraElementInEntry(entry);
				}
				break;
			default:
				break;
			}

			eventType = parser.next();
		}
	}

	private void handleAuthor(Entry entry)
	throws XmlPullParserException, IOException {

		int eventType = parser.getEventType();
		String name = parser.getName();

		if (eventType != XmlPullParser.START_TAG ||
				(!"author".equals(parser.getName()))) {
			// should not happen.
			throw new
			IllegalStateException("Expected <author>: Actual element: <"
					+ parser.getName() + ">");
		}

		eventType = parser.next();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if ("name".equals(name)) {
					String authorName = XmlUtil.extractChildText(parser);
					entry.setAuthor(authorName);
				} else if ("email".equals(name)) {
					String email = XmlUtil.extractChildText(parser);
					entry.setEmail(email);
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if ("author".equals(name)) {
					return;
				}
			default:
				// ignore
			}

			eventType = parser.next();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.wireless.gdata.parser.GDataParser#close()
	 */
	public void close() {
		if (is != null) {
			try {
				is.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	/**
	 * Hook that allows extra (service-specific) elements in an &lt;entry&gt;
	 * to be parsed.
	 * @param entry The {@link Entry} being filled.
	 */
	protected void handleExtraElementInEntry(Entry entry)
	throws XmlPullParserException, IOException, ParseException {
		// no-op in this class.
	}

	/**
	 * Hook that allows extra (service-specific) &lt;link&gt;s in an entry to be
	 * parsed.
	 * @param rel The rel attribute value.
	 * @param type The type attribute value.
	 * @param href The href attribute value.
	 * @param entry The {@link Entry} being filled.
	 */
	protected void handleExtraLinkInEntry(String rel,
			String type,
			String href,
			Entry entry)
	throws XmlPullParserException, IOException {
		// no-op in this class.
	}
}
