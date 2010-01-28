package api.wireless.gdata;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import api.wireless.gdata.client.GDataParserFactory;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.parser.GDataParser;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlGDataParser;
import api.wireless.gdata.parser.xml.XmlParserFactory;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.spreadsheets.parser.xml.XmlSpreadsheetsGDataParser;
import api.wireless.gdata.spreadsheets.serializer.xml.XmlWorksheetEntryGDataSerializer;

public class MockXmlParserFactory implements GDataParserFactory {

	private XmlParserFactory xmlFactory;
	
	public MockXmlParserFactory(XmlParserFactory xmlFactory) {
        this.xmlFactory = xmlFactory;
	}
	
	public GDataParser createParser(Class entryClass, InputStream is)
			throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	public GDataParser createParser(InputStream is) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	public GDataSerializer createSerializer(Entry entry) {
		return new XmlWorksheetEntryGDataSerializer(xmlFactory, entry);
	}

	
}
