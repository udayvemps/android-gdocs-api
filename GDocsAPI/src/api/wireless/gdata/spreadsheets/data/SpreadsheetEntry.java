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
package api.wireless.gdata.spreadsheets.data;

import api.wireless.gdata.GDataException;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.util.common.base.StringUtil;

/**
 * Represents an entry in a GData Spreadsheets meta-feed.
 */
public class SpreadsheetEntry extends Entry {
    /** The URI of the worksheets meta-feed for this spreadsheet */
    private String worksheetsUri = null;

    /**
     * Fetches the URI of the worksheets meta-feed (that is, list of worksheets)
     * for this spreadsheet.
     * 
     * @return the worksheets meta-feed URI
     * @throws GDataException if the unique key is not set
     */
    public String getWorksheetFeedUri() throws GDataException {
        if (StringUtil.isEmpty(worksheetsUri)) {
            throw new GDataException("worksheet URI is not set");
        }
        return worksheetsUri;
    }

    /**
     * Sets the URI of the worksheet meta-feed corresponding to this
     * spreadsheet.
     * 
     * @param href
     */
    public void setWorksheetFeedUri(String href) {
        worksheetsUri = href;
    }
}
