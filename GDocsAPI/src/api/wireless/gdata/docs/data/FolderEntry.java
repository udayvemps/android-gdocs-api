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
package api.wireless.gdata.docs.data;

import api.wireless.gdata.parser.xml.XmlGDataParser;

public class FolderEntry extends DocumentListEntry {

	/**
	 * Label for category.
	 */
	public static final String LABEL = "folder";
	
	/**
	 * Kind category term used to label the entries which are
	 * of document type.
	 */
	public static final String TERM = DocsNamespace.DOCS + "#" + LABEL;	
	
	public FolderEntry() {
		super();
		setCategory(TERM);
		setCategoryScheme(XmlGDataParser.NAMESPACE_GD_URI + "#" + DocsNamespace.KIND);
		setLabel(LABEL);
	}
	
	public FolderEntry(FolderEntry entry){
		super(entry);
		setCategory(TERM);
		setCategoryScheme(XmlGDataParser.NAMESPACE_GD_URI + "#" + DocsNamespace.KIND);
		setLabel(LABEL);
	}

}
