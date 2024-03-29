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
package api.wireless.gdata.parser;

import api.wireless.gdata.GDataException;

/**
 * Exception thrown if a GData feed cannot be parsed.
 */
public class ParseException extends GDataException {
    
    /**
     * Creates a new empty ParseException.
     */
    public ParseException() {
        super();
    }
    
    /**
     * Creates a new ParseException with the supplied message.
     * @param message The message for this ParseException.
     */
    public ParseException(String message) {
        super(message);
    }
    
    /**
     * Creates a new ParseException with the supplied message and underlying
     * cause.
     * 
     * @param message The message for this ParseException.
     * @param cause The underlying cause that was caught and wrapped by this
     * ParseException.
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
