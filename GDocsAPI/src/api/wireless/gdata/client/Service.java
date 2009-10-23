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

import java.io.IOException;
import java.net.URL;

import api.wireless.gdata.client.http.GDataRequest;
import api.wireless.gdata.client.http.GDataRequest.GDataRequestFactory;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.ServiceException;


public class Service {

	protected GDataRequestFactory requestFactory; // = new GDataRequest.GDataRequestFactory();	

	/**
	 * Client-configured connection timeout value. A value of -1 indicates the
	 * client has not set any timeout.
	 */
	protected int connectTimeout = -1;

	/**
	 * Client configured read timeout value. A value of -1 indicates the client
	 * has not set any timeout.
	 */
	int readTimeout = -1;

	/**
	 * Content type of data posted to the GData service. Defaults to Atom using
	 * UTF-8 character set.
	 */
	private ContentType contentType = ContentType.ATOM;
	
	protected String SERVICE_VERSION = "3.0";

	public Service() {}

	/**
	 * Sets the default wait timeout (in milliseconds) for a connection to the
	 * remote GData service.
	 * 
	 * @param timeout the read timeout. A value of zero indicates an infinite
	 *        timeout.
	 * @throws IllegalArgumentException if the timeout value is negative.
	 * 
	 * @see java.net.URLConnection#setConnectTimeout(int)
	 */
	public void setConnectTimeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout value cannot be negative");
		}
		connectTimeout = timeout;
	}


	/**
	 * Sets the default wait timeout (in milliseconds) for a response from the
	 * remote GData service.
	 * 
	 * @param timeout the read timeout. A value of zero indicates an infinite
	 *        timeout.
	 * @throws IllegalArgumentException if the timeout value is negative.
	 * 
	 * @see java.net.URLConnection#setReadTimeout(int)
	 */
	public void setReadTimeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout value cannot be negative");
		}
		readTimeout = timeout;
	}
	
	/**
	 * Sets timeout value for GDataRequest.
	 */
	public void setTimeouts(GDataRequest request) {
		if (connectTimeout >= 0) {
			request.setConnectTimeout(connectTimeout);
		}
		if (readTimeout >= 0) {
			request.setReadTimeout(readTimeout);
		}
	}	


	/**
	 * Creates a new GDataRequest for use by the service.
	 * 
	 * For query requests, use {@link #createRequest(Query, ContentType)} instead.
	 */
	public GDataRequest createRequest(GDataRequest.RequestType type,
			URL requestUrl, ContentType inputType) throws IOException,
			ServiceException {

		GDataRequest request =
			requestFactory.getRequest(type, requestUrl, inputType);
		setTimeouts(request);
		return request;
	}
	
	public GDataRequest createRequest(GDataRequest.RequestType type,
			URL requestUrl) throws IOException,
			ServiceException {

		GDataRequest request =
			requestFactory.getRequest(type, requestUrl, contentType);
		setTimeouts(request);
		return request;
	}

	/**
	 * Executes a GData feed request against the target service and returns the
	 * resulting feed results via an input stream.
	 * 
	 * @param feedUrl URL that defines target feed.
	 * @return GData request instance that can be used to read the feed data.
	 * @throws IOException error communicating with the GData service.
	 * @throws ServiceException creation of query feed request failed.
	 * 
	 * @see Query#getUrl()
	 */
	public GDataRequest createFeedRequest(URL feedUrl) throws IOException,
	ServiceException {
		return createRequest(GDataRequest.RequestType.QUERY, feedUrl, contentType);
	}

	/**
	 * Returns a GDataRequest instance that can be used to access an entry's
	 * contents as a stream, given the URL of the entry.
	 * 
	 * @param entryUrl resource URL for the entry.
	 * @return GData request instance that can be used to read the entry.
	 * @throws IOException error communicating with the GData service.
	 * @throws ServiceException entry request creation failed.
	 */
	public GDataRequest createEntryRequest(URL entryUrl) throws IOException,
	ServiceException {
		return createRequest(GDataRequest.RequestType.QUERY, entryUrl, contentType);
	}

	/**
	 * Creates a new GDataRequest that can be used to insert a new entry into a
	 * feed using the request stream and to read the resulting entry content from
	 * the response stream.
	 * 
	 * @param feedUrl the POST URI associated with the target feed.
	 * @return GDataRequest to interact with remote GData service.
	 * @throws IOException error reading from or writing to the GData service.
	 * @throws ServiceException insert request failed.
	 */
	public GDataRequest createInsertRequest(URL feedUrl) throws IOException,
	ServiceException {
		return createRequest(GDataRequest.RequestType.INSERT, feedUrl, contentType);
	}

	/**
	 * Creates a new GDataRequest that can be used to delete an Atom entry. For
	 * delete requests, no input is expected from the request stream nor will any
	 * response data be returned.
	 * 
	 * @param entryUrl the edit URL associated with the entry.
	 * @throws IOException error communicating with the GData service.
	 * @throws ServiceException creation of delete request failed.
	 */
	public GDataRequest createDeleteRequest(URL entryUrl) throws IOException,
	ServiceException {

		return createRequest(GDataRequest.RequestType.DELETE, entryUrl, 
				contentType);
	}

	/**
	 * Creates a new GDataRequest that can be used to update an existing Atom
	 * entry. The updated entry content can be written to the GDataRequest request
	 * stream and the resulting updated entry can be obtained from the
	 * GDataRequest response stream.
	 * 
	 * @param entryUrl the edit URL associated with the entry.
	 * @throws IOException error communicating with the GData service.
	 * @throws ServiceException creation of update request failed.
	 */
	public GDataRequest createUpdateRequest(URL entryUrl) throws IOException,
	ServiceException {

		return createRequest(GDataRequest.RequestType.UPDATE, entryUrl, 
				contentType);
	}	    

}
