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
package api.wireless.gdata.docs.serializer.xml;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.parser.xml.XmlParserFactory;
import api.wireless.gdata.serializer.xml.XmlEntryGDataSerializer;

public class XmlFolderEntryGDataSerializer extends XmlEntryGDataSerializer {

	public XmlFolderEntryGDataSerializer(XmlParserFactory factory, Entry entry) {
		super(factory, entry);
	}
	
    /**
     * Sets up the GData Document namespace.
     * 
     * @param serializer the XML serializer to use
     * @throws IOException on stream errors.
     */
    @Override
	protected void declareExtraEntryNamespaces(XmlSerializer serializer)
            throws IOException {
    	super.declareExtraEntryNamespaces(serializer);       
    }
    
    /* (non-JavaDoc)
     * Handles the non-Atom data belonging to the GData Document  namespace.
     */
    @Override
	protected void serializeExtraEntryContents(XmlSerializer serializer,
            int format) throws ParseException, IOException {
//        ListEntry entry = (ListEntry) getEntry();
//        Vector names = entry.getNames();
//        String name = null;
//        String value = null;
//        Iterator it = names.iterator();
//        while (it.hasNext()) {
//            name = (String) it.next();
//            value = entry.getValue(name);
//            if (value != null) {
//                serializer.startTag(NAMESPACE_GSX_URI, name);
//                serializer.text(value);
//                serializer.endTag(NAMESPACE_GSX_URI, name);
//            }
//        }
    }

}
