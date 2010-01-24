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
package api.wireless.gdata.data;

import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.util.common.base.StringUtil;

/**
 * Entry in a GData feed.
 */
// TODO: make this an interface?
// allow for writing directly into data structures used by native PIM, etc.,
// APIs.
// TODO: comment that setId(), etc., only used for parsing code.
public class Entry {
    private String id = null;
    private String etag = null;
    private String title = null;
    private String editUri = null;
    private String htmlUri = null;
    private String summary = null;
    private String content = null;
    private String contentType = null;
    private String author = null;
    private String email = null;
    private String category = null;
    private String categoryScheme = null;
    private String publicationDate = null;
    private String updateDate = null;
    private boolean deleted = false;
    private boolean starred = false;
    private String label = null;
    private String mime = null;
    
    /**
     * Creates a new empty entry.
     */
    public Entry() {
    }
    
    /**
     * Copy constructor
     */
    public Entry(Entry entry) {
        id = entry.id;
        etag = entry.etag;
        title = entry.title;
        editUri = entry.editUri;
        htmlUri = entry.htmlUri;
        summary = entry.summary;
        content = entry.content;
        contentType = entry.contentType;
        author = entry.author;
        email = entry.email;
        category = entry.category;
        categoryScheme = entry.categoryScheme;
        publicationDate = entry.publicationDate;
        updateDate = entry.updateDate;
        deleted = entry.deleted;
        starred = entry.starred;
        label = entry.label;
    }

    /**
     * Clears all the values in this entry.
     */
    public void clear() {
        id = null;
        etag = null;
        title = null;
        editUri = null;
        htmlUri = null;
        summary = null;
        content = null;
        contentType = null;
        author = null;
        email = null;
        category = null;
        categoryScheme = null;
        publicationDate = null;
        updateDate = null;
        deleted = false;
        starred = false;
        label = null;
        mime = null;
    }
    
    public String getEtag() { return etag; }
    public void setEtag(String v) { etag = v; }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the categoryScheme
     */
    public String getCategoryScheme() {
        return categoryScheme;
    }

    /**
     * @param categoryScheme the categoryScheme to set
     */
    public void setCategoryScheme(String categoryScheme) {
        this.categoryScheme = categoryScheme;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param content the content type to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }


    /**
     * @return the editUri
     */
    public String getEditUri() {
        return editUri;
    }

    /**
     * @param editUri the editUri to set
     */
    public void setEditUri(String editUri) {
        this.editUri = editUri;
    }

    /**
     * @return The uri for the HTML version of this entry.
     */
    public String getHtmlUri() {
        return htmlUri;
    }

    /**
     * Set the uri for the HTML version of this entry.
     * @param htmlUri The uri for the HTML version of this entry.
     */
    public void setHtmlUri(String htmlUri) {
        this.htmlUri = htmlUri;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the publicationDate
     */
    public String getPublicationDate() {
        return publicationDate;
    }

    /**
     * @param publicationDate the publicationDate to set
     */
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the updateDate
     */
    public String getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return true if this entry represents a tombstone
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param isDeleted true if the entry is deleted
     */
    public void setDeleted(boolean isDeleted) {
        deleted = isDeleted;
    }
    
    /**
     * @return true if this entry represents is starred
     */
    public boolean isStarred() {
        return starred;
    }

    /**
     * @param isStarred true if the entry is starred
     */
    public void setStarred(boolean isStarred) {
        starred = isStarred;
    }
 
    /**
     * Appends the name and value to this StringBuffer, if value is not null.
     * Uses the format: "<NAME>: <VALUE>\n"
     * @param sb The StringBuffer in which the name and value should be
     * appended.
     * @param name The name that should be appended.
     * @param value The value that should be appended.
     */
    protected void appendIfNotNull(StringBuffer sb,
                                   String name, String value) {
        if (!StringUtil.isEmpty(value)) {
            sb.append(name);
            sb.append(": ");
            sb.append(value);
            sb.append("\n");
        }
    }

    /**
     * Helper method that creates the String representation of this Entry.
     * Called by {@link #toString()}.
     * Subclasses can add additional data to the StringBuffer.
     * @param sb The StringBuffer that should be modified to add to the String
     * representation of this Entry.
     */
    protected void toString(StringBuffer sb) {
        appendIfNotNull(sb, "ID", id);
        appendIfNotNull(sb, "ETAG", etag);
        appendIfNotNull(sb, "TITLE", title);
        appendIfNotNull(sb, "LABEL", label);
        appendIfNotNull(sb, "EDIT URI", editUri);
        appendIfNotNull(sb, "HTML URI", htmlUri);        
        appendIfNotNull(sb, "CONTENT", content);
        appendIfNotNull(sb, "CONTENT TYPE", contentType);
        appendIfNotNull(sb, "AUTHOR", author);
        appendIfNotNull(sb, "CATEGORY", category);
        appendIfNotNull(sb, "CATEGORY SCHEME", categoryScheme);
        appendIfNotNull(sb, "PUBLICATION DATE", publicationDate);
        appendIfNotNull(sb, "UPDATE DATE", updateDate);
        appendIfNotNull(sb, "DELETED", String.valueOf(deleted));
        appendIfNotNull(sb, "STARRED", String.valueOf(starred));
    }

    /**
     * Creates a StringBuffer and calls {@link #toString(StringBuffer)}.  The
     * return value for this method is simply the result of calling
     * {@link StringBuffer#toString()} on this StringBuffer.  Mainly used for
     * debugging.
     */
    @Override
	public String toString() {
        StringBuffer sb = new StringBuffer();
        toString(sb);
        return sb.toString();
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * @return the mime type of document
     */
    public String getMime() {
        return mime;
    }

    /**
     * @param the mime type to set
     */
    public void setMime(String mime) {
        this.mime = mime;
    }

    public void validate() throws ParseException {
    }
}
