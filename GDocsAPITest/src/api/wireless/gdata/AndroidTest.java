package api.wireless.gdata;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import api.wireless.BufferOutputStream;
import api.wireless.TestUtils;
import api.wireless.gdata.client.AbstructParserFactory;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
import api.wireless.gdata.spreadsheets.serializer.xml.XmlWorksheetEntryGDataSerializer;
import junit.framework.TestCase;

public class AndroidTest extends TestCase {

	public static String createWorksheetEntry = 
		"<?xml version='1.0' encoding='UTF-8' standalone='no' ?>" + 
		"<entry xmlns=\"http://www.w3.org/2005/Atom\" " + 
		"xmlns:gd=\"http://schemas.google.com/g/2005\" " +
		"xmlns:gs=\"http://schemas.google.com/spreadsheets/2006\">" +
		"<title>Title</title>" + 
		"<gs:rowCount>50</gs:rowCount>" +
		"<gs:colCount>50</gs:colCount>" +
		"</entry>";
	
	public void testTestableMethod(){
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestUtils util = new TestUtils();
		createWorksheetEntry = util.clearWhiteSpaces(createWorksheetEntry);
		actual = util.clearWhiteSpaces(actual);
		assertEquals(-1, util.findDifference(actual, createWorksheetEntry));
	}

	
}
