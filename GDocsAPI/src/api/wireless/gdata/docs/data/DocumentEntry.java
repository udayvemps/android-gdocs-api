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

public class DocumentEntry extends DocumentListEntry{

    /** The URI of the document meta-feed for this document */
    private String editMediaUri = null;
	

	/**
	 * Kind category term used to label the entries which are
	 * of document type.
	 */
	public static final String TERM = DocsNamespace.DOCS + "#";	
	
	public DocumentEntry(){
		super();
		setCategoryScheme(XmlGDataParser.NAMESPACE_GD_URI + "#" + DocsNamespace.KIND);
		setDocument();
	}
		
	public DocumentEntry(DocumentEntry entry){
		super(entry);
		setEditMediaUri(entry.getEditMediaUri());
	}

	public void setEditMediaUri(String href) {
		editMediaUri = href;		
	}
	
	public String getEditMediaUri() {
		return editMediaUri;		
	}
	
	public void setPDF(){
		setLabel(XmlGDataParser.PDF_LABEL);
		setCategory(TERM+getLabel());
	}
	
	public void setDocument(){
		setLabel(XmlGDataParser.DOC_LABEL);
		setCategory(TERM+getLabel());
	}
	
	public void setSpreadsheet(){
		setLabel(XmlGDataParser.SPS_LABEL);
		setCategory(TERM+getLabel());
	}
	
	public void setPresentation(){
		setLabel(XmlGDataParser.PRS_LABEL);
		setCategory(TERM+getLabel());
	}
	
	public void setFile(){
		setLabel(XmlGDataParser.FILE_LABEL);
		setCategory(TERM+getLabel());
	}


}
