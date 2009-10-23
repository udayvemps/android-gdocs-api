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
package api.wireless.gdata.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.data.Feed;
import api.wireless.gdata.docs.data.DocumentEntry;
import api.wireless.gdata.parser.GDataParser;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.ServiceException;
import api.wireless.gdata.util.common.base.StringUtil;

/**
 * Abstract base class for service-specific clients to access GData feeds.
 */
public abstract class GDataServiceClient {

	protected final String URL_FEED = "/feeds";
	protected final String URL_DOWNLOAD = "/download";
	protected final String URL_DOCLIST_FEED = "/private/full";

	protected final String URL_DEFAULT = "/default";
	protected final String URL_FOLDERS = "/contents";
	protected final String URL_ACL = "/acl";
	protected final String URL_REVISIONS = "/revisions";

	protected final String URL_CATEGORY_DOCUMENT = "/-/document";
	protected final String URL_CATEGORY_SPREADSHEET = "/-/spreadsheet";
	protected final String URL_CATEGORY_PDF = "/-/pdf";
	protected final String URL_CATEGORY_PRESENTATION = "/-/presentation";
	protected final String URL_CATEGORY_STARRED = "/-/starred";
	protected final String URL_CATEGORY_TRASHED = "/-/trashed";
	protected final String URL_CATEGORY_FOLDER = "/-/folder";
	protected final String URL_CATEGORY_EXPORT = "/Export";

	protected final String PARAMETER_SHOW_FOLDERS = "showfolders=true";


	private final ServiceDataClient gDataClient;
	private final GDataParserFactory gDataParserFactory;

	public GDataServiceClient(ServiceDataClient gDataClient,
			GDataParserFactory gDataParserFactory) {
		this.gDataClient = gDataClient;
		this.gDataParserFactory = gDataParserFactory;
	}

	/**
	 * Returns the {@link GDataClient} being used by this GDataServiceClient.
	 * @return The {@link GDataClient} being used by this GDataServiceClient.
	 */
	protected ServiceDataClient getGDataClient() {
		return gDataClient;
	}

	/**
	 * Returns the {@link GDataParserFactory} being used by this
	 * GDataServiceClient.
	 * @return The {@link GDataParserFactory} being used by this
	 * GDataServiceClient.
	 */
	protected GDataParserFactory getGDataParserFactory() {
		return gDataParserFactory;
	}

	/**
	 * Returns the name of the service.  Used for authentication.
	 * @return The name of the service.
	 */
	public abstract String getServiceName();


	/**
	 * Fetches a feed for this user.  The caller is responsible for closing the
	 * returned {@link GDataParser}.
	 *
	 * @param feedEntryClass the class of Entry that is contained in the feed
	 * @param feedUrl ThAe URL of the feed that should be fetched.
	 * @param eTag The authentication token for this user.
	 * @return A {@link GDataParser} for the requested feed.
	 * @throws ParseException Thrown if the server response cannot be parsed.
	 * @throws IOException Thrown if an error occurs while communicating with
	 * the GData service.
	 * @throws HttpException Thrown if the http response contains a result other than 2xx
	 */
	public GDataParser getParserForFeed(Class feedEntryClass, URL feedUrl, String eTag)
	throws ParseException, IOException, ServiceException {
		InputStream is = gDataClient.getFeedAsStream(feedUrl, eTag);
		return gDataParserFactory.createParser(feedEntryClass, is);
	}

	/**
	 * Returns a parser for the specified feed type.
	 * 
	 * @param feedEntryClass the Class of entry type that will be parsed. This lets this
	 *   method figure out which parser to create.
	 * @param feedUri the URI of the feed to be fetched and parsed
	 * @throws IOException 
	 * @throws ServiceException if an http error is encountered 
	 * @throws ParseException if the response from the server could not be
	 *         parsed
	 */
	public GDataParser getParserForTypedFeed(Class feedEntryClass, URL feedUri) 
	throws ServiceException, IOException, ParseException {

		InputStream is = gDataClient.getFeedAsStream(feedUri, (String) null);	
		return gDataParserFactory.createParser(feedEntryClass, is);
	}

	public <T extends Entry> Collection<T> getFeed(Class<T> entryClass, URL feedUrl) 
	throws ServiceException, IOException, ParseException {

		ArrayList<T> entries = new ArrayList<T>();
		GDataParser parser = null;

		try{
			parser = getParserForTypedFeed(entryClass, feedUrl);

			Feed feed = parser.init();
			// go thru the entries and pick information
			boolean nextEntry = true;
			while(nextEntry)
			{
				T entry = null;
				entry = (T) parser.readNextEntry(entry);
				if (entry != null) 
					entries.add(entry);
				else
					nextEntry = false;
			}
		}
		finally{
			if (parser != null){				
				//parser.close(); //TODO: API Error LocalCloseInputStream
				parser = null;
			}
		}

		return entries;
	}	

