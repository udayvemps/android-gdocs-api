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
package api.wireless.gdata.spreadsheets.client;

import android.net.Uri;
import api.wireless.gdata.client.GDataParserFactory;
import api.wireless.gdata.client.GDataServiceClient;
import api.wireless.gdata.client.HttpException;
import api.wireless.gdata.client.ServiceDataClient;
import api.wireless.gdata.client.TokenFactory.ClientLoginAccountType;
import api.wireless.gdata.client.http.GDataRequest;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.parser.GDataParser;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.spreadsheets.data.CellEntry;
import api.wireless.gdata.spreadsheets.data.ListEntry;
import api.wireless.gdata.spreadsheets.data.SpreadsheetEntry;
import api.wireless.gdata.spreadsheets.data.WorksheetEntry;
import api.wireless.gdata.util.AuthenticationException;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.ServiceException;
import api.wireless.gdata.util.common.base.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * GDataServiceClient for accessing Google Spreadsheets. This client can
 * access and parse all of the Spreadsheets feed types: Spreadsheets feed,
 * Worksheets feed, List feed, and Cells feed. Read operations are supported
 * on all feed types, but only the List and Cells feeds support write
 * operations. (This is a limitation of the protocol, not this API. Such write
 * access may be added to the protocol in the future, requiring changes to
 * this implementation.)
 * 
 */
public class SpreadsheetsClient extends GDataServiceClient {
	
	public static final String SPREADSHEETS_HOST = "spreadsheets.google.com";
	
    /** The name of the service, dictated to be 'wise' by the protocol. */
    private static final String SERVICE = "wise";

	private static final String URL_SPREADSHEETS = "/spreadsheets";

    /**
     * Create a new SpreadsheetsClient.
     *
     * @param client The GDataClient that should be used to authenticate
     *        requests, retrieve feeds, etc.
     * @param spreadsheetFactory The GDataParserFactory that should be used to obtain GDataParsers
     * used by this client.
     * @param baseFeedUrl The base URL for spreadsheets feeds.
     */
    public SpreadsheetsClient(ServiceDataClient client, GDataParserFactory spreadsheetFactory) {
        super(client, spreadsheetFactory);
        getGDataClient().createTokenFactory(SERVICE);
    }

    /* (non-Javadoc)
     * @see GDataServiceClient#getServiceName
     */
    public String getServiceName() {
        return SERVICE;
    }
    
	public void setUserCredentials(String user, String pass) throws AuthenticationException{
		getGDataClient().setUserCredentials(user, pass);	
	}
	
	public void setUserCredentials(String user, String pass, ClientLoginAccountType accountType) throws AuthenticationException{
		getGDataClient().setUserCredentials(user, pass, accountType);	
	}
		
	public void IsAuthTokenValid() throws AuthenticationException{
		try {			
			
			URL url = buildUrl(SPREADSHEETS_HOST, URL_SPREADSHEETS+URL_DOCLIST_FEED, (String[])null);
			//new URL("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
			GDataRequest request = getGDataClient().createFeedRequest(url);			
			request.execute();
		} catch (Exception e) {
			throw new AuthenticationException("Authentication token is invalid");
		}		
	}
	
	public String getToken(){
		return getGDataClient().getTokenFactory().getAuthToken().getValue();
	}
	
	public void setToken(String token){
		getGDataClient().setUserToken(token);
	}

    /**
     * Returns a parser for a Cells-based feed.
     *
     * @param feedUri the URI of the feed to be fetched and parsed
     * @param authToken the current authToken to use for the request
     * @return a parser for the indicated feed
     * @throws HttpException if an http error is encountered
     * @throws ParseException if the response from the server could not be
     *         parsed
     */
    public GDataParser getParserForCellsFeed(URL feedUri)
            throws ParseException, IOException, ServiceException {
        return getParserForTypedFeed(CellEntry.class, feedUri);
    }


