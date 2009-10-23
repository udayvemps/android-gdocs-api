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
// Copyright 2007 The Android Open Source Project

package api.wireless.gdata.client;

import java.io.InputStream;

/**
 * A class representing exceptional (i.e., non 200) responses from an HTTP
 * Server.
 */
public class HttpException extends Exception {

  public static final int SC_BAD_REQUEST = 400;

  public static final int SC_UNAUTHORIZED = 401;

  public static final int SC_FORBIDDEN = 403;

  public static final int SC_NOT_FOUND = 404;

  public static final int SC_CONFLICT = 409;

  public static final int SC_GONE = 410;

  public static final int SC_INTERNAL_SERVER_ERROR = 500;

  private final int statusCode;

  private final InputStream responseStream;

  /**
   * Creates an HttpException with the given message, statusCode and
   * responseStream.
   */
  //TODO: also record response headers?
  public HttpException(String message, int statusCode,
      InputStream responseStream) {
    super(message);
    this.statusCode = statusCode;
    this.responseStream = responseStream;
  }

  /**
   * Gets the status code associated with this exception.
   * @return the status code returned by the server, typically one of the SC_*
   * constants.
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * @return the error response stream from the server.
   */
  public InputStream getResponseStream() {
    return responseStream;
  }
}