	/**
	 * Creates a new entry at the provided feed.  Parses the server response
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
	public Entry createEntry(URL feedUrl, Entry entry)
	throws ParseException, IOException, ServiceException {
		GDataSerializer serializer = gDataParserFactory.createSerializer(entry);
		InputStream is = gDataClient.createEntry(feedUrl, serializer);
		return parseEntry(entry.getClass(), is);
	}	

	/**
	 * Fetches an existing entry.
	 * @param entryClass the type of entry to expect
	 * @param id of the entry to fetch.
	 * @param eTag The authentication token for this user. @return The entry returned by the server.
	 * @throws ParseException Thrown if the server response cannot be parsed.
	 * @throws ServiceException if the service returns an error response
	 * @throws IOException Thrown if an error occurs while communicating with
	 * the GData service.
	 * @return The entry returned by the server
	 */
	public Entry getEntry(Class entryClass, URL url, String eTag)
	throws ParseException, IOException, ServiceException {
		InputStream is = gDataClient.getFeedAsStream(url, eTag);                  
		return parseEntry(entryClass, is);
	}

	/**
	 * Fetches a media entry as an InputStream.  The caller is responsible for closing the
	 * returned {@link InputStream}.
	 *
	 * @param mediaEntryUrl The URL of the media entry that should be fetched.
	 * @param eTag The authentication token for this user.
	 * @return A {@link InputStream} for the requested media entry.
	 * @throws IOException Thrown if an error occurs while communicating with
	 * the GData service.
	 */
	public InputStream getMediaEntry(URL mediaEntryUrl, String eTag, ContentType ct)
	throws IOException, ServiceException {
		return gDataClient.getMediaEntryAsStream(mediaEntryUrl, eTag, ct);
	}

	/**
	 * Updates an existing entry.  Parses the server response into the version
	 * of the entry stored on the server.
	 *
	 * @param entry The entry that should be updated.
	 * @param eTag The authentication token for this user.
	 * @return The entry returned by the server as a result of updating the
	 * provided entry.
	 * @throws ParseException Thrown if the server response cannot be parsed.
	 * @throws IOException Thrown if an error occurs while communicating with
	 * the GData service.
	 * @throws ServiceException if the service returns an error response
	 */
	public Entry updateEntry(Entry entry, String eTag)
	throws ParseException, IOException, ServiceException {
		String editUri = entry.getEditUri();
		if (StringUtil.isEmpty(editUri)) {
			throw new ParseException("No edit URI -- cannot update.");
		}

		GDataSerializer serializer = gDataParserFactory.createSerializer(entry);
		InputStream is = gDataClient.updateEntry(new URL(editUri), eTag, serializer);
		return parseEntry(entry.getClass(), is);
	}

	/**
	 * Updates an existing entry.  Parses the server response into the metadata
	 * of the entry stored on the server.
	 *
	 * @param editUri The URI of the resource that should be updated.
	 * @param inputStream The {@link java.io.InputStream} that contains the new value
	 *   of the media entry
	 * @param contentType The content type of the new media entry
	 * @param eTag The authentication token.
	 * @return The entry returned by the server as a result of updating the
	 * provided entry.
	 * @throws ServiceException if the service returns an error response
	 * @throws ParseException Thrown if the server response cannot be parsed.
	 * @throws IOException Thrown if an error occurs while communicating with
	 * the GData service.
	 */
	public DocumentEntry updateMediaEntry(URL editUri, String eTag, InputStream inputStream, ContentType contentType) 
	throws IOException, ServiceException, ParseException {
		if (editUri == null) {
			throw new IllegalArgumentException("No edit URI -- cannot update.");
		}

		InputStream is = gDataClient.updateMediaEntry(editUri, eTag, inputStream, contentType);
		return (DocumentEntry) parseEntry(DocumentEntry.class, is);
	}

	/**
	 * Deletes an existing entry.
	 *
	 * @param editUri The editUri for the entry that should be deleted.
	 * @param eTag The authentication token for this user.
	 * @throws IOException Thrown if an error occurs while communicating with
	 * the GData service.
	 * @throws ServiceException if the service returns an error response
	 */
	public void deleteEntry(URL editUri, String eTag)
	throws IOException, ServiceException {
		gDataClient.deleteEntry(editUri, eTag);
	}

