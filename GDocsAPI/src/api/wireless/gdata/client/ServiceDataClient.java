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
package api.wireless.gdata.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import android.util.Log;
import api.wireless.gdata.client.TokenFactory.ClientLoginAccountType;
import api.wireless.gdata.client.TokenFactory.UserToken;
import api.wireless.gdata.client.http.GDataRequest;
import api.wireless.gdata.client.http.GDataRequest.GDataRequestFactory;
import api.wireless.gdata.client.http.GDataRequest.RequestType;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.util.AuthenticationException;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.ServiceException;

public class ServiceDataClient extends Service implements GDataClient {
	private static final String TAG = "ServiceDataClient";
	
	public static final String DEFAULT_AUTH_PROTOCOL = "https";
	public static final String DEFAULT_AUTH_HOST = "docs.google.com";

	public static final String DEFAULT_PROTOCOL = "http";
	public static final String DEFAULT_HOST = "docs.google.com";
	
	protected TokenFactory authTokenFactory;
	protected String applicationName;		
	protected String authProtocol;
	protected String authHost;
	protected String protocol;
	protected String host;
	
	public ServiceDataClient(String applicationName, String protocol, String host){
		this(applicationName, protocol, host, new GDataRequestFactory());
	}

	protected ServiceDataClient(String applicationName, String protocol, String host, GDataRequestFactory requestFactory){
		this.applicationName = applicationName;
	    this.protocol = protocol;
	    this.host = host;
		this.requestFactory = requestFactory;	    
		initRequestFactory(applicationName);
	}	

	private void initRequestFactory(String applicationName) {	
		requestFactory.setHeader("User-Agent", applicationName + "(gzip)");
		requestFactory.setHeader(GDataProtocol.Header.VERSION, SERVICE_VERSION); //       requestVersion.getVersionString()
	}
	
	public boolean getSSL(){
		return requestFactory.getSsl();
	}
	
	public void setSSL(boolean set){
		requestFactory.setSsl(set);
	}

	public void close() {
		//Log.i(TAG, "Closing everything! Need to implement if needed.");
	}

	public InputStream createEntry(URL feedUrl, GDataSerializer entry) 
		throws ServiceException, IOException {

		InputStream entryStream = null;
		GDataRequest request = null;
		try {
			request = createInsertRequest(feedUrl);						
			
			OutputStream os = request.getRequestStream();
			entry.serialize(os, GDataSerializer.FORMAT_CREATE);
			os.flush();

			request.execute();
			entryStream = request.getResponseStream();					
		} catch (ParseException e) {
			throw new ServiceException("Unable to serialize entry", e);
		}
		return entryStream;
	}

	public void deleteEntry(URL editUri, String etag)
	throws ServiceException, IOException {
		
		GDataRequest request = createDeleteRequest(editUri);
		request.setEtag(etag);
		request.execute();

	}

	/**
	 * Generates feed stream by request
	 * @param feedUrl Request URL
	 * @param etag Entry identifier  
	 * @throws ServiceException 
	 */
	public InputStream getFeedAsStream(URL feedUrl, String etag)
	throws  IOException, ServiceException {

		InputStream feedStream = null;
		GDataRequest request;

		request = createFeedRequest(feedUrl);
		request.setEtag(etag);
		request.execute();
		feedStream = request.getResponseStream();			

		return feedStream;
	}

	public InputStream getMediaEntryAsStream(URL mediaEntryUrl, String etag, ContentType ct)
			throws ServiceException, IOException {
		InputStream feedStream = null;
		GDataRequest request;

		request = createRequest(RequestType.QUERY, mediaEntryUrl, ct);
		request.setEtag(etag);
		request.execute();
		feedStream = request.getResponseStream();			

		return feedStream;
	}
	
	public InputStream getMediaEntryAsStream(URL mediaEntryUrl, boolean isHosted)
	throws ServiceException, IOException {
		InputStream feedStream = null;
		GDataRequest request = requestFactory.getRequest(mediaEntryUrl, isHosted);
				
		String cookie;
		if (isHosted)
			cookie = String.format("WRITELYHS=%s=%s; SID=%s",
					getTokenFactory().getUserDomain(),
					requestFactory.getAuthToken().getValue(UserToken.AUTH), 
					requestFactory.getAuthToken().getValue(UserToken.SID));
		else
			cookie = String.format("WRITELY_SID=%s; SID=%s", 
					requestFactory.getAuthToken().getValue(UserToken.AUTH), 
					requestFactory.getAuthToken().getValue(UserToken.SID));
		request.setHeader("Cookie", cookie);
		
		
		request.execute();
		feedStream = request.getResponseStream();			

		return feedStream;
	}

	
	
	public InputStream updateEntry(URL editUri, String etag,
			GDataSerializer entry) throws ServiceException, IOException {
		
		if (entry == null) {
			throw new NullPointerException("Must supply entry");
		}		

		InputStream entryStream = null;
		try {
			GDataRequest request = createUpdateRequest(editUri);
			request.setEtag(etag);			
			
			OutputStream os = request.getRequestStream();
			entry.serialize(os, GDataSerializer.FORMAT_UPDATE);
			os.flush();

			request.execute();
			entryStream = request.getResponseStream();			
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return entryStream;
	}

	public InputStream updateMediaEntry(URL editUri, String eTag,
			InputStream mediaEntryInputStream, ContentType contentType)
	throws ServiceException, IOException {	

		InputStream entryStream = null;
		GDataRequest request = null;

		request = createRequest(RequestType.UPDATE,  editUri, contentType);
		request.setEtag(eTag);			

		// Copy input stream 
		BufferedInputStream bis = new BufferedInputStream(mediaEntryInputStream);
		BufferedOutputStream bos = new BufferedOutputStream(request.getRequestStream());

		int byte_;
		while ((byte_ = bis.read ()) != -1)
			bos.write (byte_);
		bos.flush();

		request.execute();
		entryStream = request.getResponseStream();

		
		return entryStream;
	}
	
	public void createTokenFactory(String serviceName) {
		authTokenFactory = new TokenFactory(serviceName, applicationName);
	}
	
	public void createTokenFactory(String serviceName, ClientLoginAccountType accountType) {
		authTokenFactory = new TokenFactory(serviceName, applicationName);
		authTokenFactory.setAccountType(accountType);
	}
	
	
	public TokenFactory getTokenFactory(){
		return authTokenFactory;
	}	
	
	public void setUserCredentials(String user, String pass) throws AuthenticationException {
		authTokenFactory.setUserCredentials(user, pass);		
		requestFactory.setAuthToken(authTokenFactory.getAuthToken());
	}
	
	public void setUserCredentials(String user, String pass, ClientLoginAccountType accountType) throws AuthenticationException {
		authTokenFactory.setUserCredentials(user, pass, accountType);		
		requestFactory.setAuthToken(authTokenFactory.getAuthToken());
	}
	
	public void setUserToken(String token){
		authTokenFactory.setAuthToken(token);		
		requestFactory.setAuthToken(authTokenFactory.getAuthToken());
	}
	
	public void setUserToken(HashMap<String,String> tokens){
		authTokenFactory.setAuthTokens(tokens);		
		requestFactory.setAuthToken(authTokenFactory.getAuthToken());
	}

}
