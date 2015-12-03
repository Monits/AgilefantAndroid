package com.monits.agilefant.listeners;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.SearchResult;
import com.monits.agilefant.model.search.SearchResultType;
import com.monits.agilefant.service.ProjectService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by edipasquale on 10/11/15.
 */
public class SuggestionListener implements SearchView.OnSuggestionListener {

	private final Context context;
	private List<SearchResult> searchResultList;

	@Inject
	/* default */ ProjectService projectService;

	/**
	 * Standard Constructor
	 * @param context the context
	 */
	public SuggestionListener(final Context context) {
		this.context = context;
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public boolean onSuggestionSelect(final int position) {
		return false;
	}

	@Override
	public boolean onSuggestionClick(final int position) {
		final SearchResult item = searchResultList.get(position);

		if (item.getType() == SearchResultType.PROJECT) {

			// Request project and go to ProjectActivity
			projectService.getProjectData(item.getId(),
					new Response.Listener<Project>() {
						@Override
						public void onResponse(final Project project) {
							context.startActivity(ProjectActivity
									.newIntentInstance(context, project.getParent(), project));
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(final VolleyError volleyError) {
							Toast.makeText(context, context.getResources()
									.getString(R.string.feedback_failed_retrieve_project), Toast.LENGTH_LONG)
									.show();
						}
					});

		}

		// TODO : Should be contemplated actions if clicked item was a Product, Iteration, Story or a Task.

		return true;
	}

	public void setItems(@NonNull final List<SearchResult> searchResultList) {
		this.searchResultList = searchResultList;
	}
}
