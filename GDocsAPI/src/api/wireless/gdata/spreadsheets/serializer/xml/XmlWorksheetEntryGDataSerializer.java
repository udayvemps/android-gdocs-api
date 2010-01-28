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
package api.wireless.gdata.spreadsheets.serializer.xml;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlParserFactory;
import api.wireless.gdata.serializer.xml.XmlEntryGDataSerializer;
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * A serializer for handling GData Spreadsheets Cell entries.
 */
public class XmlWorksheetEntryGDataSerializer extends XmlEntryGDataSerializer {
    public static final String NAMESPACE_GS = "gs";
    public static final String NAMESPACE_GS_URI =
            "http://schemas.google.com/spreadsheets/2006";

    /**
     * Creates a new XmlCellEntryGDataSerializer.
     * 
     * @param entry the entry to be serialized
     */
    public XmlWorksheetEntryGDataSerializer(XmlParserFactory xmlFactory,
            Entry entry) {
        super(xmlFactory, entry);
    }

    /**
     * Sets up the GData Cell namespace.
     * 
     * @param serializer the serializer to use
     */
    protected void declareExtraEntryNamespaces(XmlSerializer serializer)
            throws IOException {
        serializer.setPrefix(NAMESPACE_GS, NAMESPACE_GS_URI);
    }

    /*
     * Handles the non-Atom data belonging to the GData Spreadsheets Cell
     * namespace.
     * 
     * @param serializer the XML serializer to use
     * @param format unused
     * @throws ParseException if the data could not be serialized
     * @throws IOException on network error
     */
    protected void serializeExtraEntryContents(XmlSerializer serializer,
            int format) throws ParseException, IOException {
        WorksheetEntry entry = (WorksheetEntry) getEntry();
        int calCount = entry.getColCount();
        int rowCount = entry.getRowCount();
        String title = entry.getTitle();
 
        addRowCountTag(serializer, rowCount);
        addColCountTag(serializer, calCount);
        
    }

	private void addColCountTag(XmlSerializer serializer, int calCount)
			throws IOException {
		serializer.setPrefix("gs", "http://schemas.google.com/spreadsheets/2006");
        serializer.startTag(NAMESPACE_GS_URI, "colCount");
        serializer.text(Integer.toString(calCount));
        serializer.endTag(NAMESPACE_GS_URI, "colCount");
	}

	private void addRowCountTag(XmlSerializer serializer, int rowCount)
			throws IOException {
		serializer.setPrefix("gs", "http://schemas.google.com/spreadsheets/2006");
        serializer.startTag(NAMESPACE_GS_URI, "rowCount");
        serializer.text(Integer.toString(rowCount));
        serializer.endTag(NAMESPACE_GS_URI, "rowCount");
	}
}
