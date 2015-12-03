package com.monits.agilefant.service;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monits.agilefant.model.SearchResult;
import com.monits.agilefant.model.search.SearchResultType;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Created by edipasquale on 11/11/15.
 */
public class SearchServiceImpl implements SearchService {

	private static final String SEARCH_BACKLOGS_URL = "%1$s/ajax/search.action?term=%2$s";

	private final AgilefantService agilefantService;
	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	/**
	 * Standar constructor
	 *
	 * @param agilefantService AgilefantService injected by Dagger
	 * @param gson Gson injected by Dagger
	 */
	@Inject
	public SearchServiceImpl(final AgilefantService agilefantService, final Gson gson) {
		this.agilefantService = agilefantService;
		this.gson = gson;
	}

	@Override
	public void searchItems(final String term, final Response.Listener<List<SearchResult>> listener,
							final Response.ErrorListener error) {
		final String url = String.format(Locale.US, SEARCH_BACKLOGS_URL, agilefantService.getHost(), term);

		final Type listType = new TypeToken<ArrayList<SearchResult>>() { }.getType();
		final GsonRequest<List<SearchResult>> request = new GsonRequest<>(Request.Method.GET, url,
				gson, listType,
				new Response.Listener<List<SearchResult>>() {
					@Override
					public void onResponse(final List<SearchResult> searchResultList) {
						// TODO : It has to be possible to show and execute actions if result is not a Project.

						final List<SearchResult> filteredList = new ArrayList<>();

						// We only show projects
						for (final SearchResult item : searchResultList) {
							if (item.getType() == SearchResultType.PROJECT) {
								filteredList.add(item);
							}
						}

						listener.onResponse(filteredList);
					}
				}, error, null);

		agilefantService.addRequest(request);
	}


}
