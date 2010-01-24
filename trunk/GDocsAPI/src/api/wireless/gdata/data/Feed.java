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

import java.util.LinkedList;
import java.util.List;

/**
 * Class containing information about a GData feed.  Note that this feed does
 * not contain any of the entries in that feed -- the entries are yielded
 * separately from this Feed.
 */
public class Feed<E extends Entry> {
    private int startIndex;
    private String title;
    private String id;
    private String lastUpdated;
    private String category;
    private String categoryScheme;
    private String next;
    private String eTag;
    
    /**
     * Class used to construct new entry instance, initialized at construction.
     */
    protected Class<? extends E> entryClass;

    /** Feed entries. */
    protected List<E> entries = new LinkedList<E>();

    /**
     * Creates a new, empty feed.
     */
    public Feed() {
    }   

    
    /** Returns the list of entries in this feed */
    public List<E> getEntries() {
      return entries;
    }

    /** Sets the list to use for storing the entry list */
    public void setEntries(List<E> entryList) { this.entries = entryList; }
    
    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
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
     * @return the lastUpdated
     */
    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param lastUpdated the lastUpdated to set
     */
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
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
     * @return feed link to rest of the documents
     */
    public String getNext(){
    	return next;
    }
    
    /** 
     * @return set next feed link
     */
    public void setNext(String next){
    	this.next = next;
    }
    
    /** 
     * @return Feed etag
     */
    public String getEtag(){
    	return eTag;
    }
    
    /**
     * Set feed etag
     * @param eTag
     */
    public void setEtag(String eTag){
    	this.eTag = eTag;
    }

}
