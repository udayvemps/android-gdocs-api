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
package api.wireless.gdata.docs.parser.xml;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.docs.data.FolderEntry;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlGDataParser;


public class XmlFolderGDataParser extends XmlDocumentListGDataParser {

	public XmlFolderGDataParser(InputStream is, XmlPullParser parser)
			throws ParseException {
		super(is, parser);	
	}
	
    @Override
	protected Entry createEntry() {
        return new FolderEntry();
    }

}
