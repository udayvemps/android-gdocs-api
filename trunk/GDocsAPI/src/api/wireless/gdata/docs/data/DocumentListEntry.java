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

import java.util.HashSet;

import api.wireless.gdata.data.Entry;


/**
 * An entry representing a single document of any type within a
 * {@link DocumentListFeed}.
 *
 * 
 * 
 */
public class DocumentListEntry extends Entry {

	/**
	 * Represents the MIME types supported by the doclist GData feed
	 */
	public enum MediaType {
		JPG("image/jpeg"),
		JPEG("image/jpeg"),
		PNG("image/png"),
		BMP("image/bmp"),
		GIF("image/gif"),
		TXT("text/plain"),
		HTML("text/html"),
		HTM("text/html"),
		ODT("application/vnd.oasis.opendocument.text"),
		SXW("application/vnd.sun.xml.writer"),
		DOC("application/msword"),
		RTF("application/rtf"),
		PDF("application/pdf"),
		PPS("application/vnd.ms-powerpoint"),
		PPT("application/vnd.ms-powerpoint"),
		XLS("application/vnd.ms-excel"),
		ODS("application/x-vnd.oasis.opendocument.spreadsheet"),
		CSV("text/csv"),
		TAB("text/tab-separated-value"),
		TSV("text/tab-separated-value"),
		;

		private String mimeType;

		private MediaType(String mimeType) {
			this.mimeType = mimeType;
		}

		public String getMimeType() {
			return mimeType;
		}

		public static MediaType fromFileName(String fileName) {
			int index = fileName.indexOf('.');
			if (index > 0) {
				return valueOf(fileName.substring(index + 1).toUpperCase());
			} else {
				return valueOf(fileName);
			}
		}
	}

	public class DocsNamespace {

		private DocsNamespace() {}

		/** Docs (DOCS) namespace */
		public static final String DOCS = "http://schemas.google.com/docs/2007";

		/** Docs (DOCS) namespace prefix */
		public static final String DOCS_PREFIX = DOCS + "#";

		/** Docs (DOCS) namespace alias */
		public static final String DOCS_ALIAS = "docs";		
		
		public static final String KIND = "kind";

	}

	/**
	 * Label for category.
	 */
	public static final String UNKNOWN_LABEL = "unknown";

	/**
	 * Kind category term used to label the entries which are
	 * of document type.
	 */
	public static final String UNKNOWN_KIND = DocsNamespace.DOCS_PREFIX
	+ DocumentListEntry.UNKNOWN_LABEL;

	public static final String FOLDERS_NAMESPACE =
		DocsNamespace.DOCS + "/folders";

	public static final String PARENT_NAMESPACE =
		DocsNamespace.DOCS_PREFIX + "parent";

	public static final String REVISIONS_NAMESPACE =
		DocsNamespace.DOCS + "/revisions";
	
	
	private String self = null;
    private String lastModifiedBy = null;
    private String lastViewed = null;
    private String resourceId = null;
    private HashSet<String> parents = null;

	/**
	 * Constructs a new uninitialized entry, to be populated by the
	 * GData parsers.
	 */
	public DocumentListEntry() {
		super();
		parents = new HashSet<String>();
	}
	
	/**
	 * Constructs a new uninitialized entry from copy
	 */
	public DocumentListEntry(DocumentListEntry entry) {
		super(entry);
		self = entry.self;
	    lastModifiedBy = entry.lastModifiedBy;
	    lastViewed = entry.lastViewed;
	    resourceId = entry.resourceId;
	    parents = (HashSet<String>) entry.parents.clone();
	}

	//  @Override
	//  public void declareExtensions(ExtensionProfile extProfile) {
	//    super.declareExtensions(extProfile);
	//    extProfile.declare(DocumentListEntry.class, DocumentListAclFeedLink.class);
	//    extProfile.declare(DocumentListEntry.class, LastModifiedBy.class);
	//    extProfile.declare(DocumentListEntry.class, LastViewed.class);
	//    extProfile.declare(DocumentListEntry.class, QuotaBytesUsed.class);
	//    extProfile.declare(DocumentListEntry.class, ResourceId.class);
	//  }

	/**
	 * Gets the non-user-friendly key that is used to access the
	 * document feed.  This is the key that can be used to construct the
	 * Atom id for this document, and to access the document-specific
	 * feed.
	 *
	 * <code>http://docs.google.com/getdoc?id={id}</code>
	 * <code>http://spreadsheets.google.com/ccc?key={id}</code>
	 *
	 * @return the Google Docs &amp; Spreadsheets id
	 */
	public String getKey() {
		String result = getId();
		if (result != null) {
			int position = result.lastIndexOf("/");

			if (position > 0) {
				result = result.substring(position + 1);
			}
		}

		return result;
	}
	
	public static String getKeyfromID(String result) {
		if (result != null) {
			int position = result.lastIndexOf("/");

			if (position > 0) {
				result = result.substring(position + 1);
			}
		}

		return result;
	}

	/**
	 * Returns the time when the document was last viewed by the user.
	 *
	 * @return the last viewed time
	 */
	public String getLastViewed() {		
		return lastViewed  == null ? null : lastViewed;
	}

	/**
	 * Sets the time when the document was last viewed by the user.
	 *
	 * @param lastViewed the last viewed time
	 */
	public void setLastViewed(String lastViewed) {
		this.lastViewed = lastViewed;
	}


	/**
	 * Returns the user who last modified the document.
	 *
	 * @return the user who last modified the document
	 */
	public String getLastModifiedBy() {		
		return lastModifiedBy == null ? null : lastModifiedBy;
	}

	/**
	 * Sets the user who last modified the document.
	 *
	 * @param lastModifiedBy the user who last modified the document.
	 */
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/**
	 * Returns the document's resource id.
	 *
	 * @return the resource id.
	 */
	public String getResourceId() {		
		return resourceId == null ? null : resourceId;
	}

	/**
	 * Sets the document's resource id.
	 *
	 * @param resourceId the resource id.
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getSelfUri() {		
		return self == null ? null : self;
	}
	
	public void setSelfUri(String href) {
		this.self = href;		
	}
	
	public void setParent(String key){
		this.parents.add(key);
	}
	
	public String[] getParents(){
		String[] result = new String[this.parents.size()];
		this.parents.toArray(result);
		return result;
	}
}
