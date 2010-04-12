package api.wireless.gdata.spreadsheets.serializer.xml;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.parser.xml.XmlParserFactory;
import api.wireless.gdata.serializer.xml.XmlEntryGDataSerializer;

public class XmlSpreadsheetEntryGDataSerializer extends XmlEntryGDataSerializer{

	public XmlSpreadsheetEntryGDataSerializer(XmlParserFactory factory, Entry entry) {
		super(factory, entry);
		
	}
}
