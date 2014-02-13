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
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.dialog.PromptDialogFragment;
import com.monits.agilefant.dialog.PromptDialogFragment.PromptDialogListener;
import com.monits.agilefant.fragment.iteration.SpentEffortFragment;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment.OnUsersSubmittedListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.TaskTimeTrackingService;
import com.monits.agilefant.util.InputUtils;

public class TaskAdapterViewActionListener extends AbstractObservableAdapterViewActionListener<Task> {

	@Inject
	private MetricsService metricsService;

	@Inject
	private IterationService iterationService;

	private final Observer observer;

	public TaskAdapterViewActionListener(final FragmentActivity context, final Observer observer) {
		super(context);

		this.observer = observer;

		RoboGuice.injectMembers(context, this);
	}

	@Override
	public void onAction(final View view, final Task object) {

		switch (view.getId()) {
			case R.id.column_name:
				final Builder confirmStartTrackingBuilder = new Builder(context);
				confirmStartTrackingBuilder.setMessage(R.string.start_tracking_task_time);
				confirmStartTrackingBuilder.setPositiveButton(R.string.dialog_start_tracking_task_time_positive, new OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						final Intent intent = new Intent(context, TaskTimeTrackingService.class);
						intent.putExtra(TaskTimeTrackingService.EXTRA_TASK, object);
						context.startService(intent);

					}
				});
				confirmStartTrackingBuilder.setNegativeButton(R.string.dialog_start_tracking_task_time_negative, new OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
					}
				});
				final AlertDialog confirmStartTrackingDialog = confirmStartTrackingBuilder.create();
				confirmStartTrackingDialog.show();

				break;
			case R.id.column_effort_left:

				// Agilefant's tasks that are already done, can't have it's EL changed.
				if (!object.getState().equals(StateKey.DONE)) {

					final PromptDialogFragment dialogFragment = PromptDialogFragment.newInstance(
							R.string.dialog_effortleft_title,
							String.valueOf((float) object.getEffortLeft() / 60), // Made this way to avoid strings added in utils method.
							InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

					dialogFragment.setPromptDialogListener(new PromptDialogListener() {

						@Override
						public void onAccept(final String inputValue) {
							metricsService.changeEffortLeft(
									InputUtils.parseStringToDouble(inputValue),
									object,
									new Listener<Task>() {

										@Override
										public void onResponse(final Task arg0) {
											Toast.makeText(
													context, R.string.feedback_succesfully_updated_effort_left, Toast.LENGTH_SHORT).show();
										}
									},
									new ErrorListener() {

										@Override
										public void onErrorResponse(final VolleyError arg0) {
											Toast.makeText(
													context, R.string.feedback_failed_update_effort_left, Toast.LENGTH_SHORT).show();
										}
									});
						}
					});

					dialogFragment.show(context.getSupportFragmentManager(), "effortLeftDialog");
				}

				break;

			case R.id.column_spent_effort:

				final FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
				transaction.add(R.id.container, SpentEffortFragment.newInstance(object));
				transaction.addToBackStack(null);
				transaction.commit();

				break;

			case R.id.column_state:

				final OnClickListener onChoiceSelectedListener = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						metricsService.taskChangeState(
								StateKey.values()[which],
								object,
								new Listener<Task>() {

									@Override
									public void onResponse(final Task arg0) {
										Toast.makeText(
												context, R.string.feedback_successfully_updated_state, Toast.LENGTH_SHORT).show();
									};
								},
								new ErrorListener() {

									@Override
									public void onErrorResponse(final VolleyError arg0) {
										Toast.makeText(
												context, R.string.feedback_failed_update_state, Toast.LENGTH_SHORT).show();
									};
								});

						dialog.dismiss();
					}
				};

				final AlertDialog.Builder builder = new Builder(context);
				builder.setTitle(R.string.dialog_state_title);
				builder.setSingleChoiceItems(
						StateKey.getDisplayStates(), object.getState().ordinal(), onChoiceSelectedListener);
				builder.show();

				break;

			case R.id.column_responsibles:
				final Fragment fragment = UserChooserFragment.newInstance(
						object.getResponsibles(),
						new OnUsersSubmittedListener() {

							@Override
							public void onSubmitUsers(final List<User> users) {
								metricsService.changeTaskResponsibles(
										users,
										object,
										new Listener<Task>() {

											@Override
											public void onResponse(final Task project) {
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

	@Override
	protected Observer getObserver() {
		return observer;
	}
}