	protected Entry parseEntry(Class entryClass, InputStream is) 
	throws ParseException, IOException {
		GDataParser parser = null;
		try {
			parser = gDataParserFactory.createParser(entryClass, is);
			return parser.parseStandaloneEntry();
		} finally {
			if (parser != null) {
				//parser.close(); //TODO: API Error LocalCloseInputStream
			}
		}
	}


	/**
	 * Gets the suffix of the resourceId. If the resourceId is
	 * "document:dh3bw3j_0f7xmjhd8", "dh3bw3j_0f7xmjhd8" will be returned.
	 *
	 * @param resourceId the resource id to extract the suffix from.
	 *
	 * @throws DocumentListException
	 */
	public String getResourceIdSuffix(String resourceId) throws ServiceException {
		if (resourceId == null) {
			throw new ServiceException("null resourceId");
		}

		if (resourceId.indexOf("%3A") != -1) {
			return resourceId.substring(resourceId.lastIndexOf("%3A") + 3);
		} else if (resourceId.indexOf(":") != -1) {
			return resourceId.substring(resourceId.lastIndexOf(":") + 1);
		}
		throw new ServiceException("Bad resourceId");
	}

	/**
	 * Gets the prefix of the resourceId. If the resourceId is
	 * "document:dh3bw3j_0f7xmjhd8", "document" will be returned.
	 *
	 * @param resourceId the resource id to extract the suffix from.
	 *
	 * @throws DocumentListException
	 */
	public String getResourceIdPrefix(String resourceId) throws ServiceException {
		if (resourceId == null) {
			throw new ServiceException("null resourceId");
		}

		if (resourceId.indexOf("%3A") != -1) {
			return resourceId.substring(0, resourceId.indexOf("%3A"));
		} else if (resourceId.indexOf(":") != -1) {
			return resourceId.substring(0, resourceId.indexOf(":"));
		} else {
			throw new ServiceException("Bad resourceId");
		}
	}

	/**
	 * Builds a URL from a patch.
	 *
	 * @param path the path to add to the protocol/host
	 *
	 * @throws MalformedURLException
	 * @throws DocumentListException
	 */
	public URL buildUrl(String path) throws MalformedURLException, ServiceException {
		if (path == null) {
			throw new ServiceException("null path");
		}

		return buildUrl(path, null);
	}

	/**
	 * Builds a URL with parameters.
	 *
	 * @param path the path to add to the protocol/host
	 * @param parameters parameters to be added to the URL.
	 *
	 * @throws MalformedURLException
	 * @throws DocumentListException
	 */
	public URL buildUrl(String path, String[] parameters)
	throws MalformedURLException, ServiceException {
		if (path == null) {
			throw new ServiceException("null path");
		}

		return buildUrl(gDataClient.host, path, parameters);
	}

	/**
	 * Builds a URL with parameters.
	 *
	 * @param domain the domain of the server
	 * @param path the path to add to the protocol/host
	 * @param parameters parameters to be added to the URL.
	 *
	 * @throws MalformedURLException
	 * @throws DocumentListException
	 */
	public URL buildUrl(String domain, String path, String[] parameters)
	throws MalformedURLException, ServiceException {
		if (path == null) {
			throw new ServiceException("null path");
		}

		StringBuffer url = new StringBuffer();
		url.append(gDataClient.protocol + "://" + domain + URL_FEED + path);

		if (parameters != null && parameters.length > 0) {
			url.append("?");
			for (int i = 0; i < parameters.length; i++) {
				url.append(parameters[i]);
				if (i != (parameters.length - 1)) {
					url.append("&");
				}
			}
		}

		return new URL(url.toString());
	}

	/**
	 * Builds a URL with parameters.
	 *
	 * @param domain the domain of the server
	 * @param path the path to add to the protocol/host
	 * @param parameters parameters to be added to the URL as key value pairs.
	 * @throws MalformedURLException 
	 *
	 * @throws MalformedURLException
	 * @throws DocumentListException
	 */
	public URL buildUrl(String domain, String path, Map<String, String> parameters)
	throws ServiceException, MalformedURLException {
		if (path == null) {
			throw new ServiceException("null path");
		}

		StringBuffer url = new StringBuffer();
		url.append(gDataClient.protocol + "://" + domain + URL_FEED + path);

		if (parameters != null && parameters.size() > 0) {
			Set<Map.Entry<String, String>> params = parameters.entrySet();
			Iterator<Map.Entry<String, String>> itr = params.iterator();

			url.append("?");
			while (itr.hasNext()) {
				Map.Entry<String, String> entry = itr.next();
				url.append(entry.getKey() + "=" + entry.getValue());
				if (itr.hasNext()) {
					url.append("&");
				}
			}
		}

		return new URL(url.toString());
	}

}
