package com.monits.agilefant.listeners.implementations;

import java.util.List;
import java.util.Observer;

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
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.dialog.PromptDialogFragment;
import com.monits.agilefant.dialog.PromptDialogFragment.PromptDialogListener;
import com.monits.agilefant.fragment.iteration.SpentEffortFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment.OnUsersSubmittedListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.TaskTimeTrackingService;
import com.monits.agilefant.util.InputUtils;

import javax.inject.Inject;

public class TaskAdapterViewActionListener extends AbstractObservableAdapterViewActionListener<Task> {

	@Inject
	MetricsService metricsService;

	@Inject
	IterationService iterationService;

	private final Observer observer;
	private final List<Project> projectList;

	/**
	 * Constructor
	 * @param context The context
	 * @param observer An observer object
	 */
	public TaskAdapterViewActionListener(final FragmentActivity context, final Observer observer) {
		this(context, observer, null);
	}

	/**
	 * Constructor
	 * @param context The context
	 * @param observer An observer object
	 * @param projectList The project list
	 */
	public TaskAdapterViewActionListener(final FragmentActivity context, final Observer observer,
			final List<Project> projectList) {
		super(context);

		this.observer = observer;
		this.projectList = projectList;
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public void onAction(final View view, final Task object) {
		super.onAction(view, object);

		switch (view.getId()) {
		case R.id.column_name:
			createTrackingDialog(object);
			break;

		case R.id.column_effort_left:
			createPromptDialogFragment(object);
			break;

		case R.id.column_spent_effort:
			final FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.container, SpentEffortFragment.newInstance(object));
			transaction.addToBackStack(null);
			transaction.commit();
			break;

		case R.id.column_state:
			createChangeStateDialog(object);
			break;

		case R.id.column_responsibles:
			createUserChooser(object);
			break;

		case R.id.column_context:
			getIterationDetails(object);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("checkstyle:anoninnerlength")
	private void getIterationDetails(final Task object) {
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

					// Workaround that may be patchy,
					// but it depends on the request whether it comes or not, and how to get it.
					if (iteration.getParent() == null && projectList != null) {
						for (final Project project : projectList) {
							final List<Iteration> iterationList = project.getIterationList();
							for (final Iteration parentIteration : iterationList) {
								if (parentIteration.getId() == iteration.getId()) {
									response.setParent(iteration);
									break;
								}
							}
						}
					} else {
						response.setParent(iteration.getParent());
					}

					final Intent intent = new Intent(context, IterationActivity.class);
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
	}

	private void createUserChooser(final Task object) {
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
								Toast.makeText(
									context, R.string.feedback_success_updated_project, Toast.LENGTH_SHORT).show();
							}
						},
						new ErrorListener() {
							@Override
							public void onErrorResponse(final VolleyError arg0) {
								Toast.makeText(
									context, R.string.feedback_failed_update_project, Toast.LENGTH_SHORT).show();
							}
						});
				}
			});

		context.getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, fragment)
			.addToBackStack(null)
			.commit();
	}

	private void createChangeStateDialog(final Task object) {
		final OnClickListener onChoiceSelectedListener = new OnClickListener() {

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
						}
					},
					new ErrorListener() {
						@Override
						public void onErrorResponse(final VolleyError arg0) {
							Toast.makeText(
								context, R.string.feedback_failed_update_state, Toast.LENGTH_SHORT).show();
						}
					});

				dialog.dismiss();
			}
		};

		final Builder builder = new Builder(context);
		builder.setTitle(R.string.dialog_state_title);
		builder.setSingleChoiceItems(
			StateKey.getDisplayStates(), object.getState().ordinal(), onChoiceSelectedListener);
		builder.show();
	}

	private void createPromptDialogFragment(final Task object) {
		// Agilefant's tasks that are already done, can't have it's EL changed.
		if (object.getState() != StateKey.DONE) {

			final PromptDialogFragment dialogFragment = PromptDialogFragment.newInstance(
					R.string.dialog_effortleft_title,
					// Made this way to avoid strings added in utils method.
					String.valueOf((float) object.getEffortLeft() / 60),
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
									context, R.string.feedback_succesfully_updated_effort_left, Toast.LENGTH_SHORT)
										.show();
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
	}

	private void createTrackingDialog(final Task object) {
		final Builder confirmStartTrackingBuilder = new Builder(context);
		confirmStartTrackingBuilder.setMessage(R.string.start_tracking_task_time);
		confirmStartTrackingBuilder.setPositiveButton(
			R.string.dialog_start_tracking_task_time_positive,
			new OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					final Intent intent = new Intent(context, TaskTimeTrackingService.class);
					intent.putExtra(TaskTimeTrackingService.EXTRA_TASK, object);
					context.startService(intent);
				}
			});

		confirmStartTrackingBuilder.setNegativeButton(
			R.string.dialog_start_tracking_task_time_negative,
			new OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					dialog.dismiss();
				}
			});

		final AlertDialog confirmStartTrackingDialog = confirmStartTrackingBuilder.create();
		confirmStartTrackingDialog.show();
	}

	@Override
	protected Observer getObserver() {
		return observer;
	}
}