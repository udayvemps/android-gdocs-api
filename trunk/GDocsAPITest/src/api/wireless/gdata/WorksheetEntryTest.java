package api.wireless.gdata;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import api.wireless.gdata.client.AbstructParserFactory;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.data.Feed;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
import api.wireless.gdata.spreadsheets.parser.xml.XmlWorksheetsGDataParser;
import api.wireless.gdata.spreadsheets.serializer.xml.XmlWorksheetEntryGDataSerializer;
import junit.framework.TestCase;

public class WorksheetEntryTest extends TestCase {

	public static String createWorksheetEntry = 
		"<?xml version='1.0' encoding='UTF-8' standalone='no' ?>" + 
		"<entry xmlns=\"http://www.w3.org/2005/Atom\" " + 
		"xmlns:gd=\"http://schemas.google.com/g/2005\" " +
		"xmlns:gs=\"http://schemas.google.com/spreadsheets/2006\">" +
		"<title>Title</title>" + 
		"<gs:rowCount>50</gs:rowCount>" +
		"<gs:colCount>50</gs:colCount>" +
		"</entry>";
	
	public static String updateWorksheetEntry =  
		"<?xml version='1.0' encoding='UTF-8' standalone='no' ?>" + 
		"<entry xmlns=\"http://www.w3.org/2005/Atom\" " + 
		"xmlns:gd=\"http://schemas.google.com/g/2005\" " +
		"xmlns:gs=\"http://schemas.google.com/spreadsheets/2006\">" +
		"<id>http://spreadsheets.google.com/feeds/worksheets/key/private/full/worksheetId</id>" + 
		"<updated>2007-07-30T18:51:30.666Z</updated>" + 
		"<category scheme=\"http://schemas.google.com/spreadsheets/2006\" "+
		"term=\"http://schemas.google.com/spreadsheets/2006#worksheet\"/> " +
		"<title type=\"text\">SomeTitle</title>" + 
		"<content type=\"text\">SomeContent</content>" +
		"<link rel=\"http://schemas.google.com/spreadsheets/2006#listfeed\" " +
		"type=\"application/atom+xml\" href=\"http://spreadsheets.google.com/feeds/list/key/worksheetId/private/full\"/>" + 
		"<link rel=\"http://schemas.google.com/spreadsheets/2006#cellsfeed\" " + 
		"type=\"application/atom+xml\" href=\"http://spreadsheets.google.com/feeds/cells/t9Vty0Ub77fApMbtxDFY7KA/od6/private/full\"/> " + 
		"<link rel=\"self\" type=\"application/atom+xml\" "+
		"href=\"http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full\"/>" +
		"<link rel=\"edit\" type=\"application/atom+xml\" " +
		"href=\"http://spreadsheets.google.com/feeds/worksheets/key/private/full/worksheetId/version\"/> "+
		"<gs:rowCount>45</gs:rowCount>" +
		"<gs:colCount>15</gs:colCount>" +
		"</entry>";
	
