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
package api.wireless.gdata.spreadsheets.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.util.Log;

import api.wireless.gdata.client.HttpException;
import api.wireless.gdata.client.ServiceDataClient;
import api.wireless.gdata.client.http.GDataRequest;
import api.wireless.gdata.client.http.GDataRequest.GDataRequestFactory;
import api.wireless.gdata.client.http.GDataRequest.RequestType;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.ServiceException;


public class SpreadsheetGDataClient extends ServiceDataClient  {		
	
	public static final String SPREADSHEETS_HOST = "spreadsheets.google.com";
	

	public SpreadsheetGDataClient(String applicationName, String protocol, String host){
		super(applicationName, protocol, host);
	}

	protected SpreadsheetGDataClient(String applicationName, String protocol, String host, GDataRequestFactory requestFactory){
		super(applicationName, protocol, host, requestFactory);
	}				
	

	public InputStream createCompleteEntry(String feedUrl, GDataSerializer entry, InputStream content, String contentType) 
	throws ServiceException, IOException {
	
		String boundary = "END_OF_PART";
		InputStream entryStream = null;
		GDataRequest request = null;
		try {
			URL url = new URL(feedUrl);
			request = createInsertRequest(url);
			request.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	
	        DataOutputStream dstream = new DataOutputStream(request.getRequestStream());
	        dstream.writeBytes("--" + boundary + "\r\n");
	        
	        // Put header
	        dstream.writeBytes("Content-Type: " + ContentType.ATOM);            
	        entry.serialize(dstream, GDataSerializer.FORMAT_CREATE);            
	        dstream.writeBytes("--" + boundary + "\r\n");
	        
	        // Put body
	        dstream.writeBytes("Content-Type: "+contentType);                                    
			byte buf[]=new byte[1024];
			int len;
			while((len=content.read(buf))>0)
				dstream.write(buf,0,len);			
			dstream.writeBytes("--" + boundary + "\r\n"); 			
			dstream.flush();
	
			request.execute();
			entryStream = request.getResponseStream();		
		} catch (ParseException e) {
			throw new ServiceException("Unable to parse entry", e);
		}
		return entryStream;
	}

//	/**
//	 * Load entry media content stream by request
//	 * @param mediaEntryUrl Request URL
//	 * @param etag Entry identifier 
//	 */
//	public InputStream getMediaEntryAsStream(String entryID,
//			String etag, ContentType Type) throws ServiceException, IOException {
//
//		String GD_DOMAIN = "http://spreadsheets.google.com";
//
//		InputStream entryStream = null;
//		GDataRequest request = null;
//		String key = entryID.substring(14);						
//		String type = ContentType.getFileExtension(Type);
//
//		if (type != null){
//			//URL url = new URL(GD_DOMAIN+"/feeds/download/spreadsheets/Export?fmcmd="+type+"&key="+key);
//			URL url = new URL(GD_DOMAIN+"/feeds/download/spreadsheets/Export?key="+key+"&exportFormat="+type);
//			request = createRequest(RequestType.QUERY, url, Type);
//			request.setEtag(etag);
//			request.execute();
//			entryStream = request.getResponseStream();
//		}
//		return entryStream;
//	}
	
	
	/**
	 * Load entry media content stream by request
	 * @param mediaEntryUrl Request URL
	 * @param cType Entry content type 
	 */	
	public InputStream getMediaEntryAsStream(URL mediaEntryUrl,
			ContentType cType) throws ServiceException, IOException {		
		InputStream entryStream = null;			
		GDataRequest request = createRequest(RequestType.QUERY, mediaEntryUrl, cType);		
		request.execute();
		entryStream = request.getResponseStream();
		return entryStream;
	}



//	entryStream = new GZIPInputStream(request.getErrorStream());
//	File textFile = new File("/data/anr/create_error.txt");
//	FileOutputStream fos = null;
//	try {
//		fos = new FileOutputStream(textFile);
//		byte buf[]=new byte[1024];
//		int len;
//		while((len=entryStream.read(buf))>0)
//			fos.write(buf,0,len);
//		fos.close();	
//	} catch (Exception x) {
//		x.printStackTrace();
//	}	
	
	
//	File textFile = new File("/sdcard/update_error.txt");
//	FileOutputStream fos = null;
//	try {
//		fos = new FileOutputStream(textFile);
//		byte buf[]=new byte[1024];
//		int len;
//		while((len=entryStream.read(buf))>0)
//			fos.write(buf,0,len);
//		fos.close();	
//	} catch (Exception x) {
//		x.printStackTrace();
//	}
	
//    FileOutputStream fos = new FileOutputStream(new File("/data/anr/update.xml"));
//    try {
//		entry.serialize(fos, GDataSerializer.FORMAT_UPDATE);
//	} catch (ParseException e1) {
//		e1.printStackTrace();
//	}
//    fos.close();

}
