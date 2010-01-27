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
package api.wireless.gdata.docs.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.FileEntity;

import android.util.Log;
import api.wireless.gdata.client.GDataParserFactory;
import api.wireless.gdata.client.GDataServiceClient;
import api.wireless.gdata.client.TokenFactory.ClientLoginAccountType;
import api.wireless.gdata.client.http.GDataRequest;
import api.wireless.gdata.client.http.GDataRequest.RequestType;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.data.Feed;
import api.wireless.gdata.docs.data.DocumentEntry;
import api.wireless.gdata.docs.data.DocumentListEntry;
import api.wireless.gdata.docs.data.FolderEntry;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.util.AuthenticationException;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.ServiceException;
import api.wireless.gdata.util.common.base.StringUtil;


public class DocsClient extends GDataServiceClient {
	private static final String TAG = "DocsClient";
	
	public static final String ALL = "all";
	public static final String DOCS = "documents";
	public static final String PRESENTATIONS = "presentations";
	public static final String SPREADSHEETS = "spreadsheets";
	public static final String PDFS = "pdfs";
	public static final String STARRED = "starred";
	public static final String TRASHED = "trashed";	
	
	/** The name of the service by the protocol. */
	private static final String SERVICE = "writely";
	
	private int feedCollectionSize = FEED_COLLECTION_SIZE;
	
	public DocsClient(DocsGDataClient client, GDataParserFactory docsParserFactory) {
		super(client, docsParserFactory);		
		// Create token handler
		getGDataClient().createTokenFactory(SERVICE);
	}
	
	public String getServiceName() {
		return SERVICE;
	}
	
	public void setFeedCollectionSize(int size) {
		feedCollectionSize = size;
	}
	
	public void setUserCredentials(String user, String pass) throws AuthenticationException{
		getGDataClient().setUserCredentials(user, pass);	
	}
	
	public void setUserCredentials(String user, String pass, ClientLoginAccountType accountType) throws AuthenticationException{
		getGDataClient().setUserCredentials(user, pass, accountType);	
	}
	