	public static String getWorksheetEntry = 
		"<?xml version='1.0' encoding='UTF-8'?>" + 
		"<feed xmlns='http://www.w3.org/2005/Atom' " + 
		"xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/' " + 
		"xmlns:gs='http://schemas.google.com/spreadsheets/2006'>" +
		"<id>http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full</id>" + 
		"<updated>2010-02-20T21:01:52.210Z</updated>" + 
		"<category scheme='http://schemas.google.com/spreadsheets/2006' " +
		"term='http://schemas.google.com/spreadsheets/2006#worksheet'/> " + 
		"<title type='text'>Testlabel1266699411926</title>" + 
		"<link rel='alternate' type='text/html' " + 
		"href='http://spreadsheets.google.com/ccc?key=t9Vty0Ub77fApMbtxDFY7KA'/>" + 
		"<link rel='http://schemas.google.com/g/2005#feed' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full'/>" + 
		"<link rel='http://schemas.google.com/g/2005#post' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full'/>" + 
		"<link rel='self' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full'/>" + 
		"<author><name>buurdb</name><email>buurdb@gmail.com</email></author>" + 
		"<openSearch:totalResults>2</openSearch:totalResults>" +
		"<openSearch:startIndex>1</openSearch:startIndex>" + 
		"<entry><id>http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full/od6</id>" + 
		"<updated>2010-02-20T21:01:52.210Z</updated>" + 
		"<category scheme='http://schemas.google.com/spreadsheets/2006' " + 
		"term='http://schemas.google.com/spreadsheets/2006#worksheet'/>" + 
		"<title type='text'>Sheet 1</title><content type='text'>Sheet 1</content>" + 
		"<link rel='http://schemas.google.com/spreadsheets/2006#listfeed' type='application/atom+xml' " +
		"href='http://spreadsheets.google.com/feeds/list/t9Vty0Ub77fApMbtxDFY7KA/od6/private/full'/>" + 
		"<link rel='http://schemas.google.com/spreadsheets/2006#cellsfeed' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/cells/t9Vty0Ub77fApMbtxDFY7KA/od6/private/full'/>" + 
		"<link rel='http://schemas.google.com/visualization/2008#visualizationApi' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/tq?key=t9Vty0Ub77fApMbtxDFY7KA&amp;sheet=od6'/>" + 
		"<link rel='self' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full/od6'/>" + 
		"<link rel='edit' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full/od6/ch09tbfkgh'/>" + 
		"<gs:rowCount>100</gs:rowCount><gs:colCount>20</gs:colCount></entry>" + 
		"<entry><id>http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full/od7</id>" + 
		"<updated>2010-02-20T21:01:52.210Z</updated>" + 
		"<category scheme='http://schemas.google.com/spreadsheets/2006' " + 
		"term='http://schemas.google.com/spreadsheets/2006#worksheet'/>" + 
		"<title type='text'>Title</title><content type='text'>Title</content>" + 
		"<link rel='http://schemas.google.com/spreadsheets/2006#listfeed' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/list/t9Vty0Ub77fApMbtxDFY7KA/od7/private/full'/>" + 
		"<link rel='http://schemas.google.com/spreadsheets/2006#cellsfeed' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/cells/t9Vty0Ub77fApMbtxDFY7KA/od7/private/full'/>" + 
		"<link rel='http://schemas.google.com/visualization/2008#visualizationApi' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/tq?key=t9Vty0Ub77fApMbtxDFY7KA&amp;sheet=od7'/>" + 
		"<link rel='self' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full/od7'/>" + 
		"<link rel='edit' type='application/atom+xml' " + 
		"href='http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full/od7/0'/>" + 
		"<gs:rowCount>5</gs:rowCount><gs:colCount>5</gs:colCount></entry></feed>";


	
	public void testCreateWorkSheetEntry(){
		WorksheetEntry entry = new WorksheetEntry();
		entry.setTitle("Title");
		entry.setRowCount(50);
		entry.setColCount(50);
		
		BufferOutputStream bos = new BufferOutputStream();
		XmlWorksheetEntryGDataSerializer serializer = new XmlWorksheetEntryGDataSerializer(new AbstructParserFactory(), entry);
		
		String actual = "";
		try {
			serializer.serialize(bos, 1);
			actual = new String(bos.getData());
		} catch (ParseException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		createWorksheetEntry = clearWhiteSpaces(createWorksheetEntry);
		actual = clearWhiteSpaces(actual);
		assertEquals(-1, findDifference(actual, createWorksheetEntry));
	}
	
	public void testUpdateWorksheetEntry(){
		WorksheetEntry entry = new WorksheetEntry();
		entry.setId("http://spreadsheets.google.com/feeds/worksheets/key/private/full/worksheetId");
		entry.setTitle("SomeTitle");
		entry.setRowCount(45);
		entry.setColCount(15);
		entry.setContent("SomeContent");
		entry.setUpdateDate("2007-07-30T18:51:30.666Z");
		entry.setCategory("http://schemas.google.com/spreadsheets/2006#worksheet");
		entry.setCategoryScheme("http://schemas.google.com/spreadsheets/2006");
		entry.setEditUri("http://spreadsheets.google.com/feeds/worksheets/key/private/full/worksheetId/version");
		entry.setHtmlUri("http://spreadsheets.google.com/feeds/cells/key/worksheetId/private/full");
		entry.setListFeedUri("http://spreadsheets.google.com/feeds/list/key/worksheetId/private/full");
		entry.setCellFeedUri("http://spreadsheets.google.com/feeds/cells/t9Vty0Ub77fApMbtxDFY7KA/od6/private/full");
		entry.setSelfUri("http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full");
		BufferOutputStream bos = new BufferOutputStream();
		XmlWorksheetEntryGDataSerializer serializer = new XmlWorksheetEntryGDataSerializer(new AbstructParserFactory(), entry);
		
		String actual = "";
		try {
			serializer.serialize(bos, 2);
			actual = new String(bos.getData());
		} catch (ParseException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		updateWorksheetEntry = clearWhiteSpaces(updateWorksheetEntry);
		actual = clearWhiteSpaces(actual);
		assertEquals(-1, findDifference(actual, updateWorksheetEntry));

	}
	
	public void testGetWorksheetEntry() throws XmlPullParserException, ParseException, IOException{
		InputStream inputStream = new StringInputStream(getWorksheetEntry);
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser rawParser = factory.newPullParser(); 
		XmlWorksheetsGDataParser parser = new XmlWorksheetsGDataParser(inputStream, rawParser);
		Feed feed = parser.init();
		WorksheetEntry entry = null;
		while(parser.hasMoreData()){
			entry = (WorksheetEntry)parser.readNextEntry(entry);
			if(entry.getTitle().equals("Title")){
				break;
			}
		}
		
		assertEquals("http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full/od7", 
					entry.getId());
		assertEquals("http://spreadsheets.google.com/feeds/cells/t9Vty0Ub77fApMbtxDFY7KA/od7/private/full",
					entry.getCellFeedUri());
		assertEquals("http://spreadsheets.google.com/feeds/worksheets/t9Vty0Ub77fApMbtxDFY7KA/private/full/od7/0",
					entry.getEditUri());
		
		
	}

	private int findDifference(String actual, String expected) {
		int index = -1;
		for(int i = 0; i < actual.length(); i++){
			char ac = actual.charAt(i);
			char ex = expected.charAt(i);
			if(ac != ex){
				index = i;
				break;
			}
		}
		return index;
	}

	private String clearWhiteSpaces(String string) {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < string.length(); i++){
			char ch = string.charAt(i);
			if(ch != '\n' && ch != ' '){
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}
}