    /**
     * Returns a parser for a List (row-based) feed.
     * 
     * @param feedUri the URI of the feed to be fetched and parsed
     * @param authToken the current authToken to use for the request
     * @return a parser for the indicated feed
     * @throws HttpException if an http error is encountered
     * @throws ParseException if the response from the server could not be
     *         parsed
     */
    public GDataParser getParserForListFeed(URL feedUri)
            throws ParseException, IOException, ServiceException {
        return getParserForTypedFeed(ListEntry.class, feedUri);
    }

    /**
     * Returns a parser for a Spreadsheets meta-feed.
     * 
     * @param feedUri the URI of the feed to be fetched and parsed
     * @param authToken the current authToken to use for the request
     * @return a parser for the indicated feed
     * @throws HttpException if an http error is encountered
     * @throws ParseException if the response from the server could not be
     *         parsed
     */
    public GDataParser getParserForSpreadsheetsFeed(URL feedUri)
            throws ParseException, IOException, ServiceException {
        return getParserForTypedFeed(SpreadsheetEntry.class, feedUri);
    }

    /**
     * Returns a parser for a Worksheets meta-feed.
     * 
     * @param feedUri the URI of the feed to be fetched and parsed
     * @param authToken the current authToken to use for the request
     * @return a parser for the indicated feed
     * @throws HttpException if an http error is encountered
     * @throws ParseException if the response from the server could not be
     *         parsed
     */
    public GDataParser getParserForWorksheetsFeed(URL feedUri)
            throws ParseException, IOException, ServiceException {
        return getParserForTypedFeed(WorksheetEntry.class, feedUri);
    }

    /**
     * Updates an entry. The URI to be updated is taken from
     * <code>entry</code>. Note that only entries in List and Cells feeds
     * can be updated, so <code>entry</code> must be of the corresponding
     * type; other types will result in an exception.
     * 
     * @param entry the entry to be updated; must include its URI
     * @param authToken the current authToken to be used for the operation
     * @return An Entry containing the re-parsed version of the entry returned
     *         by the server in response to the update.
     * @throws HttpException if an http error is encountered
     * @throws ParseException if the server returned an error, if the server's
     *         response was unparseable (unlikely), or if <code>entry</code>
     *         is of a read-only type
     * @throws IOException on network error
     */
    public Entry updateEntry(Entry entry, String authToken)
            throws ParseException, IOException, ServiceException {
        GDataParserFactory factory = getGDataParserFactory();
        GDataSerializer serializer = factory.createSerializer(entry);

        String editUri = entry.getEditUri();
        if (StringUtil.isEmpty(editUri)) {
            throw new ServiceException("No edit URI -- cannot update.");
        }

        InputStream is = getGDataClient().updateEntry(new URL(editUri), authToken, serializer);
        GDataParser parser = factory.createParser(entry.getClass(), is);
        try {
            return parser.parseStandaloneEntry();
        } finally {
            parser.close();
        }
    }
    
	public InputStream getMediaEntry(String resourceId, ContentType ct)
	throws IOException, ServiceException, ParseException {		
	    if (StringUtil.isEmpty(resourceId)) {
	        throw new ServiceException("No document found.");
	    }	    
	    
	    HashMap<String, String> parameters = new HashMap<String, String>();	    
	    parameters.put("exportFormat", ContentType.getFileExtension(ct));
	    parameters.put("key", Uri.encode(resourceId));

	    // If exporting to .csv or .tsv, add the gid parameter to specify which sheet to export
	    if (ct.equals(ContentType.CSV) || ct.equals(ContentType.TSV)) {
	      parameters.put("gid", "0"); // download only the first sheet
	    }

	    URL url = buildUrl(SPREADSHEETS_HOST, URL_DOWNLOAD + URL_SPREADSHEETS + URL_CATEGORY_EXPORT, parameters);
	    
	    return ((SpreadsheetGDataClient)getGDataClient()).getMediaEntryAsStream(url, ct);
	}

	public InputStream getMediaEntryAsHTML(String resourceId)
    throws IOException, ServiceException, ParseException {		
        if (StringUtil.isEmpty(resourceId)) {
            throw new ServiceException("No document found.");
        }        
        return getMediaEntry(resourceId, ContentType.TEXT_HTML);
	}

}
