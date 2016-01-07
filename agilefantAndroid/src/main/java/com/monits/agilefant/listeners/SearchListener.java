package com.monits.agilefant.listeners;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.search.SearchAdapter;
import com.monits.agilefant.adapter.search.SearchCursor;
import com.monits.agilefant.model.SearchResult;
import com.monits.agilefant.service.SearchService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by edipasquale on 09/11/15.
 */
public class SearchListener implements SearchView.OnQueryTextListener {

	private static final long SEARCH_TIMEOUT = 300;

	private final SearchAdapter searchAdapter;
	private final Context context;
	private final SuggestionListener suggestionListener;
	private TimerOut timerOut;

	@Inject
	/* default */ SearchService searchService;

	/**
	 * Standard constructor
	 *
	 * @param context            The context
	 * @param searchAdapter      Searchview's adapter
	 * @param suggestionListener SearchView's suggestionListener
	 */
	public SearchListener(final Context context, final SearchAdapter searchAdapter,
						final SuggestionListener suggestionListener) {
		this.searchAdapter = searchAdapter;
		this.context = context;
		this.suggestionListener = suggestionListener;
		this.timerOut = new TimerOut();
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public boolean onQueryTextSubmit(final String input) {

		// If input isn't null we make the search
		if (!input.isEmpty()) {
			searchService.searchItems(input, new Response.Listener<List<SearchResult>>() {
				@Override
				public void onResponse(final List<SearchResult> results) {

					final SearchCursor cursor = new SearchCursor();

					cursor.addSearchResults(results);
					suggestionListener.setItems(results);

					searchAdapter.changeCursor(cursor);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(final VolleyError volleyError) {
					Toast.makeText(context, context.getResources().getString(R.string.feedback_search_error),
							Toast.LENGTH_LONG).show();
				}
			});
		}

		return true;
	}

	@Override
	public boolean onQueryTextChange(final String input) {

		// We cancel previous execution
		timerOut.cancel(true);

		// On text remove we drop the cursor
		if (input.isEmpty()) {
			searchAdapter.changeCursor(null);
		} else {
			/**
			 * Created a new instance of AsyncTask for avoiding multiple executions
			 */
			timerOut = new TimerOut();
			timerOut.execute(input);
		}

		return true;
	}

	private final class TimerOut extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(final String... params) {

			try {
				Thread.sleep(SEARCH_TIMEOUT);
				onQueryTextSubmit(params[0]);
			} catch (final InterruptedException e) {
				throw new AssertionError(e);
			}
			return null;
		}
	}
}
