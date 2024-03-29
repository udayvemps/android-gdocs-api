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


package api.wireless.gdata.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple class for parsing and generating Content-Type header values, per
 * RFC 2045 (MIME) and 2616 (HTTP 1.1).
 *
 * 
 */
public class ContentType implements Serializable {

  private static String TOKEN =
    "[\\p{ASCII}&&[^\\p{Cntrl} ;/=\\[\\]\\(\\)\\<\\>\\@\\,\\:\\\"\\?\\=]]+";

  // Precisely matches a token
  private static Pattern TOKEN_PATTERN = Pattern.compile(
    "^" + TOKEN + "$");

  // Matches a media type value
  private static Pattern TYPE_PATTERN = Pattern.compile(
    "(" + TOKEN + ")" +         // type  (G1)
    "/" +                       // separator
    "(" + TOKEN + ")" +         // subtype (G2)
    "\\s*(.*)\\s*", Pattern.DOTALL);

  // Matches an attribute value
  private static Pattern ATTR_PATTERN = Pattern.compile(
    "\\s*;\\s*" +
      "(" + TOKEN + ")" +       // attr name  (G1)
      "\\s*=\\s*" +
      "(?:" +
        "\"([^\"]*)\"" +        // value as quoted string (G3)
        "|" +
        "(" + TOKEN + ")?" +    // value as token (G2)
      ")"
    );

  /**
   * Name of the attribute that contains the encoding character set for
   * the content type.
   * @see #getCharset()
   */
  public static final String ATTR_CHARSET = "charset";

  /**
   * Special "*" character to match any type or subtype.
   */
  private static final String STAR = "*";

  /**
   * The UTF-8 charset encoding is used by default for all text and xml
   * based MIME types.
   */
  private static final String DEFAULT_CHARSET = ATTR_CHARSET + "=UTF-8";

  /**
   * A ContentType constant that describes the base unqualified Atom content
   * type.
   */
  public static final ContentType ATOM =
    new ContentType("application/atom+xml;" + DEFAULT_CHARSET);

  /**
   * A ContentType constant that describes the qualified Atom entry content
   * type.
   *
   * @see #getAtomEntry()
   */
  public static final ContentType ATOM_ENTRY =
    new ContentType("application/atom+xml;type=entry;" + DEFAULT_CHARSET) {
      @Override
      public boolean match(ContentType acceptedContentType) {
        String type = acceptedContentType.getAttribute("type");
        return super.match(acceptedContentType) &&
            (type == null || type.equals("entry"));
      }
    };

  /**
   * A ContentType constant that describes the qualified Atom feed content
   * type.
   *
   * @see #getAtomFeed()
   */
  public static final ContentType ATOM_FEED =
    new ContentType("application/atom+xml;type=feed;" + DEFAULT_CHARSET) {
      @Override
      public boolean match(ContentType acceptedContentType) {
        String type = acceptedContentType.getAttribute("type");
        return super.match(acceptedContentType) &&
            (type == null || type.equals("feed"));
      }
    };

  /**
   * Returns the ContentType that should be used in contexts that expect
   * an Atom entry.
   */
  public static ContentType getAtomEntry() {
    return ATOM_ENTRY;
  }

  /**
   * Returns the ContentType that should be used in contexts that expect
   * an Atom feed.
   */
  public static ContentType getAtomFeed() {
    // Use the unqualified type for v1, the qualified one for later versions
    return ATOM_FEED;
  }

  /**
   * A ContentType constant that describes the Atom Service content type.
   */
  public static final ContentType ATOM_SERVICE =
    new ContentType("application/atomsvc+xml;" + DEFAULT_CHARSET);

  /**
   * A ContentType constant that describes the RSS channel/item content type.
   */
  public static final ContentType RSS =
    new ContentType("application/rss+xml;" + DEFAULT_CHARSET);

  /**
   * A ContentType constant that describes the JSON content type.
   */
  public static final ContentType JSON =
    new ContentType("application/json;" + DEFAULT_CHARSET);

