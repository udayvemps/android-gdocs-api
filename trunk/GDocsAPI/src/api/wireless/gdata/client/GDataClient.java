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
// Copyright 2007 The Android Open Source Project

package api.wireless.gdata.client;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import api.wireless.gdata.serializer.GDataSerializer;
import api.wireless.gdata.util.ContentType;
import api.wireless.gdata.util.ServiceException;

/**
 * Interface for interacting with a GData server.  Specific platforms can
 * provide their own implementations using the available networking and HTTP
 * stack for that platform.
 */
public interface GDataClient {

    /**
     * Closes this GDataClient, cleaning up any resources, persistent connections, etc.,
     * it may have.
     */
    void close();


    /**
	 * Connects to a GData server (specified by the feedUrl) and creates a new
	 * entry.  The response from the server is returned as an 
	 * {@link InputStream}.  The caller is responsible for calling
	 * {@link InputStream#close()} on the returned {@link InputStream}.
	 * 
	 * @param feedUrl The feed url where the entry should be created.
	 * @param authToken The authentication token that should be used when 
	 * creating the entry.
	 * @param entry The entry that should be created.
	 * @throws IOException Thrown if an io error occurs while communicating with
	 * the service.
	 * @throws HttpException if the service returns an error response.
	 */
	InputStream createEntry(URL feedUrl, GDataSerializer entry)
	    throws ServiceException, IOException;


	/**
     * Connects to a GData server (specified by the feedUrl) and fetches the
     * specified feed as an InputStream.  The caller is responsible for calling
     * {@link InputStream#close()} on the returned {@link InputStream}.
     *
     * @param feedUrl The feed that should be fetched.
     * @param eTag The authentication token that should be used when
     * fetching the feed.
     * @return An InputStream for the feed.
     * @throws IOException Thrown if an io error occurs while communicating with
     * the service.
     * @throws HttpException if the service returns an error response.
     * @throws ServiceException 
     */
    InputStream getFeedAsStream(URL feedUrl,
                                String eTag)
        throws IOException, ServiceException;

    // TODO: support batch update
	
	/**
	 * Connects to a GData server (specified by the mediaEntryUrl) and fetches the
	 * specified media entry as an InputStream.  The caller is responsible for calling
	 * {@link InputStream#close()} on the returned {@link InputStream}.
	 *
	 * @param mediaEntryUrl The media entry that should be fetched.
	 * @param authToken The authentication token that should be used when
	 * fetching the media entry.
	 * @return An InputStream for the media entry.
	 * @throws IOException Thrown if an io error occurs while communicating with
	 * the service.
	 * @throws HttpException if the service returns an error response.
	 */
	InputStream getMediaEntryAsStream(URL mediaEntryUrl, String eTag, ContentType ct)
	    throws ServiceException, IOException;


	/**
     * Connects to a GData server (specified by the editUri) and updates an
     * existing entry.  The response from the server is returned as an 
     * {@link InputStream}.  The caller is responsible for calling
     * {@link InputStream#close()} on the returned {@link InputStream}.
     * 
     * @param editUri The edit uri that should be used for updating the entry.
     * @param authToken The authentication token that should be used when 
     * updating the entry.
     * @param entry The entry that should be updated.
     * @throws IOException Thrown if an io error occurs while communicating with
     * the service.
     * @throws HttpException if the service returns an error response.
     */
    InputStream updateEntry(URL editUri,
                            String eTag,
                            GDataSerializer entry)
        throws ServiceException, IOException;


    /**
     * Connects to a GData server (specified by the editUri) and updates an
     * existing media entry.  The response from the server is returned as an
     * {@link InputStream}.  The caller is responsible for calling
     * {@link InputStream#close()} on the returned {@link InputStream}.
     *
     * @param editUri The edit uri that should be used for updating the entry.
     * @param authToken The authentication token that should be used when
     * updating the entry.
     * @param mediaEntryInputStream The {@link InputStream} that contains the new
     *   value of the resource
     * @param contentType The contentType of the new media entry
     * @throws IOException Thrown if an io error occurs while communicating with
     * the service.
     * @throws HttpException if the service returns an error response.
     * @return The {@link InputStream} that contains the metadata associated with the
     *   new version of the media entry.
     */
    public InputStream updateMediaEntry(URL editUri, String eTag,
            InputStream mediaEntryInputStream, ContentType contentType)
        throws ServiceException, IOException;
    
    /**
     * Connects to a GData server (specified by the editUri) and deletes an
     * existing entry.
     * 
     * @param editUri The edit uri that should be used for deleting the entry.
     * @param authToken The authentication token that should be used when
     * deleting the entry.
     * @throws IOException Thrown if an io error occurs while communicating with
     * the service.
     * @throws HttpException if the service returns an error response.
     */
    void deleteEntry(URL editUri,
                     String eTag)
        throws ServiceException, IOException;
}
