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
package api.wireless.gdata.docs.client;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import api.wireless.gdata.client.ServiceDataClient;
import api.wireless.gdata.client.http.GDataRequest;
import api.wireless.gdata.client.http.GDataRequest.GDataRequestFactory;
import api.wireless.gdata.client.http.GDataRequest.RequestType;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.ServiceException;


public class DocsGDataClient extends ServiceDataClient  {			

	public DocsGDataClient(String applicationName, String protocol, String host){
		super(applicationName, protocol, host);
	}

	protected DocsGDataClient(String applicationName, String protocol, String host, GDataRequestFactory requestFactory){
		super(applicationName, protocol, host, requestFactory);
	}				
	

	public InputStream createCompleteEntry(URL feedUrl, GDataSerializer entry, InputStream content, String contentType) 
	throws ServiceException, IOException {
	
		String boundary = "END_OF_PART";
		InputStream entryStream = null;
		GDataRequest request = null;
		try {
			request = createInsertRequest(feedUrl);
			request.setHeader("Content-Type", "multipart/related; boundary=" + boundary);
	        
	        DataOutputStream dstream = new DataOutputStream(request.getRequestStream());
	        
	        dstream.writeBytes("--" + boundary+"\r\n");
	        
	        // Put header
	        dstream.writeBytes("Content-Type: " + ContentType.ATOM+"\r\n\r\n");            
	        entry.serialize(dstream, GDataSerializer.FORMAT_CREATE);            
	        dstream.writeBytes("\r\n\r\n--" + boundary+"\r\n");
	        
	        // Put body
	        dstream.writeBytes("Content-Type: "+contentType+"\r\n\r\n");                                    
			byte buf[]=new byte[1024];
			int len;
			while((len=content.read(buf))>0)
				dstream.write(buf,0,len);			
			dstream.writeBytes("\r\n\r\n--" + boundary + "--");
			dstream.flush();			
	
			request.execute();
			entryStream = request.getResponseStream();			
		} catch (ParseException e) {
			throw new ServiceException("Unable to serialize entry", e);
		}
		return entryStream;
	}
	
	public InputStream createCompleteEntry(URL feedUrl, String name, InputStream content, String contentType) 
	throws ServiceException, IOException {
	
		InputStream entryStream = null;
		GDataRequest request = null;

		request = createInsertRequest(feedUrl);
		request.setHeader("Content-Type", contentType);
		request.setHeader("Slug", name);

		DataOutputStream dstream = new DataOutputStream(request.getRequestStream());	        	        

		byte buf[]=new byte[1024];
		int len;
		while((len=content.read(buf))>0)
			dstream.write(buf,0,len);			
		dstream.flush();			

		request.execute();
		entryStream = request.getResponseStream();			

		return entryStream;
	}
	
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
//	
//	/**
//	 * Load document entry media content stream by request
//	 * @param entryID Document key
//	 * @param etag Entry identifier 
//	 * @param cType Entry content type 
//	 */	
//	public InputStream getMediaEntryAsStream(String entryID,
//			String etag, ContentType cType) throws ServiceException, IOException {
//
//		String GD_DOMAIN = "http://docs.google.com";
//
//		InputStream entryStream = null;
//		GDataRequest request;
//
//		String key = entryID.substring(XmlGDataParser.DOC_LABEL.length()+3);
//		String type = ContentType.getFileExtension(cType);
//		if (type != null){			
//			URL url = new URL(GD_DOMAIN+"/feeds/download/documents/Export?docID="+key+"&exportFormat="+type);
//			request = createRequest(RequestType.QUERY, url, cType);
//			request.setEtag(etag);
//			request.execute();
//			entryStream = request.getResponseStream();
//		}
//		return entryStream;
//	}	
//	
//	/**
//	 * Load entry media content stream by request
//	 * @param entryID Document key
//	 * @param etag Entry identifier 
//	 * @param cType Entry content type 
//	 */	
//	public InputStream getPresentationEntryAsStream(String url,
//			String etag, ContentType cType) throws ServiceException, IOException {
//
//		InputStream entryStream = null;
//		GDataRequest request;
//
//		//String key = entryID.substring(XmlGDataParser.PRS_LABEL.length()+3);
//		String type = ContentType.getFileExtension(cType);
//		if (type != null){						
//			//URL url = new URL(GD_DOMAIN+"/feeds/download/presentation/Export?docID="+key+"&exportFormat="+type);
//			// http://docs.google.com/present/export?format=pdf&up=1&bg=1&print=0&id=dpb4t2f_120hm37pbf6&notes=0
//			//URL url = new URL(GD_DOMAIN+"/present/export?id="+key+"&format="+type);			
//			request = createRequest(RequestType.QUERY, new URL(url), cType);
//			request.setEtag(etag);
//			request.execute();
//			entryStream = request.getResponseStream();
//		}
//		return entryStream;
//	}	


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
