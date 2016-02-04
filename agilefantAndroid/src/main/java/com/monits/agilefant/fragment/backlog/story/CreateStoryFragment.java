package com.monits.agilefant.fragment.backlog.story;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.fragment.backlog.AbstractCreateBacklogElementFragment;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.model.backlog.BacklogType;
import com.monits.agilefant.service.WorkItemService;

import javax.inject.Inject;

public class CreateStoryFragment extends AbstractCreateBacklogElementFragment {

	@Inject
	/* default */ WorkItemService workItemService;

	/**
	 * Return a new CreateStoryFragment with the given iteration id.
	 *
	 * @param backlogType The backlog type
	 * @param backlogId The backlog id.
	 * @return a new CreateStoryFragment with the given iteration id.
	 */
	public static CreateStoryFragment newInstance(final BacklogType backlogType, final long backlogId) {
		final CreateStoryFragment fragment = new CreateStoryFragment();

		if (backlogType == BacklogType.ITERATION) {
			prepareFragmentForIteration(backlogId, fragment);
		} else {
			prepareFragmentForBacklog(backlogId, fragment);
		}
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							final Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void onSubmit(final BacklogElementParameters parameters) {
		if (TextUtils.isEmpty(parameters.getName())) {
			Toast.makeText(getActivity(), R.string.validation_empty_name, Toast.LENGTH_LONG).show();
		} else {
			final FragmentActivity context = getActivity();
			workItemService.createStory(
					parameters,
					new Listener<Story>() {
						@Override
						public void onResponse(final Story newStory) {
							context.sendBroadcast(AgilefantApplication.createNewStoryBroadcastIntent(newStory));

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
	}

	@Override
	protected int getTitleResourceId() {
		return R.string.new_story;
	}
}
