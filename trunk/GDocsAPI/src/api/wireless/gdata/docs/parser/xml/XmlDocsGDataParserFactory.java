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
package api.wireless.gdata.docs.parser.xml;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import api.wireless.gdata.client.GDataParserFactory;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.docs.data.DocumentEntry;
import api.wireless.gdata.docs.data.DocumentListEntry;
import api.wireless.gdata.docs.data.FolderEntry;
import api.wireless.gdata.docs.serializer.xml.XmlDocumentEntryGDataSerializer;
import api.wireless.gdata.docs.serializer.xml.XmlFolderEntryGDataSerializer;
import api.wireless.gdata.parser.GDataParser;
import api.wireless.gdata.parser.ParseException;
import api.wireless.gdata.parser.xml.XmlParserFactory;
import api.wireless.gdata.serializer.GDataSerializer;


public class XmlDocsGDataParserFactory implements GDataParserFactory{

	/** The XmlParserFactory to use to actually process XML streams. */
	private XmlParserFactory xmlFactory;

	/*
	 * @see GDataParserFactory
	 */
	public XmlDocsGDataParserFactory(XmlParserFactory xmlFactory) {
		this.xmlFactory = xmlFactory;
	}

	/** Intentionally private. */
	@SuppressWarnings("unused")
	private XmlDocsGDataParserFactory() {
		super();
	}

	@SuppressWarnings("unchecked")
	public GDataParser createParser(Class entryClass, InputStream is)
	throws ParseException {
		try {
			XmlPullParser xmlParser = xmlFactory.createParser();
			if (entryClass == DocumentEntry.class){
				return new XmlDocumentGDataParser(is, xmlParser);
			} else if (entryClass == DocumentListEntry.class) {
				return new XmlDocumentListGDataParser(is, xmlParser);
			} else if (entryClass == FolderEntry.class) {
				return new XmlFolderGDataParser(is, xmlParser);
			} else {
				throw new ParseException("Unrecognized entry class parser requested.");
			}
		} catch (XmlPullParserException e) {
			throw new ParseException("Failed to create parser", e);
		}
	}

	public GDataParser createParser(InputStream is) throws ParseException {
		return createParser(DocumentListEntry.class, is);
	}

	public GDataSerializer createSerializer(Entry entry) {
		if (entry instanceof DocumentEntry) {
			DocumentEntry docEntry = (DocumentEntry) entry;
			return new XmlDocumentEntryGDataSerializer(xmlFactory, docEntry);
		} else if (entry instanceof FolderEntry) {
			FolderEntry fldEntry = (FolderEntry) entry;
			return new XmlFolderEntryGDataSerializer(xmlFactory, fldEntry);
		} else {
			throw new IllegalArgumentException("Unrecognized entry serializer requested.");
		}
	}

}
