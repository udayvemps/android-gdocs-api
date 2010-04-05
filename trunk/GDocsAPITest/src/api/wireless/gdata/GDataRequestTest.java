package api.wireless.gdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import api.wireless.gdata.client.AbstructParserFactory;
import api.wireless.gdata.client.GDataProtocol;
import api.wireless.gdata.client.http.GDataRequest;
import api.wireless.gdata.client.http.GDataRequest.GDataRequestFactory;
import api.wireless.gdata.spreadsheets.parser.xml.XmlSpreadsheetsGDataParserFactory;
import api.wireless.gdata.util.ContentType;
import junit.framework.TestCase;

public class GDataRequestTest extends TestCase {

	public void testSetEtagForUpdateRequest() throws MalformedURLException, IOException {
		GDataRequestFactory requestFactory = new GDataRequestFactory();
		ContentType contentType = ContentType.TEXT_HTML;
		GDataRequest request =
			requestFactory.getRequest(GDataRequest.RequestType.UPDATE, new URL("http://www.google.com"), contentType);
		
		request.setEtag("test");
		HttpURLConnection connection = request.getConnection();
		
		String property = connection.getRequestProperty(GDataProtocol.Header.IF_MATCH);
		
		assertEquals("*", property);
	}
	
	public void testSetEtagForDeleteRequest() throws MalformedURLException, IOException {
		GDataRequestFactory requestFactory = new GDataRequestFactory();
		ContentType contentType = ContentType.TEXT_HTML;
		GDataRequest request =
			requestFactory.getRequest(GDataRequest.RequestType.DELETE, new URL("http://www.google.com"), contentType);
		
		request.setEtag("test");
		HttpURLConnection connection = request.getConnection();
		
		String property = connection.getRequestProperty(GDataProtocol.Header.IF_MATCH);
		
		assertEquals("*", property);
	}

}
