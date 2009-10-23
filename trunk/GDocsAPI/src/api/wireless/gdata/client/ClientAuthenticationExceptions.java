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

import api.wireless.gdata.util.AuthenticationException;


public class ClientAuthenticationExceptions {
		
	/**
	 * Authentication failed, invalid credentials presented to server.
	 */
	public static class InvalidCredentialsException
	extends AuthenticationException {
		public InvalidCredentialsException(String message) {
			super(message);
		}
	}


	/**
	 * Authentication failed, account has been deleted.
	 */
	public static class AccountDeletedException extends AuthenticationException {
		public AccountDeletedException(String message) {
			super(message);
		}
	}


	/**
	 * Authentication failed, account has been disabled.
	 */
	public static class AccountDisabledException extends AuthenticationException {
		public AccountDisabledException(String message) {
			super(message);
		}
	}


	/**
	 * Authentication failed, account has not been verified.
	 */
	public static class NotVerifiedException extends AuthenticationException {
		public NotVerifiedException(String message) {
			super(message);
		}
	}


	/**
	 * Authentication failed, user did not agree to the terms of service.
	 */
	public static class TermsNotAgreedException extends AuthenticationException {
		public TermsNotAgreedException(String message) {
			super(message);
		}
	}


	/**
	 * Authentication failed, authentication service not available.
	 */
	public static class ServiceUnavailableException extends
	AuthenticationException {
		public ServiceUnavailableException(String message) {
			super(message);
		}
	}
	
	/**
	 * Authentication failed, the token's session has expired.
	 */
	public static class SessionExpiredException extends AuthenticationException {
		public SessionExpiredException(String message) {
			super(message);
		}
	}	
}
