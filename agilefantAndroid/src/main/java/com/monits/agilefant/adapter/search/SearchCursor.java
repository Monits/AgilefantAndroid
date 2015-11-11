package com.monits.agilefant.adapter.search;

import android.database.MatrixCursor;

import com.monits.agilefant.model.SearchResult;
import com.monits.agilefant.model.search.SearchResultType;

import java.util.List;

/**
 * Created by edipasquale on 10/11/15.
 */
public class SearchCursor extends MatrixCursor {

	private static final int ID = 0;
	private static final int LABEL = 1;
	private static final int TYPE = 2;
	private static final String[] FIELDS = {"_id", "label", "type"};


	/**
	 * Standard Constructor
	 */
	public SearchCursor() {
		this(FIELDS);
	}

	/**
	 * Hidding Default constructor
	 * @param columnNames fields the cursor will store
	 */
	private SearchCursor(final String[] columnNames) {
		super(columnNames);
	}

	public String getLabel() {
		return getString(LABEL);
	}

	public String getType() {
		return getString(TYPE);
	}

	/**
	 * Adds results to the cursor
	 * @param searchResultList the list of SearchResult objects to add
	 */
	public void addSearchResults(final List<SearchResult> searchResultList) {

		// TODO : It has to be possible to show and execute actions if result is not a Project.

		for (final SearchResult searchResult : searchResultList) {
			// It's only adding projects
			if (searchResult.getType() == SearchResultType.PROJECT) {
				final String[] fields = new String[FIELDS.length];
				fields[ID] = String.valueOf(searchResult.getId());
				fields[LABEL] = searchResult.getLabel();
				fields[TYPE] = searchResult.getType().toString();
				addRow(fields);
			}
		}
	}
}
