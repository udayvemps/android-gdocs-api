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
package api.wireless.gdata.client.http;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.util.Log;
import api.wireless.gdata.DateTime;
import api.wireless.gdata.client.GDataProtocol;
import api.wireless.gdata.client.TokenFactory;
import api.wireless.gdata.client.GDataProtocol.Header;
import api.wireless.gdata.client.TokenFactory.UserToken;
import api.wireless.gdata.util.AuthenticationException;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.EntityTooLargeException;
import api.wireless.gdata.util.InvalidEntryException;
import api.wireless.gdata.util.NoLongerAvailableException;
import api.wireless.gdata.util.NotAcceptableException;
import api.wireless.gdata.util.NotImplementedException;
import api.wireless.gdata.util.NotModifiedException;
import api.wireless.gdata.util.PreconditionFailedException;
import api.wireless.gdata.util.ResourceNotFoundException;
import api.wireless.gdata.util.ServiceException;
import api.wireless.gdata.util.ServiceForbiddenException;
import api.wireless.gdata.util.VersionConflictException;
import api.wireless.gdata.util.common.base.StreamUtil;


public class GDataRequest {

	public enum RequestType {
		QUERY, INSERT, UPDATE, DELETE, BATCH
	}


	public static class GDataRequestFactory {

		protected UserToken authToken;
		protected Map<String, String> headerMap
		= new LinkedHashMap<String, String>();
		protected Map<String, String> privateHeaderMap
		= new LinkedHashMap<String, String>();
		protected boolean useSsl = false;

		public void setAuthToken(TokenFactory.UserToken authToken) {
			this.authToken = authToken;
		}
		
		public TokenFactory.UserToken getAuthToken() {
			return this.authToken;
		}

		public boolean getSsl() {
			return this.useSsl;
		}
		
		public void setSsl(boolean set) {
			this.useSsl = set;
		}

		private void extendHeaderMap(Map<String, String> headerMap,
				String header, String value) {
			if (value == null) {
				headerMap.remove(header);
			} else {
				headerMap.put(header, value);
			}
		}

		public void setHeader(String header, String value) {
			extendHeaderMap(this.headerMap, header, value);
		}

		public void setPrivateHeader(String header, String value) {
			extendHeaderMap(this.privateHeaderMap, header, value);
		}

		@SuppressWarnings("unused")
		public GDataRequest getRequest(RequestType type,
				URL requestUrl,
				ContentType contentType)
		throws IOException {
			if (this.useSsl && !requestUrl.getProtocol().startsWith("https")) {
				requestUrl = new URL(
						requestUrl.toString().replaceFirst("http", "https"));
			}
			return new GDataRequest(type, requestUrl, contentType, authToken, headerMap, privateHeaderMap);
		}
		
		@SuppressWarnings("unused")
		public GDataRequest getRequest(URL requestUrl)
		throws IOException {
			return getRequest(requestUrl, false);
		}
		
		@SuppressWarnings("unused")
		public GDataRequest getRequest(URL requestUrl, boolean isHosted)
		throws IOException {
			if ((this.useSsl || isHosted) && !requestUrl.getProtocol().startsWith("https")) {
				requestUrl = new URL(
						requestUrl.toString().replaceFirst("http", "https"));
			}
			return new GDataRequest(requestUrl, authToken);
		}

	}


	/**
	 * Underlying HTTP connection to the GData service.
	 */
	protected HttpURLConnection httpConn;

	/**
	 * The request URL provided by the client.
	 */
	protected URL requestUrl;

	/**
	 * The GData request type.
	 */
	protected RequestType type;


	/**
	 * Indicates whether request execution has taken place. Set to
	 * <code>true</code> if executed, <code>false</code> otherwise.
	 */
	protected boolean executed = false;


	/**
	 * True if the request type expects input from the client.
	 */
	protected boolean expectsInput;


	/**
	 * True if the request type returns output to the client.
	 */
	protected boolean hasOutput;


	/**
	 * The connection timeout for this request. A value of -1 means no value has
	 * been configured (use JDK default timeout behavior).
	 */
	protected int connectTimeout = -1;


	/**
	 * The read timeout for this request. A value of -1 means no value has been
	 * configured (use JDK default timeout behavior).
	 */
	protected int readTimeout = -1;

	private String METHOD_OVERRIDE_PROPERTY = "true";


	/**
	 * Protected default constructor for testing.
	 */
	protected GDataRequest() {
	}

