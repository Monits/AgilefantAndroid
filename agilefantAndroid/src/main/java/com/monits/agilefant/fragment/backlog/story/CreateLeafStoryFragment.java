package com.monits.agilefant.fragment.backlog.story;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.fragment.backlog.AbstractCreateBacklogElementFragment;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.service.MetricsService;

import javax.inject.Inject;

public class CreateLeafStoryFragment extends AbstractCreateBacklogElementFragment {

	@Inject
	MetricsService metricsService;

	/**
	 * Return a new CreateLeafStoryFragment with the given backlog id
	 * @param backlogId The backlog id
	 * @return a new CreateLeafStoryFragment with the given backlog id
	 */
	public static CreateLeafStoryFragment newInstance(final Long backlogId) {
		final CreateLeafStoryFragment fragment = new CreateLeafStoryFragment();
		return prepareFragmentForBacklog(backlogId, fragment);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	protected void onSubmit(final BacklogElementParameters parameters) {
		final FragmentActivity context = getActivity();
		metricsService.createStory(
			parameters,
			new Listener<Story>() {
				@Override
				public void onResponse(final Story newStory) {

					final Intent newStoryIntent = new Intent();
					newStoryIntent.setAction(AgilefantApplication.ACTION_NEW_STORY);
					newStoryIntent.putExtra(AgilefantApplication.EXTRA_NEW_STORY, newStory);
					context.sendBroadcast(newStoryIntent);

					getFragmentManager().popBackStack();
					Toast.makeText(context, R.string.saved_story, Toast.LENGTH_SHORT).show();
				}
			},
			new ErrorListener() {
				@Override
				public void onErrorResponse(final VolleyError arg0) {
					Toast.makeText(context, R.string.error_saving_story, Toast.LENGTH_SHORT).show();
				}
			});
	}

	@Override
	protected int getTitleResourceId() {
		return R.string.new_story;
	}
}
