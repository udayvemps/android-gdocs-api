/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package api.wireless.gdata.util;

/**
 * The ServiceConfigurationException should be thrown if a GData server
 * or client configuration is not valid.
 *
 * 
 */
public class ServiceConfigurationException extends ServiceException {

  public ServiceConfigurationException(String message) {
    super(message);
  }

  public ServiceConfigurationException(String message,
      Throwable cause) {
    super(message, cause);
  }

  public ServiceConfigurationException(Throwable cause) {
    super(cause);
  }

}