	/**
	 * Obtains a connection to the GData service.
	 */
	protected HttpURLConnection getRequestConnection(URL requestUrl)
	throws IOException {

		if (!requestUrl.getProtocol().startsWith("http")) {
			throw new UnsupportedOperationException("Unsupported scheme:"
					+ requestUrl.getProtocol());
		}
		HttpURLConnection uc = (HttpURLConnection) requestUrl.openConnection();

		// Should never cache GData requests/responses
		uc.setUseCaches(false);

		// Always follow redirects
		uc.setInstanceFollowRedirects(true);

		return uc;
	}

	public OutputStream getRequestStream() throws IOException {
	
		if (!expectsInput) {
			throw new IllegalStateException("Request doesn't accept input");
		}
		return httpConn.getOutputStream();
	}
	
	public InputStream getResponseStream() throws IOException {

		if (!executed) {
			throw new IllegalStateException(
					"Must call execute() before attempting to read response");
		}

		if (!hasOutput) {
			throw new IllegalStateException("Request doesn't have response data");
		}

		InputStream responseStream = httpConn.getInputStream();
		if ("gzip".equalsIgnoreCase(httpConn.getContentEncoding())) {
			responseStream = new GZIPInputStream(responseStream);
		}
		return responseStream;
	}
	
	public InputStream getErrorStream() throws IOException {
		return httpConn.getErrorStream();
	}

	public HttpURLConnection getConnection() {
		return httpConn;
	}
	
	public ContentType getResponseContentType() {

		if (!executed) {
			throw new IllegalStateException(
					"Must call execute() before attempting to read response");
		}
		String value = httpConn.getHeaderField("Content-Type");
		if (value == null) {
			return null;
		}
		return new ContentType(value);
	}

	public String getResponseHeader(String headerName) {
		return httpConn.getHeaderField(headerName);
	}

	public DateTime getResponseDateHeader(String headerName) {
		long dateValue = httpConn.getHeaderFieldDate(headerName, -1);
		return (dateValue >= 0) ? new DateTime(dateValue) : null;
	}	
	
	public void execute() throws IOException, ServiceException {

		if (connectTimeout >= 0) {
			httpConn.setConnectTimeout(connectTimeout);
		}

		if (readTimeout >= 0) {
			httpConn.setReadTimeout(readTimeout);
		}

		// Set the http.strictPostRedirect property to prevent redirected
		// POST/PUT/DELETE from being mapped to a GET. This
		// system property was a hack to fix a jdk bug w/out changing back
		// compat behavior. It's bogus that this is a system (and not a
		// per-connection) property, so we just change it for the duration
		// of the connection.
		// See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4023866
		String httpStrictPostRedirect =
			System.getProperty("http.strictPostRedirect");
		try {
			System.setProperty("http.strictPostRedirect", "true");
			httpConn.connect();
			
			checkResponse(); // will flush any request data

		} finally {						
			if (httpStrictPostRedirect == null) {
				System.clearProperty("http.strictPostRedirect");
			} else {
				System.setProperty("http.strictPostRedirect", httpStrictPostRedirect);
			}
		}

		executed = true;
	}

	/**
	 * Called after a request is executed to process the response and generate an
	 * appropriate exception (on failure).
	 */
	protected void checkResponse() throws IOException, ServiceException {

		int code = httpConn.getResponseCode();
		if (code >= 300 || code < 0) {
			handleErrorResponse();
		}		
	}

	/**
	 * Handles an error response received while executing a GData service request.
	 * Throws a {@link ServiceException} or one of its subclasses, depending on
	 * the failure conditions.
	 *
	 * @throws ServiceException exception describing the failure.
	 * @throws IOException error reading the error response from the GData
	 *         service.
	 */
	protected void handleErrorResponse() throws ServiceException, IOException {

		switch (httpConn.getResponseCode()) {

		case HttpURLConnection.HTTP_NOT_FOUND:
			throw new ResourceNotFoundException(httpConn);

		case HttpURLConnection.HTTP_BAD_REQUEST:
			throw new InvalidEntryException(httpConn);

		case HttpURLConnection.HTTP_FORBIDDEN:
			throw new ServiceForbiddenException(httpConn);

		case HttpURLConnection.HTTP_UNAUTHORIZED:
			throw new AuthenticationException(httpConn);

		case HttpURLConnection.HTTP_NOT_MODIFIED:
			throw new NotModifiedException(httpConn);

		case HttpURLConnection.HTTP_PRECON_FAILED:
			throw new PreconditionFailedException(httpConn);

		case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
			throw new NotImplementedException(httpConn);

		case HttpURLConnection.HTTP_CONFLICT:
			throw new VersionConflictException(httpConn);

		case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
			throw new EntityTooLargeException(httpConn);

		case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
			throw new NotAcceptableException(httpConn);

		case HttpURLConnection.HTTP_GONE:
			throw new NoLongerAvailableException(httpConn);

		default:
			throw new ServiceException(httpConn);
		}
	}	