  /**
   * A ContentType constant that describes the Javascript content type.
   */
  public static final ContentType JAVASCRIPT =
    new ContentType("text/javascript;" + DEFAULT_CHARSET);

  /**
   * A ContentType constant that describes the generic text/xml content type.
   */
  public static final ContentType TEXT_XML =
    new ContentType("text/xml;" + DEFAULT_CHARSET);

  /**
   * A ContentType constant that describes the generic text/html content type.
   */
  public static final ContentType TEXT_HTML =
    new ContentType("text/html;" + DEFAULT_CHARSET);

  /**
   * A ContentType constant that describes the generic text/plain content type.
   */
  public static final ContentType TEXT_PLAIN =
    new ContentType("text/plain;" + DEFAULT_CHARSET);
  
  /**
   * A ContentType constant that describes the generic text/plain content type.
   */
  public static final ContentType TEXT =
    new ContentType("text/plain");

  /**
   * A ContentType constant that describes the GData error content type.
   */
  public static final ContentType GDATA_ERROR =
    new ContentType("application/vnd.google.gdata.error+xml");

  /**
   * A ContentType constant that describes the MIME multipart/related content
   * type.
   */
  public static final ContentType MULTIPART_RELATED =
    new ContentType("multipart/related");
  
  /**
  * A ContentType constant that describes the PDF MIME 
  */
  public static final ContentType PDF =
	new ContentType("application/pdf");

  /**
   * A ContentType constant that describes the CSV MIME 
   */
  public static final ContentType CSV =
		new ContentType("text/csv");

  /**
   * A ContentType constant that describes the binary file MIME 
   */
  public static final ContentType BIN =
		new ContentType("application/octet-stream");
  
  /**
   * A ContentType constant that describes the binary file MIME 
   */
  public static final ContentType PNG =
		new ContentType("image/png");
  
  public static final ContentType JPEG =
		new ContentType("image/jpeg");
  
  public static final ContentType RTF =
		new ContentType("application/rtf");

  public static final ContentType DOC =
		new ContentType("application/msword");
  
  public static final ContentType XLS =
		new ContentType("application/vnd.ms-excel");

  public static final ContentType ODT =
		new ContentType("application/vnd.oasis.opendocument.text");

  public static final ContentType ODS =
		new ContentType("application/vnd.oasis.opendocument.spreadsheet");
  
  public static final ContentType ZIP =
		new ContentType("application/zip");

  public static final ContentType TSV =
		new ContentType("text/tab-separated-values");

  public static final ContentType PPT =
		new ContentType("application/vnd.ms-powerpoint");

  /**
   * Determines the best "Content-Type" header to use in a servlet response
   * based on the "Accept" header from a servlet request.
   *
   * @param acceptHeader       "Accept" header value from a servlet request (not
   *                           <code>null</code>)
   * @param actualContentTypes actual content types in descending order of
   *                           preference (non-empty, and each entry is of the
   *                           form "type/subtype" without the wildcard char
   *                           '*') or <code>null</code> if no "Accept" header
   *                           was specified
   * @return the best content type to use (or <code>null</code> on no match).
   */
  public static ContentType getBestContentType(String acceptHeader,
      List<ContentType> actualContentTypes) {

    // If not accept header is specified, return the first actual type
    if (acceptHeader == null) {
      return actualContentTypes.get(0);
    }

    // iterate over all of the accepted content types to find the best match
    float bestQ = 0;
    ContentType bestContentType = null;
    String[] acceptedTypes = acceptHeader.split(",");
    for (String acceptedTypeString : acceptedTypes) {

      // create the content type object
      ContentType acceptedContentType;
      try {
        acceptedContentType = new ContentType(acceptedTypeString.trim());
      } catch (IllegalArgumentException ex) {
        // ignore exception
        continue;
      }

      // parse the "q" value (default of 1)
      float curQ = 1;
      try {
        String qAttr = acceptedContentType.getAttribute("q");
        if (qAttr != null) {
          float qValue = Float.valueOf(qAttr);
          if (qValue <= 0 || qValue > 1) {
            continue;
          }
          curQ = qValue;
        }
      } catch (NumberFormatException ex) {
        // ignore exception
        continue;
      }

      // only check it if it's at least as good ("q") as the best one so far
      if (curQ < bestQ) {
        continue;
      }

      /* iterate over the actual content types in order to find the best match
      to the current accepted content type */
      for (ContentType actualContentType : actualContentTypes) {

        /* if the "q" value is the same as the current best, only check for
        better content types */
        if (curQ == bestQ && bestContentType == actualContentType) {
          break;
        }

        /* check if the accepted content type matches the current actual
        content type */
        if (actualContentType.match(acceptedContentType)) {
          bestContentType = actualContentType;
          bestQ = curQ;
          break;
        }
      }
    }

    // if found an acceptable content type, return the best one
    if (bestQ != 0) {
      return bestContentType;
    }

    // Return null if no match
    return null;
  }

