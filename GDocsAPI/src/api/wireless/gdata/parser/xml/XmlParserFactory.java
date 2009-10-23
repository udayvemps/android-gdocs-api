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
package api.wireless.gdata.parser.xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * Factory for creating new {@link org.xmlpull.v1.XmlPullParser}s and
 * {@link org.xmlpull.v1.XmlSerializer}s
 */
public interface XmlParserFactory {

    /**
     * Creates a new {@link XmlPullParser}.
     *
     * @return A new {@link XmlPullParser}.
     * @throws XmlPullParserException Thrown if the parser could not be created.
     */
    XmlPullParser createParser() throws XmlPullParserException;

    /**
     * Creates a new {@link XmlSerializer}.
     *
     * @return A new {@link XmlSerializer}.
     * @throws XmlPullParserException Thrown if the serializer could not be
     * created.
     */
    XmlSerializer createSerializer() throws XmlPullParserException;
}
