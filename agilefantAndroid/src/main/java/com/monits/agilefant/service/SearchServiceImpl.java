package com.monits.agilefant.service;

import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monits.agilefant.model.SearchResult;
import com.monits.agilefant.model.StorySearchResult;
import com.monits.agilefant.model.search.SearchResultType;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
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
	//TODO: In future feature add type in url, for add task search
	private static final String SEARCH_STORY_URL
			= "%1$s/searchResult.action?targetClassName=fi.hut.soberit.agilefant.model.Story&targetObjectId=%2$s";

	private static final String LOCATION_HEADER = "Location";
	private static final String ITERATION_ID_REQUEST = "iterationId";

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

						// We only show projects, Iteration and Story
						for (final SearchResult item : searchResultList) {
							if (item.getType() == SearchResultType.PROJECT
									|| item.getType() == SearchResultType.ITERATION
									|| item.getType() == SearchResultType.STORY) {
								filteredList.add(item);
							}
						}
						listener.onResponse(filteredList);
					}
				}, error, null);

		agilefantService.addRequest(request);
	}

	@Override
	public void searchStory(final int storyId, final Response.Listener<StorySearchResult> listener,
							final Response.ErrorListener error) {
		final String url = String.format(Locale.US, SEARCH_STORY_URL, agilefantService.getHost(), storyId);

		final ErrorListener reqError = new ErrorListener() {
			@Override
			public void onErrorResponse(final VolleyError volleyError) {
				final NetworkResponse response = volleyError.networkResponse;
				if (response != null && response.statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {

					final String location = response.headers.get(LOCATION_HEADER);
					//Validate the location contain iteration id and contain needed text
					if (location.contains(ITERATION_ID_REQUEST)) {
						//Simple parse iteration ID for Story
						final Uri uri = Uri.parse(location);
						final int iterationId = Integer.parseInt(uri.getQueryParameter(ITERATION_ID_REQUEST));
						listener.onResponse(new StorySearchResult(storyId, iterationId));
					} else {
						error.onErrorResponse(volleyError);
					}
				} else {
					error.onErrorResponse(volleyError);
				}

			}

		};

		final GsonRequest<StorySearchResult> request = new GsonRequest<>(Request.Method.GET, url, gson,
				StorySearchResult.class, listener, reqError, null);

		agilefantService.addRequest(request);
	}

}