 /**
   * Constructs a new instance with default media type
   */
  public ContentType() {
    this(null);
  }

  /**
   * Constructs a new instance from a content-type header value
   * parsing the MIME content type (RFC2045) format.  If the type
   * is {@code null}, then media type and charset will be
   * initialized to default values.
   *
   * @param typeHeader content type value in RFC2045 header format.
   */
  public ContentType(String typeHeader) {

    // If the type header is no provided, then use the HTTP defaults.
    if (typeHeader == null) {
      type = "application";
      subType = "octet-stream";
      attributes.put(ATTR_CHARSET, "iso-8859-1"); // http default
      return;
    }

    // Get type and subtype
    Matcher typeMatch = TYPE_PATTERN.matcher(typeHeader);
    if (!typeMatch.matches()) {
      throw new IllegalArgumentException("Invalid media type:" + typeHeader);
    }

    type = typeMatch.group(1).toLowerCase();
    subType = typeMatch.group(2).toLowerCase();
    if (typeMatch.groupCount() < 3) {
      return;
    }

    // Get attributes (if any)
    Matcher attrMatch = ATTR_PATTERN.matcher(typeMatch.group(3));
    while (attrMatch.find()) {

      String value = attrMatch.group(2);
      if (value == null) {
        value = attrMatch.group(3);
        if (value == null) {
          value = "";
        }
      }

      attributes.put(attrMatch.group(1).toLowerCase(), value);
    }

    // Infer a default charset encoding if unspecified.
    if (!attributes.containsKey(ATTR_CHARSET)) {
      inferredCharset = true;
      if (subType.endsWith("xml")) {
        if (type.equals("application")) {
          // BUGBUG: Actually have need to look at the raw stream here, but
          // if client omitted the charset for "application/xml", they are
          // ignoring the STRONGLY RECOMMEND language in RFC 3023, sec 3.2.
          // I have little sympathy.
          attributes.put(ATTR_CHARSET, "utf-8");    // best guess
        } else {
          attributes.put(ATTR_CHARSET, "us-ascii"); // RFC3023, sec 3.1
        }
      } else if (subType.equals("json")) {
        attributes.put(ATTR_CHARSET, "utf-8");    // RFC4627, sec 3
      } else {
        attributes.put(ATTR_CHARSET, "iso-8859-1"); // http default
      }
    }
  }

  /** {code True} if parsed input didn't contain charset encoding info */
  private boolean inferredCharset = false;

