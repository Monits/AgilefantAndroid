package com.monits.agilefant.listeners;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.PageSelect;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.SearchResult;
import com.monits.agilefant.model.StorySearchResult;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.ProjectService;
import com.monits.agilefant.service.SearchService;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by edipasquale on 10/11/15.
 */
public class SuggestionListener implements SearchView.OnSuggestionListener {

	private final Context context;
	private List<SearchResult> searchResultList;
	private ProgressDialog progressDialog;

	@Inject
	/* default */ ProjectService projectService;
	@Inject
	/* default */ IterationService iterationService;

	@Inject
	/* default */ SearchService searchService;

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

		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(context.getString(R.string.loading));
		progressDialog.show();

		switch (item.getType()) {
		case PROJECT:
			callProject(item);
			break;

		case ITERATION:
			callIteration(item);
			break;

		case STORY:
			callStory(item);
			break;

		default:
			break;

		}
		// TODO : Should be contemplated actions if clicked item was a Product and Task.

		return true;
	}

	private void callStory(final SearchResult item) {

		searchService.searchStory(item.getId(), new Response.Listener<StorySearchResult>() {
			@Override
			public void onResponse(final StorySearchResult storySearchResult) {
				if (storySearchResult == null) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}

					Toast.makeText(context, R.string.feedback_failed_retrieve_story,
							Toast.LENGTH_SHORT).show();
				} else {
					getIteration(storySearchResult.getIterationId(), storySearchResult.getStoryId(),
							PageSelect.STORIES);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(final VolleyError volleyError) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				Toast.makeText(context, R.string.feedback_failed_retrieve_story,
						Toast.LENGTH_SHORT).show();

			}
		});

	}

	private void callProject(final SearchResult item) {
		// Request project and go to ProjectActivity
		projectService.getProjectData(item.getId(),
				new Response.Listener<Project>() {
					@Override
					public void onResponse(final Project project) {
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						context.startActivity(ProjectActivity
								.getIntent(context, project.getParent(), project));
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError volleyError) {
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						Toast.makeText(context, context.getResources()
								.getString(R.string.feedback_failed_retrieve_project), Toast.LENGTH_LONG)
								.show();
					}
				});
	}

	private void callIteration(final SearchResult item) {
		// Request Iteration and go to IterationActivity
		getIteration(item.getId());
	}

	private void getIteration(final int iterationId) {
		iterationService.getIteration(
				iterationId,
				new Response.Listener<Iteration>() {
					@Override
					public void onResponse(final Iteration response) {
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						context.startActivity(IterationActivity.getIntent(context, response));
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError arg0) {
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						Toast.makeText(context, R.string.feedback_failed_retrieve_iteration,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void getIteration(final int iterationId, final Integer storyId, final PageSelect focusPage) {
		// Request Story and go to IterationActivity, and focus in Story list
		iterationService.getIteration(
				iterationId,
				new Response.Listener<Iteration>() {
					@Override
					public void onResponse(final Iteration response) {
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						context.startActivity(IterationActivity.getIntent(context, response, focusPage, storyId));

					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError arg0) {
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						Toast.makeText(context, R.string.feedback_failed_retrieve_iteration,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	public void setItems(@NonNull final List<SearchResult> searchResultList) {
		this.searchResultList = searchResultList;
	}
}
