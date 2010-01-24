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

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.parser.GDataParser;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.serializer.GDataSerializer;

/**
 * Factory that creates {@link GDataParser}s and {@link GDataSerializer}s.
 */
public interface GDataParserFactory {
  /**
   * Creates a new {@link GDataParser} for the provided InputStream.
   *
   * @param entryClass Specify the class of Entry objects that are to be parsed. This
   *   lets createParser know which parser to create. 
   * @param is The InputStream that should be parsed. @return The GDataParser that will parse is.
   * @throws ParseException Thrown if the GDataParser could not be created.
   * @throws IllegalArgumentException if the feed type is unknown.
   */
  <E extends Entry> GDataParser<E> createParser(Class<E> entryClass, InputStream is)
      throws ParseException;

  /**
   * Creates a new {@link GDataParser} for the provided InputStream, using the
   * default feed type for the client.
   *
   * @param is The InputStream that should be parsed.
   * @return The GDataParser that will parse is.
   * @throws ParseException Thrown if the GDataParser could not be created.
   *         Note that this can occur if the feed in the InputStream is not of
   *         the default type assumed by this method.
   * @see #createParser(Class,InputStream)
   */
  <E extends Entry> GDataParser<E> createParser(InputStream is) throws ParseException;

  /**
   * Creates a new {@link GDataSerializer} for the provided Entry.
   *
   * @param entry The Entry that should be serialized.
   * @return The GDataSerializer that will serialize entry.
   */
  <E extends Entry> GDataSerializer createSerializer(E entry);
}
