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
package api.wireless.gdata.docs.parser.xml;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.docs.data.DocumentListEntry;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlGDataParser;
import api.wireless.gdata.util.common.base.XmlUtil;


public class XmlDocumentListGDataParser extends XmlGDataParser  {

	protected static final String DOCUMENT_SELF_REL = "self";

	public XmlDocumentListGDataParser(InputStream is, XmlPullParser parser)
	throws ParseException {
		super(is, parser);
	}

	@Override
	protected Entry createEntry() {
		return new DocumentListEntry();
	}

	@Override
	protected void handleExtraElementInEntry(Entry entry)
		throws XmlPullParserException, IOException, ParseException {
		super.handleExtraElementInEntry(entry);
		
		String name = getParser().getName();
		
		if("resourceId".equals(name)){
			String resourceId = XmlUtil.extractChildText(getParser());
			DocumentListEntry doc = (DocumentListEntry) entry;
			doc.setResourceId(resourceId);
		}	
		
	}

	/* (non-JavaDoc)
	 * Callback to handle link elements that are not recognized as Atom links.
	 * Used to pick out the link tag in a Document's entry that corresponds
	 * to that document's meta-feed.
	 */
	@Override
	protected void handleExtraLinkInEntry(String rel, String type, String href,
			Entry entry) throws XmlPullParserException, IOException {
		super.handleExtraLinkInEntry(rel, type, href, entry);
		if (DOCUMENT_SELF_REL.equals(rel)
				&& "application/atom+xml".equals(type)) {
			DocumentListEntry doc = (DocumentListEntry) entry;
			doc.setSelfUri(href);
		}
		
		if (DocumentListEntry.PARENT_NAMESPACE.equals(rel)
				&& "application/atom+xml".equals(type)) {
			DocumentListEntry doc = (DocumentListEntry) entry;
			doc.setParent(DocumentListEntry.getKeyfromID(href));
		}		
	}

}
