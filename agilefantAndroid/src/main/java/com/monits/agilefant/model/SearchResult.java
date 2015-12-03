package com.monits.agilefant.model;

import com.monits.agilefant.model.search.SearchResultType;

/**
 * Created by edipasquale on 10/11/15.
 */
public class SearchResult {

	private final String label;
	private final SearchResultType type;
	private final int id;

	/**
	 * Default Constructor
	 *
	 * @param label Result object label
	 * @param type Result object type
	 * @param id Result object id
	 */
	public SearchResult(final String label, final SearchResultType type, final int id) {
		this.label = label;
		this.type = type;
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public SearchResultType getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "SearchResult{"
				+ "label=" + label
				+ ", type=" + type
				+ ", id=" + id
				+ '}';
	}
}
