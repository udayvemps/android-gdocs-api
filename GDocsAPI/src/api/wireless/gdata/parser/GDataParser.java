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


import java.io.IOException;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.data.Feed;

/**
 * Interface for parsing GData feeds.  Uses a &quot;pull&quot; model, where
 * entries are not read or parsed until {@link #readNextEntry}
 * is called.
 */
public interface GDataParser<E extends Entry> {

    /**
     * Starts parsing the feed, returning a {@link Feed} containing information
     * about the feed.  Note that the {@link Feed} does not contain any
     * information about any entries, as the entries have not yet been parsed.
     *
     * @return The {@link Feed} containing information about the parsed feed.
     * @throws ParseException Thrown if the feed cannot be parsed.
     */
    // TODO: rename to parseFeed?  need to make the API clear.
    Feed<E> init() throws ParseException;

    /**
     * Parses a GData entry.  You can either call {@link #init()} or
     * {@link #parseStandaloneEntry()} for a given feed.
     *
     * @return The parsed entry.
     * @throws ParseException Thrown if the entry could not be parsed.
     */
    E parseStandaloneEntry() throws ParseException, IOException;

    /**
     * Returns whether or not there is more data in the feed.
     */
    boolean hasMoreData();

    /**
     * Reads and parses the next entry in the feed.  The {@link Entry} that
     * should be filled is passed in -- if null, the entry will be created
     * by the parser; if not null, the entry will be cleared and reused.
     *
     * @param entry The entry that should be filled.  Should be null if this is
     * the first call to this method.  This entry is also returned as the return
     * value.
     *
     * @return The {@link Entry} containing information about the parsed entry.
     * If entry was not null, returns the same (reused) object as entry, filled
     * with information about the entry that was just parsed.  If the entry was
     * null, returns a newly created entry (as appropriate for the type of
     * feed being parsed).
     * @throws ParseException Thrown if the entry cannot be parsed.
     */
    E readNextEntry(E entry) throws ParseException, IOException;

    /**
     * Cleans up any state in the parser.  Should be called when caller is
     * finished parsing a GData feed.
     */
    void close();
}
