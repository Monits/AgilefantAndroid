package com.monits.agilefant.service;

import com.android.volley.Response;
import com.monits.agilefant.model.SearchResult;

import java.util.List;

/**
 * Created by edipasquale on 11/11/15.
 */
public interface SearchService {

	/**
	 * Searches items matching the received input. Those items
	 * could be Stories, Projects or Tasks and are handled by
	 * a SearchResult object
	 *
	 * @param term The text input
	 * @param listener Callback if the request was successful
	 * @param error Callback if the request failed
	 */
	void searchItems(String term, Response.Listener<List<SearchResult>> listener, Response.ErrorListener error);
}
