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

/**
 * The extendedProperty gdata type
 */
public class ExtendedProperty {
  private String name;
  private String value;
  private String xmlBlob;

  public String getXmlBlob() {
    return xmlBlob;
  }

  public void setXmlBlob(String xmlBlob) {
    this.xmlBlob = xmlBlob;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void toString(StringBuffer sb) {
    sb.append("ExtendedProperty");
    if (name != null) sb.append(" name:").append(name);
    if (value != null) sb.append(" value:").append(value);
    if (xmlBlob != null) sb.append(" xmlBlob:").append(xmlBlob);
  }

  public void validate() throws ParseException {
    if (name == null) {
      throw new ParseException("name must not be null");
    }

    if ((value == null && xmlBlob == null) || (value != null && xmlBlob != null)) {
      throw new ParseException("exactly one of value and xmlBlob must be present");
    }
  }
}
