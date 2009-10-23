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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import api.wireless.gdata.client.ClientAuthenticationExceptions.SessionExpiredException;
import api.wireless.gdata.util.AuthenticationException;
import api.wireless.gdata.util.common.base.StringUtil;


public class TokenFactory {	
	
	// Name of client application accessing Google service
	private String applicationName;

	// Name of Google service being accessed
	private String serviceName;

	// Login name of the user
	private String username;

	// Password of the user
	private String password;

	// The Google domain name used for authentication
	private String domainName;

	// The protocol used for authentication
	private String loginProtocol;

	// Current auth token.
	private UserToken authToken = null;

	/**
	 * The path name of the Google login handler.
	 */
	public static final String GOOGLE_LOGIN_PATH = "/accounts/ClientLogin";

	/**
	 * The UserToken encapsulates the token retrieved as a result of
	 * authenticating to Google using a user's credentials.
	 */
	public static class UserToken {

		private String token;

		public UserToken(String token) {
			this.token = token;
		}

		public String getValue() {
			return token;
		}

		/**
		 * Returns an authorization header to be used for a HTTP request
		 * for the respective authentication token.
		 *
		 * @param requestUrl the URL being requested
		 * @param requestMethod the HTTP method of the request
		 * @return the "Authorization" header to be used for the request
		 */
		public String getAuthorizationHeader() {
			return "GoogleLogin auth=" + token;
		}
	}

	
	public TokenFactory(String serviceName, String applicationName) {
		this.applicationName = applicationName;
		this.serviceName = serviceName;
		this.loginProtocol = "https";
		this.domainName = "www.google.com";		
	}
	
	protected BasicHeader[] headersToArray(ArrayList<BasicHeader> hdrs) {
		BasicHeader[] h = new BasicHeader[hdrs.size()];		
		return hdrs.toArray(h);
	}
	
	/**
	 * Sets the credentials of the user to authenticate requests to the server.
	 *
	 * @param username the name of the user (an email address)
	 * @param password the password of the user
	 * @throws AuthenticationException if authentication failed.
	 */
	public void setUserCredentials(String username, String password)
	throws AuthenticationException {
		this.username = username;
		this.password = password;
		String token = getAuthToken(username, password, serviceName, applicationName);
		setAuthToken(token);
	}

	/**
	 * Set the authentication token.
	 */
	public void setAuthToken(String token) {
		this.authToken = new UserToken(token);
	}
	
	public UserToken getAuthToken() {
	    return this.authToken;
	}

	public String getAuthToken(String username,
			String password,
			String serviceName,
			String applicationName)
	throws AuthenticationException {		

		String postOutput = "";
		try {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();						
			params.add(new BasicNameValuePair("Email", this.username));
			params.add(new BasicNameValuePair("Passwd", this.password));
			params.add(new BasicNameValuePair("source", this.applicationName));
			params.add(new BasicNameValuePair("service", this.serviceName));
			params.add(new BasicNameValuePair("accountType", "HOSTED_OR_GOOGLE"));
			// Open connection
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);	

			ArrayList<BasicHeader> hdrs = new ArrayList<BasicHeader>();
			hdrs.add(new BasicHeader("Content-type", "application/x-www-form-urlencoded"));
			
			URI url = new URI(loginProtocol + "://" + domainName + GOOGLE_LOGIN_PATH);
			postOutput = makePostRequest(url, headersToArray(hdrs), entity);
		} catch (IOException e) {
			AuthenticationException ae =
				new AuthenticationException("Error connecting with login URI");
			ae.initCause(e);
			throw ae;
		} catch (URISyntaxException e) {
			AuthenticationException ae =
				new AuthenticationException("Wrong authentication URI");
			ae.initCause(e);
			throw ae;
		}

		// Parse the output
		Map<String, String> tokenPairs =
			StringUtil.string2Map(postOutput.trim(), "\n", "=", true);
		String token = tokenPairs.get("Auth");
		if (token == null) {
			throw getAuthException(tokenPairs);
		}
		return token;
	}
	
	protected String makePostRequest(URI url, Header[] headers, HttpEntity entity)
	throws IOException {

		// Initialize request
		HttpPost method = new HttpPost(url);   
		method.setHeaders(headers);					
		method.setEntity(entity);
		
		DefaultHttpClient client = new DefaultHttpClient();			
		HttpResponse res = client.execute(method);

		StringBuilder outputBuilder = new StringBuilder(); 
		InputStream inputStream = null;
		try {
			StatusLine sl = res.getStatusLine();
			if (sl.getStatusCode() == HttpStatus.SC_OK) {
				inputStream = res.getEntity().getContent();
			}

			String string;
			if (inputStream != null) {
				InputStreamReader reader = new InputStreamReader(inputStream);
				BufferedReader buffer = new BufferedReader(reader);				
				while ((string = buffer.readLine()) != null) {
					outputBuilder.append(string + "\n");
				}
			}												
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return outputBuilder.toString();		
	}	
	
	protected AuthenticationException getAuthException(Map<String, String> pairs) {

		String errorName = pairs.get("Error");

		if ("BadAuthentication".equals(errorName)) {
			return new ClientAuthenticationExceptions.InvalidCredentialsException("Invalid credentials");

		} else if ("AccountDeleted".equals(errorName)) {
			return new ClientAuthenticationExceptions.AccountDeletedException("Account deleted");

		} else if ("AccountDisabled".equals(errorName)) {
			return new ClientAuthenticationExceptions.AccountDisabledException("Account disabled");

		} else if ("NotVerified".equals(errorName)) {
			return new ClientAuthenticationExceptions.NotVerifiedException("Not verified");

		} else if ("TermsNotAgreed".equals(errorName)) {
			return new ClientAuthenticationExceptions.TermsNotAgreedException("Terms not agreed");

		} else if ("ServiceUnavailable".equals(errorName)) {
			return new ClientAuthenticationExceptions.ServiceUnavailableException("Service unavailable");

		} else {
			return new AuthenticationException("Error authenticating " +
			"(check service name)");
		}
	}	

	/**
	 * Handles a session expired exception.
	 */
	public void handleSessionExpiredException(
			SessionExpiredException sessionExpired)
	throws SessionExpiredException, AuthenticationException {

		if (username != null && password != null) {
			String token = getAuthToken(username, password, serviceName, applicationName);
			setAuthToken(token);
		} else {
			throw sessionExpired;
		}
	}	
}
