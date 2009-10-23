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


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.docs.data.DocumentEntry;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlGDataParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser helper for non-Atom data in a GData Document meta-feed.
 */
public class XmlDocumentGDataParser extends XmlDocumentListGDataParser {
    /**
     * The rel ID used by the server to identify the URLs for the document feed
     */
    protected static final String DOCUMENT_EDIT_MEDIA_REL = "edit-media";

    /**
     * Creates a new XmlSpreadsheetsGDataParser.
     * 
     * @param is the stream from which to read the data
     * @param xmlParser the XmlPullParser to use to parse the raw XML
     * @throws ParseException if the super-class throws one
     */
    public XmlDocumentGDataParser(InputStream is, XmlPullParser xmlParser)
            throws ParseException {
        super(is, xmlParser);
    }

    /* (non-JavaDoc)
     * Creates a new Entry that can handle the data parsed by this class.
     */
    @Override
	protected Entry createEntry() {
        return new DocumentEntry();
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
        if (DOCUMENT_EDIT_MEDIA_REL.equals(rel)
                && "text/html".equals(type)) {
        	DocumentEntry doc = (DocumentEntry) entry;
            doc.setEditMediaUri(href);
        }
    }
}
