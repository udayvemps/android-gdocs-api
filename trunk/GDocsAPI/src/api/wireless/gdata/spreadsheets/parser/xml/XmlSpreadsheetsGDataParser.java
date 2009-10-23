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
package api.wireless.gdata.spreadsheets.parser.xml;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.data.Feed;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlGDataParser;
import api.wireless.gdata.spreadsheets.data.SpreadsheetEntry;
import api.wireless.gdata.spreadsheets.data.SpreadsheetFeed;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser helper for non-Atom data in a GData Spreadsheets meta-feed.
 */
public class XmlSpreadsheetsGDataParser extends XmlGDataParser {
    /**
     * The rel ID used by the server to identify the URLs for the worksheets
     * feed
     */
    protected static final String WORKSHEET_FEED_REL =
            "http://schemas.google.com/spreadsheets/2006#worksheetsfeed";

    /**
     * Creates a new XmlSpreadsheetsGDataParser.
     * 
     * @param is the stream from which to read the data
     * @param xmlParser the XmlPullParser to use to parse the raw XML
     * @throws ParseException if the super-class throws one
     */
    public XmlSpreadsheetsGDataParser(InputStream is, XmlPullParser xmlParser)
            throws ParseException {
        super(is, xmlParser);
    }

    /* (non-JavaDoc)
     * Creates a new Entry that can handle the data parsed by this class.
     */
    protected Entry createEntry() {
        return new SpreadsheetEntry();
    }

    /* (non-JavaDoc)
     * Creates a new Feed that can handle the data parsed by this class.
     */
    protected Feed createFeed() {
        return new SpreadsheetFeed();
    }

    /* (non-JavaDoc)
     * Callback to handle link elements that are not recognized as Atom links.
     * Used to pick out the link tag in a Spreadsheet's entry that corresponds
     * to that spreadsheet's worksheets meta-feed.
     */
    protected void handleExtraLinkInEntry(String rel, String type, String href,
            Entry entry) throws XmlPullParserException, IOException {
        super.handleExtraLinkInEntry(rel, type, href, entry);
        if (WORKSHEET_FEED_REL.equals(rel)
                && "application/atom+xml".equals(type)) {
            SpreadsheetEntry sheet = (SpreadsheetEntry) entry;
            sheet.setWorksheetFeedUri(href);
        }
    }
}
