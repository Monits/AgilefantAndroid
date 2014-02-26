package com.monits.agilefant.listeners.implementations;

import java.util.List;
import java.util.Observer;

import roboguice.RoboGuice;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment.OnUsersSubmittedListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;

public class StoryAdapterViewActionListener extends AbstractObservableAdapterViewActionListener<Story> {

	@Inject
	private MetricsService metricsService;

	@Inject
	private IterationService iterationService;

	private final Observer observer;

	public StoryAdapterViewActionListener(final FragmentActivity context, final Observer observer) {
		super(context);

		this.observer = observer;

		RoboGuice.injectMembers(context, this);
	}

	@Override
	public void onAction(final View view, final Story object) {
		super.onAction(view, object);

		switch (view.getId()) {
			case R.id.column_state:
				final OnClickListener onStoryStateSelectedListener = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						final StateKey state = StateKey.values()[which];

						if (state == StateKey.DONE) {
							final AlertDialog.Builder builder = new Builder(context);
							builder.setTitle(R.string.dialog_tasks_to_done_title)
								.setMessage(R.string.dialog_tasks_to_done_msg)
								.setPositiveButton(android.R.string.yes, new OnClickListener() {

									@Override
									public void onClick(final DialogInterface dialog, final int which) {
										executeUpdateStoryTask(state, object, true);
									}
								})
								.setNegativeButton(android.R.string.no, new OnClickListener() {

									@Override
									public void onClick(final DialogInterface dialog, final int which) {
										executeUpdateStoryTask(state, object, false);
									}
								});

							builder.show();
						} else {
							executeUpdateStoryTask(state, object, false);
						}

						dialog.dismiss();
					}
				};

				final AlertDialog.Builder builder = new Builder(context);
				builder.setTitle(R.string.dialog_state_title);
				builder.setSingleChoiceItems(
						StateKey.getDisplayStates(), object.getState().ordinal(), onStoryStateSelectedListener);
				builder.show();

				break;

			case R.id.column_responsibles:

				final Fragment fragment = UserChooserFragment.newInstance(
						object.getResponsibles(),
						new OnUsersSubmittedListener() {

							@Override
							public void onSubmitUsers(final List<User> users) {

								metricsService.changeStoryResponsibles(
										users,
										object,
										new Listener<Story>() {

											@Override
											public void onResponse(final Story project) {
												Toast.makeText(context, R.string.feedback_success_updated_project, Toast.LENGTH_SHORT).show();
											}
										},
										new ErrorListener() {

											@Override
											public void onErrorResponse(final VolleyError arg0) {
												Toast.makeText(context, R.string.feedback_failed_update_project, Toast.LENGTH_SHORT).show();
											}
										});
							}
						});

				context.getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, fragment)
					.addToBackStack(null)
					.commit();

				break;
			case R.id.column_context:
				final Iteration iteration = object.getIteration();

				final ProgressDialog progressDialog = new ProgressDialog(context);
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(false);
				progressDialog.setMessage(context.getString(R.string.loading));
				progressDialog.show();

				iterationService.getIteration(
						iteration.getId(),
						new Listener<Iteration>() {

							@Override
							public void onResponse(final Iteration response) {
								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}

								final Intent intent = new Intent(context, IterationActivity.class);

								// Workaround that may be patchy, but it depends on the request whether it comes or not, and how to get it.
								response.setParent(iteration.getParent());

								intent.putExtra(IterationActivity.ITERATION, response);

								context.startActivity(intent);
							}
						},
						new ErrorListener() {

							@Override
							public void onErrorResponse(final VolleyError arg0) {
								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}

								Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
							}
						});

				break;
		}
	}

	/**
	 * Configures and executes the {@link UpdateStoryTask}.
	 *
	 * @param state
	 * @param story
	 * @param allTasksToDone
	 */
	void executeUpdateStoryTask(final StateKey state, final Story story, final boolean allTasksToDone) {

		metricsService.changeStoryState(
				state,
				story,
				allTasksToDone,
				new Listener<Story>() {

					@Override
					public void onResponse(final Story arg0) {
						Toast.makeText(
								context, R.string.feedback_successfully_updated_story, Toast.LENGTH_SHORT).show();
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(
								context, R.string.feedback_failed_update_story, Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	protected Observer getObserver() {
		return observer;
	}
}