  private String type;
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }


  private String subType;
  public String getSubType() { return subType; }
  public void setSubType(String subType) { this.subType = subType; }

  /** Returns the full media type */
  public String getMediaType() {
    StringBuilder sb = new StringBuilder();
    sb.append(type);
    sb.append("/");
    sb.append(subType);
    if (attributes.containsKey("type")) {
      sb.append(";type=").append(attributes.get("type"));
    }
    return sb.toString();
  }

  private HashMap<String, String> attributes = new HashMap<String, String>();

  /**
   * Returns the additional attributes of the content type.
   */
  public HashMap<String, String> getAttributes() { return attributes; }


  /**
   * Returns the additional attribute by name of the content type.
   *
   * @param name attribute name
   */
  public String getAttribute(String name) {
    return attributes.get(name);
  }

  /*
   * Returns the charset attribute of the content type or null if the
   * attribute has not been set.
   */
  public String getCharset() { return attributes.get(ATTR_CHARSET); }


  /**
   * Returns whether this content type is match by the content type found in the
   * "Accept" header field of an HTTP request.
   *
   * @param acceptedContentType content type found in the "Accept" header field
   *                            of an HTTP request
   */
  public boolean match(ContentType acceptedContentType) {
    String acceptedType = acceptedContentType.getType();
    String acceptedSubType = acceptedContentType.getSubType();
    return STAR.equals(acceptedType) || type.equals(acceptedType) &&
        (STAR.equals(acceptedSubType) || subType.equals(acceptedSubType));
  }

  
  public static String getFileExtension(ContentType ct){		  
	  if (ct.equals(TEXT_PLAIN))
		  return "txt";
	  else if (ct.equals(TEXT_HTML))
		  return "html";
	  else if (ct.equals(TEXT_XML))
		  return "xml";
	  else if (ct.equals(PDF))
		  return "pdf";
	  else if (ct.equals(CSV))
		  return "csv";	 
	  else if (ct.equals(PNG))
		  return "png";	
	  else if (ct.equals(RTF))
		  return "rtf";
	  else if (ct.equals(DOC))
		  return "doc";	 
	  else if (ct.equals(ODT))
		  return "odt";
	  else if (ct.equals(ODS))
		  return "ods";
	  else if (ct.equals(ZIP))
		  return "zip";
	  else if (ct.equals(TSV))
		  return "tsv";
	  else if (ct.equals(XLS))
		  return "xls";
	  else if (ct.equals(PPT))
		  return "ppt";
	  else 
		  return "";
  }
  
  public static ContentType getContentTypeFromExtension(String ext){
	  if (ext.equalsIgnoreCase("txt")) 
		  return TEXT_PLAIN;
	  else if (ext.equalsIgnoreCase("html")) 
		  return TEXT_HTML;
	  else if (ext.equalsIgnoreCase("xml"))
		  return TEXT_XML;
	  else if (ext.equalsIgnoreCase("pdf")) 
		  return PDF;
	  else if (ext.equalsIgnoreCase("csv")) 
		  return CSV;
	  else if (ext.equalsIgnoreCase("png")) 
		  return PNG;
	  else if (ext.equalsIgnoreCase("rtf")) 
		  return RTF;
	  else if (ext.equalsIgnoreCase("doc")) 
		  return DOC;
	  else if (ext.equalsIgnoreCase("odt")) 
		  return ODT;
	  else if (ext.equalsIgnoreCase("ods")) 
		  return ODS;
	  else if (ext.equalsIgnoreCase("zip")) 
		  return ZIP;
	  else if (ext.equalsIgnoreCase("tsv"))
		  return TSV;
	  else if (ext.equalsIgnoreCase("xls"))
		  return XLS;
	  else if (ext.equalsIgnoreCase("ppt"))
		  return PPT;
	  else 
		  return BIN;
  }

  /**
   * Generates the Content-Type value
   */
  @Override
  public String toString() {

    StringBuffer sb = new StringBuffer();
    sb.append(type);
    sb.append("/");
    sb.append(subType);
    for (String name : attributes.keySet()) {

      // Don't include any inferred charset attribute in output.
      if (inferredCharset && ATTR_CHARSET.equals(name)) {
        continue;
      }
      sb.append(";");
      sb.append(name);
      sb.append("=");
      String value = attributes.get(name);
      Matcher tokenMatcher = TOKEN_PATTERN.matcher(value);
      if (tokenMatcher.matches()) {
        sb.append(value);
      } else {
        sb.append("\"" + value + "\"");
      }
    }
    return sb.toString();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentType that = (ContentType) o;
    return type.equals(that.type) && subType.equals(that.subType) && attributes
        .equals(that.attributes);
  }


  @Override
  public int hashCode() {
    return (type.hashCode() * 31 + subType.hashCode()) * 31 + attributes
        .hashCode();
  }    
}
