package com.monits.agilefant.fragment.backlog.task;

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
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.service.MetricsService;

import javax.inject.Inject;

public class CreateTaskWithoutStory extends AbstractCreateBacklogElementFragment {

	@Inject
	/* default */ MetricsService metricsService;

	/**
	 * Return a new CreateTaskWithoutStory with the given iteration id
	 * @param iterationId The iteration id
	 * @return a new CreateTaskWithoutStory with the given iteration id
	 */
	public static CreateTaskWithoutStory newInstance(final long iterationId) {
		final CreateTaskWithoutStory fragment = new CreateTaskWithoutStory();
		prepareFragmentForIteration(iterationId, fragment);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	protected int getTitleResourceId() {
		return R.string.new_task;
	}

	@Override
	protected void onSubmit(final BacklogElementParameters parameters) {
		final FragmentActivity context = getActivity();
		metricsService.createTask(
				parameters,
				new Listener<Task>() {
					@Override
					public void onResponse(final Task newTask) {
						final Intent newTaskIntent = new Intent();
						newTaskIntent.setAction(AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY);
						newTaskIntent.putExtra(AgilefantApplication.EXTRA_NEW_TASK_WITHOUT_STORY, newTask);
						context.sendBroadcast(newTaskIntent);

						getFragmentManager().popBackStack();
						Toast.makeText(context, R.string.saved_task, Toast.LENGTH_SHORT).show();
					}
				},
				new ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(context, R.string.error_saving_task, Toast.LENGTH_SHORT).show();
					}
				});
	}

}
