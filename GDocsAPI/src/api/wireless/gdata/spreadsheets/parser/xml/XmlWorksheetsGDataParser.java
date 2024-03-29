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
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
import api.wireless.gdata.spreadsheets.data.WorksheetFeed;
import api.wireless.gdata.util.common.base.StringUtil;
import api.wireless.gdata.util.common.base.XmlUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser helper for non-Atom data in a GData Spreadsheets Worksheets meta-feed.
 */
public class XmlWorksheetsGDataParser extends XmlGDataParser {
    /**
     * The rel ID used by the server to identify the cells feed for a worksheet
     */
    protected static final String CELLS_FEED_REL =
            "http://schemas.google.com/spreadsheets/2006#cellsfeed";

    /**
     * The rel ID used by the server to identify the list feed for a worksheet
     */
    protected static final String LIST_FEED_REL =
            "http://schemas.google.com/spreadsheets/2006#listfeed";

    /**
     * Creates a new XmlWorksheetsGDataParser.
     * 
     * @param is the stream from which to read the data
     * @param xmlParser the XmlPullParser to use to parse the raw XML
     * @throws ParseException if the super-class throws one
     */
    public XmlWorksheetsGDataParser(InputStream is, XmlPullParser xmlParser)
            throws ParseException {
        super(is, xmlParser);
    }

    /* (non-JavaDoc)
     * Creates a new Entry that can handle the data parsed by this class.
     */
    protected Entry createEntry() {
        return new WorksheetEntry();
    }

    /* (non-JavaDoc)
     * Creates a new Feed that can handle the data parsed by this class.
     */
    protected Feed createFeed() {
        return new WorksheetFeed();
    }

    /* (non-JavaDoc)
     * Callback to handle non-Atom data present in an Atom entry tag.
     */
    protected void handleExtraElementInEntry(Entry entry)
            throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        if (!(entry instanceof WorksheetEntry)) {
            throw new IllegalArgumentException("Expected WorksheetEntry!");
        }
        WorksheetEntry worksheet = (WorksheetEntry) entry;

        // the only custom elements are rowCount and colCount
        String name = parser.getName();
        if ("rowCount".equals(name)) {
            worksheet.setRowCount(StringUtil.parseInt(XmlUtil
                    .extractChildText(parser), 0));
        } else if ("colCount".equals(name)) {
            worksheet.setColCount(StringUtil.parseInt(XmlUtil
                    .extractChildText(parser), 0));
        }
    }

    /* (non-JavaDoc)
     * Callback to handle non-Atom links present in an Atom entry tag. Used to
     * pick out a worksheet's cells and list feeds.
     */
    protected void handleExtraLinkInEntry(String rel, String type, String href,
            Entry entry) throws XmlPullParserException, IOException {
        if (LIST_FEED_REL.equals(rel) && "application/atom+xml".equals(type)) {
            WorksheetEntry sheet = (WorksheetEntry) entry;
            sheet.setListFeedUri(href);
        } else if (CELLS_FEED_REL.equals(rel)
                && "application/atom+xml".equals(type)) {
            WorksheetEntry sheet = (WorksheetEntry) entry;
            sheet.setCellFeedUri(href);
        }  else if("self".equals(rel) && "application/atom+xml".equals(type)) {
        	WorksheetEntry sheet = (WorksheetEntry) entry;
            sheet.setSelfUri(href);
        }
    }
}
