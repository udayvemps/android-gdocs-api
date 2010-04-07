package api.wireless.gdata;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import api.wireless.BufferOutputStream;
import api.wireless.StringInputStream;
import api.wireless.TestUtils;
import api.wireless.gdata.client.AbstructParserFactory;
import api.wireless.gdata.data.Feed;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.spreadsheets.data.CellEntry;
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
import api.wireless.gdata.spreadsheets.parser.xml.XmlCellsGDataParser;
import api.wireless.gdata.spreadsheets.parser.xml.XmlWorksheetsGDataParser;
import api.wireless.gdata.spreadsheets.serializer.xml.XmlCellEntryGDataSerializer;
import junit.framework.TestCase;

public class CellEntryTest extends TestCase {
	
	public static String toSerialize = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>" + 
		"<entry xmlns=\"http://www.w3.org/2005/Atom\"" + 
		"xmlns:gd=\"http://schemas.google.com/g/2005\"" +
		"xmlns:gs=\"http://schemas.google.com/spreadsheets/2006\">" + 
		"<gs:cell row=\"2\" col=\"1\">value</gs:cell></entry>";
	
	public static String toParse = "<?xml version='1.0' encoding='UTF-8'?>" +
		"<feed xmlns='http://www.w3.org/2005/Atom' " + 
		"xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/' " + 
		"xmlns:gs='http://schemas.google.com/spreadsheets/2006' " + 
		"xmlns:batch='http://schemas.google.com/gdata/batch'>" + 
		"<id>http://spreadsheets.google.com/feeds/cells/tNt2CZ7DEkE0G3CREkCq7cQ/od6/private/full</id>" + 
		"<updated>2010-04-07T19:14:07.026Z</updated>" + 
		"<category scheme='http://schemas.google.com/spreadsheets/2006' " + 
		"term='http://schemas.google.com/spreadsheets/2006#cell'/>" + 
		"<title type='text'>Sheet1</title><link rel='alternate' type='text/html' " + 
		"href='http://spreadsheets.google.com/ccc?key=tNt2CZ7DEkE0G3CREkCq7cQ'/>" + 
		"<link rel='http://schemas.google.com/g/2005#feed' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/cells/tNt2CZ7DEkE0G3CREkCq7cQ/od6/private/full'/>" + 
		"<link rel='http://schemas.google.com/g/2005#post' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/cells/tNt2CZ7DEkE0G3CREkCq7cQ/od6/private/full'/>" +
		"<link rel='http://schemas.google.com/g/2005#batch' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/cells/tNt2CZ7DEkE0G3CREkCq7cQ/od6/private/full/batch'/>" + 
		"<link rel='self' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/cells/tNt2CZ7DEkE0G3CREkCq7cQ/od6/private/full'/><author>" + 
		"<name>buurdb</name><email>buurdb@gmail.com</email></author>" + 
		"<openSearch:totalResults>3</openSearch:totalResults>" + 
		"<openSearch:startIndex>1</openSearch:startIndex><gs:rowCount>100</gs:rowCount>" + 
		"<gs:colCount>20</gs:colCount><entry>" + 
		"<id>http://spreadsheets.google.com/feeds/cells/tNt2CZ7DEkE0G3CREkCq7cQ/od6/private/full/R1C1</id>" + 
		"<updated>2010-04-07T19:14:07.026Z</updated>" + 
		"<category scheme='http://schemas.google.com/spreadsheets/2006' " + 
		"term='http://schemas.google.com/spreadsheets/2006#cell'/>" + 
		"<title type='text'>A1</title><content type='text'>2</content>" + 
		"<link rel='self' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/cells/tNt2CZ7DEkE0G3CREkCq7cQ/od6/private/full/R1C1'/>" + 
		"<link rel='edit' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/cells/tNt2CZ7DEkE0G3CREkCq7cQ/od6/private/full/R1C1/1e'/>" + 
		"<gs:cell row='1' col='2' inputValue='3' numericValue='3.0'>3</gs:cell></entry>" +
		"</feed>";
	
	
	public void testSerializeEntry(){
		CellEntry entry = new CellEntry();
		entry.setCol(1);
		entry.setRow(2);
		entry.setValue("value");
		
		BufferOutputStream bos = new BufferOutputStream();
		XmlCellEntryGDataSerializer serializer = new XmlCellEntryGDataSerializer(new AbstructParserFactory(), entry);
		
		String actual = "";
		try {
			serializer.serialize(bos, 1);
			actual = new String(bos.getData());
		} catch (ParseException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		TestUtils util = new TestUtils();
		toSerialize = util.clearWhiteSpaces(toSerialize);
		actual = util.clearWhiteSpaces(actual);
		assertEquals(-1, util.findDifference(actual, toSerialize));
	}
	
	public void testParseCellEntry() throws XmlPullParserException, ParseException, IOException{
		InputStream inputStream = new StringInputStream(toParse);
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser rawParser = factory.newPullParser(); 
		XmlCellsGDataParser parser = new XmlCellsGDataParser(inputStream, rawParser);
		Feed feed = parser.init();
		CellEntry entry = null;
		while(parser.hasMoreData()){
			entry = (CellEntry)parser.readNextEntry(entry);
		}
		
		assertEquals(1, 
					entry.getRow());
		assertEquals(2,
					entry.getCol());
		assertEquals("3",
					entry.getValue());
	}

}
