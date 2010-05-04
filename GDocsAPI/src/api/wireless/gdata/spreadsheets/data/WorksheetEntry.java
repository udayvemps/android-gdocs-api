// Copyright 2007 The Android Open Source Project
package api.wireless.gdata.spreadsheets.data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidParameterException;

import android.text.TextUtils;
import api.wireless.gdata.GDataException;
import api.wireless.gdata.data.Entry;
import api.wireless.gdata.util.common.base.StringUtil;

/**
 * Represents an entry in a GData Worksheets meta-feed.
 */
public class WorksheetEntry extends Entry {
	/** The URI to this entry's cells feed. */
	private String cellsUri = null;

	/** The number of columns in the worksheet. */
	private int colCount = -1;

	/** The URI to this entry's list feed. */
	private String listUri = null;

	/** The number of rows in the worksheet. */
	private int rowCount = -1;

	/**
	 * Fetches the URI of this entry's Cells feed.
	 * 
	 * @return The URI of the entry's Cells feed.
	 */
	public String getCellFeedUri() {
		return cellsUri;
	}

	/**
	 * Fetches the number of columns in the worksheet.
	 * 
	 * @return The number of columns.
	 */
	public int getColCount() {
		return colCount;
	}

	/**
	 * Fetches the URI of this entry's List feed.
	 * 
	 * @return The URI of the entry's List feed.
	 * @throws GDataException
	 *             If the URI is not set or is invalid.
	 */
	public String getListFeedUri() {
		if (listUri == null) {
			listUri = cellsUri.replace("cells", "list");
		}
		return listUri;
	}

	/**
	 * Fetches the number of rows in the worksheet.
	 * 
	 * @return The number of rows.
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Sets the URI of this entry's Cells feed.
	 * 
	 * @param href
	 *            The HREF attribute that should be the Cells feed URI.
	 */
	public void setCellFeedUri(String href) {
		cellsUri = href;
	}

	/**
	 * Sets the number of columns in the worksheet.
	 * 
	 * @param colCount
	 *            The new number of columns.
	 */
	public void setColCount(int colCount) {
		this.colCount = colCount;
	}

	/**
	 * Sets this entry's Atom ID.
	 * 
	 * @param id
	 *            The new ID value.
	 */
	public void setId(String id) {
		super.setId(id);
	}

	/**
	 * Sets the URI of this entry's List feed.
	 * 
	 * @param href
	 *            The HREF attribute that should be the List feed URI.
	 */
	public void setListFeedUri(String href) {
		listUri = href;
	}

	/**
	 * Sets the number of rows in the worksheet.
	 * 
	 * @param rowCount
	 *            The new number of rows.
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getCellRangeQueryFeedUri(int minRow, int minCol, int maxRow,
			int maxCol) {
		if (minRow < 1 || minCol < 1) {
			throw new IllegalArgumentException(
					"Can't have minrow or mincol less than one");
		}

		String feed = getCellFeedUri();

		StringBuffer sb = new StringBuffer();
		sb.append(feed);
		sb.append("?");
		sb.append("min-row=");
		sb.append(minRow);
		sb.append("&");
		sb.append("min-col=");
		sb.append(minCol);
		sb.append("&");
		sb.append("max-row=");
		sb.append(maxRow);
		sb.append("&");
		sb.append("max-col=");
		sb.append(maxCol);

		return sb.toString();
	}

	public String getListFeedUriWithQuery(String query) {

		String listUri = getListFeedUri();
		StringBuffer sb = new StringBuffer();
		sb.append(listUri);
		sb.append("?sq=");

		try {
			sb.append(URLEncoder.encode(query, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// Don't care right now
		}

		return sb.toString();
	}
}
