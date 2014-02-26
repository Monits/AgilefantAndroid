package com.monits.agilefant.fragment.backlog.story;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.fragment.backlog.AbstractCreateBacklogElementFragment;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.service.MetricsService;
public class CreateStoryFragment extends AbstractCreateBacklogElementFragment {

	@Inject
	private MetricsService metricsService;

	public static CreateStoryFragment newInstance(final long backlogId) {
		final CreateStoryFragment fragment = new CreateStoryFragment();
		return prepareFragment(backlogId, fragment);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void onSubmit(final BacklogElementParameters parameters) {
		final FragmentActivity context = getActivity();
		metricsService.createStory(
				getStory(parameters),
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

	private Story getStory(final BacklogElementParameters parameters) {

		final Story newStory = new Story();
		final Backlog newStoryBacklog = new Backlog();
		newStoryBacklog.setId(parameters.getBacklogId());
		newStory.setBacklog(newStoryBacklog);
		newStory.setName(parameters.getName());
		newStory.setResponsibles(parameters.getSelectedUser());
		newStory.setState(parameters.getStateKey());

		return newStory;
	}

	@Override
	protected int getTitleResourceId() {
		return R.string.new_story;
	}
}
