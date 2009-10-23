// Copyright 2007 The Android Open Source Project
package api.wireless.gdata.spreadsheets.data;

import api.wireless.gdata.data.Entry;
import api.wireless.gdata.util.common.base.StringUtil;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Represents an entry in a GData Spreadsheets List feed.
 */
public class ListEntry extends Entry {
    /** Map containing the values in the row. */
    private Hashtable values = new Hashtable();
    
    /** Caches the list of names, so they don't need to be recomputed. */
    private Vector names = null;

    /**
     * Retrieves the column names present in this row.
     * 
     * @return a Set of Strings, one per column where data exists
     */
    public Vector getNames() {
        if (names != null) {
            return names;
        }
        names = new Vector();
        Enumeration e = values.keys();
        while (e.hasMoreElements()) {
            names.add(e.nextElement());
        }
        return names;
    }

    /**
     * Fetches the value for a column. Equivalent to
     * <code>getValue(name, null)</code>.
     * 
     * @param name the name of the column whose row is to be fetched
     * @return the value of the column, or null if the column is not present
     */
    public String getValue(String name) {
        return getValue(name, null);
    }

    /**
     * Fetches the value for a column.
     * 
     * @param name the name of the column whose row is to be fetched
     * @param defaultValue the value to return if the row has no value for the
     *        requested column; may be null
     * @return the value of the column, or null if the column is not present
     */
    public String getValue(String name, String defaultValue) {
        if (StringUtil.isEmpty(name)) {
            return defaultValue;
        }
        String val = (String) values.get(name);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }

    /**
     * Sets the value of a column.
     * 
     * @param name the name of the column
     * @param value the value for the column
     */
    public void setValue(String name, String value) {
        values.put(name, value == null ? "" : value);
    }
}
