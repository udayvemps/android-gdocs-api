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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
import api.wireless.gdata.util.common.base.CharEscapers;
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
	
	private ClientLoginAccountType accountType;

	/**
	 * The path name of the Google login handler.
	 */
	public static final String GOOGLE_LOGIN_PATH = "/accounts/ClientLogin";


	/**
	 * The valid values for the "accountType" parameter in ClientLogin.	 
	 */
	public enum ClientLoginAccountType {
	  // Authenticate as a Google account only.
	  GOOGLE("GOOGLE"),
	  // Authenticate as a hosted account only.
	  HOSTED("HOSTED"),
	  // Authenticate first as a hosted account; if attempt fails, authenticate as a
	  // Google account.  Use HOSTED_OR_GOOGLE if you're not sure which type of
	  // account needs authentication. If the user information matches both a hosted
	  // and a Google account, only the hosted account is authenticated.
	  HOSTED_OR_GOOGLE("HOSTED_OR_GOOGLE");

	  private final String accountTypeValue;

	  ClientLoginAccountType(String accountTypeValue) {
	    this.accountTypeValue = accountTypeValue;
	  }

	  /** Returns the value of the accountType. */
	  public String getValue() {
	    return accountTypeValue;
	  }
	}


	/**
	 * The UserToken encapsulates the token retrieved as a result of
	 * authenticating to Google using a user's credentials.
	 */
	public static class UserToken {
		
		public static final String AUTH = "Auth";
		public static final String SID = "SID";

		private HashMap<String, String> tokens;

		public UserToken(String token) {
			tokens = new HashMap<String, String>();
			tokens.put(AUTH, token);
		}
		
		public UserToken(HashMap<String, String> tokens) {
			this.tokens = tokens;
		}

		public String getValue() {
			return tokens.get(AUTH);
		}
		
		public HashMap<String, String> getValues() {
			return tokens;
		}
		
		public String getValue(String key) {
			String token = null;
			if (tokens != null)
				token = tokens.get(key);
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
			return "GoogleLogin auth=" + tokens.get(AUTH);
		}
	}
	
	public TokenFactory(String serviceName, String applicationName) {
		this.applicationName = applicationName;
		this.serviceName = serviceName;
		this.loginProtocol = "https";
		this.domainName = "www.google.com";
		this.accountType = ClientLoginAccountType.HOSTED_OR_GOOGLE;
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
		this.accountType = ClientLoginAccountType.HOSTED_OR_GOOGLE;
		HashMap<String,String> tokenPairs = getAuthTokens(username, password, serviceName, applicationName, accountType);				
		setAuthTokens(tokenPairs);
	}
	
	public void setUserCredentials(String username,
			String password,
			ClientLoginAccountType accountType)
	throws AuthenticationException {		
		this.username = username;
		this.password = password;
		this.accountType = accountType;		
		HashMap<String,String> tokenPairs = getAuthTokens(username, password, serviceName, applicationName, accountType);
		setAuthTokens(tokenPairs);
	}

	/**
	 * Set the authentication token.
	 */
	public void setAuthToken(String token) {
		this.authToken = new UserToken(token);
	}
	
	public void setAuthTokens(HashMap<String,String> tokens) {
		this.authToken = new UserToken(tokens);
	}
	
	public UserToken getAuthToken() {
	    return this.authToken;
	}
	
	public HashMap<String, String> getAuthTokens(String username,
			String password,
			String serviceName,
			String applicationName,
			ClientLoginAccountType accountType)
	throws AuthenticationException {

		String postOutput = "";
		try {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();						
			params.add(new BasicNameValuePair("Email", username));
			params.add(new BasicNameValuePair("Passwd", password));
			params.add(new BasicNameValuePair("source", applicationName));
			params.add(new BasicNameValuePair("service", serviceName));
			params.add(new BasicNameValuePair("accountType", accountType.getValue()));
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
			ArrayList<BasicHeader> hdrs = new ArrayList<BasicHeader>();
			hdrs.add(new BasicHeader("Content-type", "application/x-www-form-urlencoded"));
			
			Map<String, String> mParams = new HashMap<String, String>();
			mParams.put("Email", username);
			mParams.put("Passwd", password);
			mParams.put("source", applicationName);
			mParams.put("service", serviceName);
			mParams.put("accountType", accountType.getValue());
			
			
			//URI url = new URI(loginProtocol + "://" + domainName + GOOGLE_LOGIN_PATH);
			//postOutput = makePostRequest(url, headersToArray(hdrs), entity);			
			URL url = new URL(loginProtocol + "://" + domainName + GOOGLE_LOGIN_PATH);
			postOutput = makePostRequest(url, mParams);
		} catch (IOException e) {
			AuthenticationException ae =
				new AuthenticationException("Error connecting with login URI");
			ae.initCause(e);
			throw ae;
		}/* catch (URISyntaxException e) {
			AuthenticationException ae =
				new AuthenticationException("Wrong authentication URI");
			ae.initCause(e);
			throw ae;
		}*/
		
		HashMap<String,String> tokenPairs = StringUtil.string2Map(postOutput.trim(), "\n", "=", true);
		String token = tokenPairs.get("Auth");
		if (token == null) {
			throw getAuthException(tokenPairs);
		}

		return tokenPairs;
	}

	public String getAuthToken(String username,
			String password,
			String serviceName,
			String applicationName,
			ClientLoginAccountType accountType)
	throws AuthenticationException {		

		HashMap<String,String> tokenPairs = getAuthTokens(username, password, serviceName, applicationName, accountType);
		return tokenPairs.get("Auth");
	}
	
	public void setAccountType(ClientLoginAccountType at){
		this.accountType = at;
	}
	
	public ClientLoginAccountType getAccountType(){
		return accountType;
	}
	
	public void setUsername(String name){
		this.username = name;
	}
	
	public String getUserName(){
		return this.username;
	}
	
	public String getUserDomain(){
		String res = "";	
		if (username != null){
			int i = username.indexOf("@");
			if (i>=0) res = username.substring(i+1);				
		}
		return res;
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
	
	/**
	 * Makes a HTTP POST request to the provided {@code url} given the
	 * provided {@code parameters}.  It returns the output from the POST
	 * handler as a String object.
	 *
	 * @param url the URL to post the request
	 * @param parameters the parameters to post to the handler
	 * @return the output from the handler
	 * @throws IOException if an I/O exception occurs while creating, writing,
	 *                     or reading the request
	 */
	public String makePostRequest(URL url, Map<String, String> parameters)
		throws IOException {

		// Open connection		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

		// Set properties of the connection
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

		// Form the POST parameters
		StringBuilder content = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> parameter : parameters.entrySet()) {
			if (!first) {
				content.append("&");
			}
			content.append(
					CharEscapers.uriEscaper().escape(parameter.getKey())).append("=");
			content.append(CharEscapers.uriEscaper().escape(parameter.getValue()));
			first = false;
		}

		OutputStream outputStream = null;
		try {
			outputStream = urlConnection.getOutputStream();
			outputStream.write(content.toString().getBytes("utf-8"));
			outputStream.flush();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}

		// Retrieve the output
		InputStream inputStream = null;
		StringBuilder outputBuilder = new StringBuilder();
		try {
			int responseCode = urlConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inputStream = urlConnection.getInputStream();
			} else {
				inputStream = urlConnection.getErrorStream();
			}

			String string;
			if (inputStream != null) {
				BufferedReader reader =
					new BufferedReader(new InputStreamReader(inputStream));
				while (null != (string = reader.readLine())) {
					outputBuilder.append(string).append('\n');
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
			String token = getAuthToken(username, password, serviceName, applicationName, accountType);
			setAuthToken(token);
		} else {
			throw sessionExpired;
		}
	}	
}