	public void setConnectTimeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout cannot be negative");
		}
		connectTimeout = timeout;
	}

	public void setReadTimeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout cannot be negative");
		}
		readTimeout = timeout;
	}

	public void setIfModifiedSince(DateTime conditionDate) {
		if (conditionDate == null) {
			return;
		}

		if (type == RequestType.QUERY) {
			setHeader(GDataProtocol.Header.IF_MODIFIED_SINCE,
					conditionDate.toStringRfc822());
		} else {
			throw new IllegalStateException(
					"Date conditions not supported for this request type");
		}
	}

	public void setEtag(String etag) {

		if (etag == null) {
			return;
		}

		switch (type) {
		case QUERY:
			if (etag != null) {
				setHeader(GDataProtocol.Header.IF_NONE_MATCH, etag);
			}
			break;
		case UPDATE:
			if (etag != null) {
				setHeader(GDataProtocol.Header.IF_MATCH, "*");
			}
			break;
		case DELETE:
			if (etag != null) {
				setHeader(GDataProtocol.Header.IF_MATCH, "*");
			}
			break;
		default:
			throw new IllegalStateException(
					"Etag conditions not supported for this request type");
		}
	}

	public void setMethod(String method) throws ProtocolException {
		httpConn.setRequestMethod(method);
	}

	public void setHeader(String name, String value) {
		httpConn.setRequestProperty(name, value);
	}


	public void setPrivateHeader(String name, String value) {
		httpConn.setRequestProperty(name, value);
	}

	/**
	 * Constructs a new HttpGDataRequest instance of the specified RequestType,
	 * targeting the specified URL.
	 *
	 * @param type type of GDataRequest.
	 * @param requestUrl request target URL.
	 * @param contentType the content type of request/response data.
	 * @param headerMap a set of headers to be included in each request
	 * @param privateHeaderMap a set of headers to be included in each request
	 * @throws IOException on error initializating service connection.
	 */
	protected GDataRequest(RequestType type, URL requestUrl,
			ContentType contentType, UserToken authToken,
			Map<String, String> headerMap, Map<String, String> privateHeaderMap)
	throws IOException {

		this.type = type;
		this.requestUrl = requestUrl;
		httpConn = getRequestConnection(requestUrl);

		switch (type) {

		case QUERY:
			hasOutput = true;
			break;

		case INSERT:
		case BATCH:
			expectsInput = true;
			hasOutput = true;
			setMethod("POST");
			setHeader("Content-Type", contentType.toString());
			break;

		case UPDATE:
			expectsInput = true;
			hasOutput = true;
			if (Boolean.getBoolean(METHOD_OVERRIDE_PROPERTY )) {
				setMethod("POST");
				setHeader(Header.METHOD_OVERRIDE, "PUT");
			} else {
				setMethod("PUT");
			}
			setHeader("Content-Type", contentType.toString());
			break;

		case DELETE:
			if (Boolean.getBoolean(METHOD_OVERRIDE_PROPERTY)) {
				setMethod("POST");
				setHeader(Header.METHOD_OVERRIDE, "DELETE");
			} else {
				setMethod("DELETE");
			}
			setHeader("Content-Length", "0"); // no data to POST
			break;

		default:
			throw new UnsupportedOperationException("Unknown request type:" + type);
		}

		if (authToken != null) {
			String authHeader = authToken.getAuthorizationHeader();
			setPrivateHeader("Authorization", authHeader);
		}

		if (headerMap != null) {
			for (Map.Entry<String, String> e : headerMap.entrySet()) {
				setHeader(e.getKey(), e.getValue());
			}
		}

		if (privateHeaderMap != null) {
			for (Map.Entry<String, String> e : privateHeaderMap.entrySet()) {
				setPrivateHeader(e.getKey(), e.getValue());
			}
		}

		// Request compressed response format
		setHeader("Accept-Encoding", "gzip");

		httpConn.setDoOutput(expectsInput);
		
		
	}
	
	protected GDataRequest(URL requestUrl, UserToken authToken)
		throws IOException {		
		
		hasOutput = true;
		this.requestUrl = requestUrl;
		
		httpConn = getRequestConnection(requestUrl);
		
		setHeader("Accept", "*/*");
		setHeader("Accept-Encoding", "gzip");
			
		httpConn.setDoOutput(expectsInput);
	}
		


}
