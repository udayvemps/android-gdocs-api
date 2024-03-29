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
package api.wireless.gdata;

/**
 * The base exception for GData operations.
 */
public class GDataException extends Exception {

    private final Throwable cause;

    /**
     * Creates a new empty GDataException.
     */
    public GDataException() {
        super();
        cause = null;
    }

    /**
     * Creates a new GDataException with the supplied message.
     * @param message The message for this GDataException.
     */
    public GDataException(String message) {
        super(message);
        cause = null;
    }

    /**
     * Creates a new GDataException with the supplied message and underlying
     * cause.
     *
     * @param message The message for this GDataException.
     * @param cause The underlying cause that was caught and wrapped by this
     * GDataException.
     */
    public GDataException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Creates a new GDataException with the underlying cause.
     *
     * @param cause The underlying cause that was caught and wrapped by this
     * GDataException.
     */
    public GDataException(Throwable cause) {
        this("", cause);
    }

    /**
     * @return the cause of this GDataException or null if the cause is unknown.
     */
    @Override
	public Throwable getCause() {
        return cause;
    }

    /**
     * @return a string representation of this exception.
     */
    @Override
	public String toString() {
        return super.toString() + (cause != null ? " " + cause.toString() : "");
    }
}