	public void IsAuthTokenValid() throws AuthenticationException{
		try {			
			URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED, new String[] {"?title="+SERVICE});
			GDataRequest request = getGDataClient().createFeedRequest(url);			
			request.execute();
		} catch (AuthenticationException e) {
			throw new AuthenticationException("Authentication token is invalid");
		} catch (Exception ex) {
			// Dump everything else
		}
		
		
	}
	
	public String getToken(){
		return getGDataClient().getTokenFactory().getAuthToken().getValue();
	}
	
	public void setToken(String token){
		getGDataClient().setUserToken(token);
	}	

	public HashMap<String,String> getTokens(){
		return getGDataClient().getTokenFactory().getAuthToken().getValues();
	}
	
	public void setTokens(HashMap<String,String> tokens){
		getGDataClient().setUserToken(tokens);
	}	
	
	public FolderEntry getFolderByTitle(String title) 
		throws ServiceException, IOException, ParseException {
		String[] parameters = null;
		try {
			parameters = new String[] {"title="+URLEncoder.encode(title, "UTF-8"), "showfolders=true"};
		} catch (UnsupportedEncodingException e) {
			throw new MalformedURLException("Unable to create parameters");
		}
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED, parameters);
		FolderEntry fld = null;
	
		Feed<FolderEntry> flds = getFeed(FolderEntry.class, url);
		if (flds.getEntries().size() > 0)
			fld = flds.getEntries().get(0);
	
		return fld;
	}

	public List<FolderEntry> getFolders() 
		throws ParseException, ServiceException, IOException{
	    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_FOLDER);		
		return getFeed(FolderEntry.class, url).getEntries();		
	}
	
	

	/**
	 * Gets a feed containing the documents.
	 *
	 * @param category what types of documents to list:
	 *     "all": lists all the doc objects (documents, spreadsheets, presentations)
	 *     "documents": lists only documents.
	 *     "spreadsheets": lists only spreadsheets.
	 *     "pdfs": lists only pdfs.
	 *     "presentations": lists only presentations.
	 *     "starred": lists only starred objects.
	 *     "trashed": lists trashed objects.
	 *
	 * @throws IOException
	 * @throws ServiceException
	 * @throws ParseException 
	 */
	public Feed<DocumentEntry> getFeedOf(String category) 
		throws IOException, ServiceException, ParseException {
		if (category == null) {
			throw new ParseException("null category");
		}
	
		URL url;
	
	    if (category.equals(ALL)) {
	        url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED);
	    } else if (category.equals(DOCS)) {
			url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_DOCUMENT);
		} else if (category.equals(SPREADSHEETS)) {
			url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_SPREADSHEET);
		} else if (category.equals(PDFS)) {
			url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_PDF);
		} else if (category.equals(PRESENTATIONS)) {
			url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_PRESENTATION);
		} else if (category.equals(STARRED)) {
			url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_STARRED);
		} else if (category.equals(TRASHED)) {
			url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + URL_CATEGORY_TRASHED);
		} else {
			return null;
		}
	
		return getFeed(DocumentEntry.class, url, feedCollectionSize);	    
	}
	
	/**
	 * Gets the feed for all the objects contained in a folder.
	 *
	 * @param folderResourceId the resource id of the folder to return the feed
	 *        for the contents.
	 *
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws ServiceException
	 * @throws DocumentListException
	 */
	public Feed<DocumentEntry> getFolderDocsListFeed(String folderResourceId) 
		throws IOException, ServiceException, ParseException {
		
		if (folderResourceId == null) {
			throw new ParseException("null folderResourceId");
		}

		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + folderResourceId + URL_FOLDERS);
		return getFeed(DocumentEntry.class, url, feedCollectionSize);
	}

	public Feed<DocumentEntry> getDocumentsByTitle(String title) 
		throws ServiceException, IOException, ParseException {
		String[] parameters = null;
		try {
			parameters = new String[] {"title="+URLEncoder.encode(title, "UTF-8")};
		} catch (UnsupportedEncodingException e) {
			throw new MalformedURLException("Unable to create parameters");
		}
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED, parameters);
		DocumentEntry doc = null;
		
		Feed<DocumentEntry> docs = getFeed(DocumentEntry.class, url);

		return docs;
	}
	
	public Feed<DocumentEntry> searchDocumentsFullText(String query)
		throws IOException, ServiceException, ParseException {	
		String[] parameters = null;
		try {
			parameters = new String[] {"q="+URLEncoder.encode(query, "UTF-8")};
		} catch (UnsupportedEncodingException e) {
			throw new MalformedURLException("Unable to create parameters");
		}
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED, parameters);
		
		Feed<DocumentEntry> docs = getFeed(DocumentEntry.class, url);
		
		return docs; 
	}

	/**
	 * Creates a new entry with media at the provided feed.  Parses the server response
	 * into the version of the entry stored on the server.
	 * 
	 * @param feedUrl The feed where the entry should be created.
	 * @param entry The entry that should be created.
	 * @return The entry returned by the server as a result of creating the
	 * provided entry.
	 * @throws ParseException Thrown if the server response cannot be parsed.
	 * @throws IOException Thrown if an error occurs while communicating with
	 * the GData service.
	 * @throws ServiceException if the service returns an error response
	 */
	public Entry createEntry(URL feedUrl, Entry entry, InputStream inputStream, String contentType)
	throws ParseException, IOException, ServiceException {
		GDataSerializer serializer = getGDataParserFactory().createSerializer(entry);
		InputStream is = ((DocsGDataClient) getGDataClient()).createCompleteEntry(feedUrl, serializer, inputStream, contentType);
		return parseEntry(entry.getClass(), is);
	}
	
	/**
	 * 
	 * @param feedUrl
	 * @param entry
	 * @param inputStream
	 * @param contentType
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public DocumentEntry createFileEntry(URL feedUrl, String name, InputStream inputStream, String contentType)
	throws ParseException, IOException, ServiceException {		
		InputStream is = ((DocsGDataClient) getGDataClient()).createCompleteEntry(feedUrl, name, inputStream, contentType);
		return parseEntry((new DocumentEntry()).getClass(), is);
	}

	/**
	 * Create new document without content
	 * @param docEntry document entry
	 * @return Created document entry
	 * @throws ParseException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public DocumentEntry createDocument(DocumentEntry docEntry) 
	throws ParseException, IOException, ServiceException {	
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED);		
		return (DocumentEntry) createEntry(url, docEntry);
	}
	/**
	 * Create new document with content
	 * @param docEntry document entry
	 * @param inputStream Stream that contains content of document
	 * @param contentType Content type
	 * @return Created document entry
	 * @throws ParseException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public DocumentEntry createDocument(DocumentEntry docEntry, InputStream inputStream, String contentType) 
	throws ParseException, IOException, ServiceException {		
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED);
		return (DocumentEntry) createEntry(url, docEntry, inputStream, contentType);
	}
	
	/**
	 * Uploads file of any type to server (only works for Premier Accounts)
	 * @param name document name
	 * @param inputStream data stream
	 * @param contentType content type
	 * @return DocumentEntry object representing file
	 * @throws ParseException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public DocumentEntry createFileDocument(String name, InputStream inputStream, String contentType) 
	throws ParseException, IOException, ServiceException {		
		String[] parameters = new String[] {"convert=false"};
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED,parameters);
		return (DocumentEntry) createFileEntry(url, name, inputStream, contentType);
	}

	public DocumentEntry createDocumentInFolder(DocumentEntry docEntry,
			InputStream inputStream, String contentType, String folderUid)
	throws ParseException, IOException, ServiceException {		
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/"+ folderUid + URL_FOLDERS);
		return (DocumentEntry) createEntry(url, docEntry, inputStream, contentType);
	}

	public DocumentEntry getDocument(String resourceId) 
	throws ParseException, IOException, ServiceException {
		if (StringUtil.isEmpty(resourceId)) {
	        throw new ParseException("No document found.");
	    }	
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED+"/"+resourceId);		
		return (DocumentEntry) getEntry(DocumentEntry.class, url, null);
	}
	
	public void trash(DocumentListEntry doc) 
	throws IOException, ServiceException, ParseException {
	    if (StringUtil.isEmpty(doc.getEditUri())) {
	        throw new ParseException("No document ID -- cannot delete.");
	    }		  
	    
	    URL url = new URL(doc.getEditUri());
		deleteEntry(url, doc.getEtag());
	}
	
	public void delete(DocumentListEntry doc) 
	throws IOException, ServiceException, ParseException {
	    if (StringUtil.isEmpty(doc.getEditUri())) {
	        throw new ParseException("No document ID -- cannot delete.");
	    }		  
	    
	    URL url = new URL(doc.getEditUri()+"?delete=true");
		deleteEntry(url, doc.getEtag()); // Delete from folder
	}
	
	/**
	 * Create new folder
	 * @param folderEntry folder entry
	 * @return Updated folder entry
	 * @throws ParseException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public FolderEntry createFolder(FolderEntry folderEntry) 
	throws ParseException, IOException, ServiceException {		
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED);
		return (FolderEntry) createEntry(url, folderEntry);
	}
	
	public FolderEntry getFolder(String resourceId) 
	throws ParseException, IOException, ServiceException {		
		if (StringUtil.isEmpty(resourceId)) {
	        throw new ParseException("No folder found.");
	    }	
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED+"/"+resourceId);		
		
		return (FolderEntry) getEntry(FolderEntry.class, url, null);
	}
	
	
//	public void createEmptyFolder(FolderEntry folderEntry) 
//	throws ParseException, IOException, ServiceException {	
//		
//		GDataSerializer serializer = getGDataParserFactory().createSerializer(folderEntry);
//        if (serializer == null) {
//			throw new NullPointerException("Must supply document");
//		}		
//
//		try {
//			URL fldurl = buildUrl(URL_DOCLIST_FEED);
//			GDataRequest request = getGDataClient().createRequest(RequestType.INSERT, fldurl);						
//			
//			OutputStream os = request.getRequestStream();
//			serializer.serialize(os, GDataSerializer.FORMAT_CREATE);
//			os.flush();
//
//			request.execute();			
//		} catch (ServiceException e) {   
//			Log.e(TAG, e.getMessage(), e);			
//		} catch (ParseException e) {
//			Log.e(TAG, e.getMessage(), e);
//		}		
//	}
	
	/**
	 * Move document into folder
	 * @param doc Document entry
	 * @param folder Folder entry
	 * @return Updated document entry
	 * @throws ParseException
	 * @throws ServiceException
	 * @throws IOException
	 */
	public DocumentEntry moveDocumentIntoFolder(DocumentEntry doc, FolderEntry folder) 
	throws ParseException, ServiceException, IOException {
        String url = folder.getContent();
        if (StringUtil.isEmpty(url)) {
            throw new ParseException("No content URI -- cannot move.");
        }
        
        GDataSerializer serializer = getGDataParserFactory().createSerializer(doc);
        if (serializer == null) {
			throw new NullPointerException("Must supply document");
		}		

		InputStream entryStream = null;
		try {
			URL fldurl = new URL(url);
			GDataRequest request = getGDataClient().createRequest(RequestType.BATCH, fldurl);			
			
			OutputStream os = request.getRequestStream();
			serializer.serialize(os, GDataSerializer.FORMAT_UPDATE);
			os.flush();

			request.execute();
			entryStream = request.getResponseStream();
		} catch (ServiceException e) {   
			Log.e(TAG, e.getMessage(), e);			
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage(), e);
		}
        
        return (DocumentEntry) parseEntry(doc.getClass(), entryStream);		
	}

	public InputStream getDocumentMedia(String resourceId, ContentType ct)
	throws IOException, ServiceException, ParseException {		
	    if (StringUtil.isEmpty(resourceId)) {
	        throw new ParseException("No document found.");
	    }	    
	    
        String[] parameters = {"docID=" + getResourceIdSuffix(resourceId), "exportFormat=" + ContentType.getFileExtension(ct)};
        URL url = buildUrl(URL_DOWNLOAD + "/documents" + URL_CATEGORY_EXPORT, parameters);	    
	    return ((DocsGDataClient)getGDataClient()).getMediaEntryAsStream(url, ct);
	}
	

	public InputStream getDocumentMediaAsHTML(String resourceId)
    throws IOException, ServiceException, ParseException {
        if (StringUtil.isEmpty(resourceId)) {
            throw new ParseException("No document found.");
        }        
        return getDocumentMedia(resourceId, ContentType.TEXT_HTML);
	}	
	
	public InputStream getDocumentMediaAsTXT(String resourceId)
    throws IOException, ServiceException, ParseException {
		if (StringUtil.isEmpty(resourceId)) {
            throw new ParseException("No document found.");
        }        
        return getDocumentMedia(resourceId, ContentType.TEXT_PLAIN);        
	}	
	
	public InputStream getPresentationMedia(String resourceId, ContentType ct)
    throws IOException, ServiceException, ParseException {		
        if (StringUtil.isEmpty(resourceId)) {
            throw new ParseException("No document found.");
        }
        
        String[] parameters = {"docID=" + getResourceIdSuffix(resourceId), "exportFormat=" + ContentType.getFileExtension(ct)};        	    
        URL url = buildUrl(URL_DOWNLOAD + "/presentations" + URL_CATEGORY_EXPORT, parameters);

	    return ((DocsGDataClient)getGDataClient()).getMediaEntryAsStream(url, ct);        
	}
	
	public DocumentEntry OCRDocument(DocumentEntry doc, InputStream inputStream, String contentType)
		throws IOException, ServiceException, ParseException {		
		
		String[] parameters = {"ocr=true"};        	            		
		URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED, parameters);
				
		return (DocumentEntry) createEntry(url, doc, inputStream, contentType);
	}
	
	public InputStream GViewDocument(String resourceId, int page, int res)
		throws IOException, ServiceException, ParseException {		
	
		String[] parameters = {
				"pid=explorer", 
				"a=bi", 
				String.format("pagenumber=%d", page),
				String.format("w=%d", res),
				String.format("srcid=%s",getResourceIdSuffix(resourceId)),
				};        	    
		
		// Hosted
		boolean isHosted = getGDataClient().getTokenFactory().getAccountType().compareTo(ClientLoginAccountType.HOSTED_OR_GOOGLE) == 0;
		String udomain = "";
		if (isHosted){
			udomain = getGDataClient().getTokenFactory().getUserDomain();
			if (udomain.length()>0) udomain = "/a/"+udomain;
		}
		
		URL url = buildAnyUrl(DocsGDataClient.DEFAULT_HOST + udomain, "/gview", parameters);
			
		return getMediaEntry(url, isHosted);
	}
		
}
