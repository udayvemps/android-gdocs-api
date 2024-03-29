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

import api.wireless.gdata.client.GDataParserFactory;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.parser.GDataParser;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlParserFactory;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.spreadsheets.data.CellEntry;
import api.wireless.gdata.spreadsheets.data.ListEntry;
import api.wireless.gdata.spreadsheets.data.SpreadsheetEntry;
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
import api.wireless.gdata.spreadsheets.serializer.xml.XmlCellEntryGDataSerializer;
import api.wireless.gdata.spreadsheets.serializer.xml.XmlListEntryGDataSerializer;
import api.wireless.gdata.spreadsheets.serializer.xml.XmlSpreadsheetEntryGDataSerializer;
import api.wireless.gdata.spreadsheets.serializer.xml.XmlWorksheetEntryGDataSerializer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;

/**
 * A GDataParserFactory capable of handling Spreadsheets.
 */
public class XmlSpreadsheetsGDataParserFactory implements GDataParserFactory {
    /*
     * @see GDataParserFactory
     */
    public XmlSpreadsheetsGDataParserFactory(XmlParserFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
    }

    /** Intentionally private. */
    private XmlSpreadsheetsGDataParserFactory() {
    }

    /*
     * Creates a parser for the indicated feed, assuming the default feed type.
     * The default type is specified on {@link SpreadsheetsClient#DEFAULT_FEED}.
     * 
     * @param is The stream containing the feed to be parsed.
     * @return A GDataParser capable of parsing the feed as the default type.
     * @throws ParseException if the feed could not be parsed for any reason
     */
    public GDataParser createParser(InputStream is) throws ParseException {
        // attempt a default
        return createParser(SpreadsheetEntry.class, is);
    }

    /*
     * Creates a parser of the indicated type for the indicated feed.
     * 
     * @param feedType The type of the feed; must be one of the constants on
     *        {@link SpreadsheetsClient}.
     * @return A parser capable of parsing the feed as the indicated type.
     * @throws ParseException if the feed could not be parsed for any reason
     */
    public GDataParser createParser(Class entryClass, InputStream is)
            throws ParseException {
        try {
            XmlPullParser xmlParser = xmlFactory.createParser();
            if (entryClass == SpreadsheetEntry.class) {
                return new XmlSpreadsheetsGDataParser(is, xmlParser);
            } else if (entryClass == WorksheetEntry.class) {
                return new XmlWorksheetsGDataParser(is, xmlParser);
            } else if (entryClass == CellEntry.class) {
                return new XmlCellsGDataParser(is, xmlParser);
            } else if (entryClass == ListEntry.class) {
                return new XmlListGDataParser(is, xmlParser);
            } else {
                throw new ParseException("Unrecognized feed requested.");
            }
        } catch (XmlPullParserException e) {
            throw new ParseException("Failed to create parser", e);
        }
    }

    /*
     * Creates a serializer capable of handling the indicated entry.
     * 
     * @param The Entry to be serialized to an XML string.
     * @return A GDataSerializer capable of handling the indicated entry.
     * @throws IllegalArgumentException if Entry is not a supported type (which
     *         currently includes only {@link ListEntry} and {@link CellEntry}.)
     */
    public GDataSerializer createSerializer(Entry entry) {
        if (entry instanceof ListEntry) {
            return new XmlListEntryGDataSerializer(xmlFactory, entry);
        } else if (entry instanceof CellEntry) {
            return new XmlCellEntryGDataSerializer(xmlFactory, entry);
        } else if (entry instanceof WorksheetEntry) {
            return new XmlWorksheetEntryGDataSerializer(xmlFactory, entry);
        } else if (entry instanceof SpreadsheetEntry){
        	return new XmlSpreadsheetEntryGDataSerializer(xmlFactory, entry);
        } else {
            throw new IllegalArgumentException(
                    "Expected a ListEntry or CellEntry");
        }
    }

    /** The XmlParserFactory to use to actually process XML streams. */
    private XmlParserFactory xmlFactory;
}
