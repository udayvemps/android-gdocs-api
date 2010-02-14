package api.wireless.gdata;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import api.wireless.gdata.client.AbstructParserFactory;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
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
		"<title type=\"text\">Title</title>" + 
		"<content type=\"text\">Some text</content>" +
		"<link rel=\"http://schemas.google.com/spreadsheets/2006#listfeed\" " +
		"type=\"application/atom+xml\" href=\"http://spreadsheets.google.com/feeds/list/key/worksheetId/private/full\"/>" + 
		"<link rel=\"http://schemas.google.com/spreadsheets/2006#cellsfeed\" " + 
		"type=\"application/atom+xml\" href=\"http://spreadsheets.google.com/feeds/cells/key/worksheetId/private/full\"/> " + 
		"<link rel=\"self\" type=\"application/atom+xml\" "+
		"href=\"http://spreadsheets.google.com/feeds/worksheets/key/private/full/worksheetId\"/>" +
		"<link rel=\"edit\" type=\"application/atom+xml\" " +
		"href=\"http://spreadsheets.google.com/feeds/worksheets/key/private/full/worksheetId/version\"/> "+
		"<gs:rowCount>45</gs:rowCount>" +
		"<gs:colCount>15</gs:colCount>" +
		"</entry>";

	
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
		entry.setTitle("Title");
		entry.setRowCount(45);
		entry.setColCount(15);
		entry.setContent("Sometext");
		entry.setUpdateDate("2007-07-30T18:51:30.666Z");
		entry.setCategory("http://schemas.google.com/spreadsheets/2006#worksheet");
		entry.setCategoryScheme("http://schemas.google.com/spreadsheets/2006");
		entry.setEditUri("http://spreadsheets.google.com/feeds/list/key/worksheetId/private/full");
		entry.setHtmlUri("http://spreadsheets.google.com/feeds/cells/key/worksheetId/private/full");
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
