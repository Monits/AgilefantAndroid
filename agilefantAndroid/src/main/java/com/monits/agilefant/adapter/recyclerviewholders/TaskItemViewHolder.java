package com.monits.agilefant.adapter.recyclerviewholders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.dialog.PromptDialogFragment;
import com.monits.agilefant.fragment.iteration.SpentEffortFragment;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.TaskTimeTrackingService;
import com.monits.agilefant.util.HoursUtils;
import com.monits.agilefant.util.InputUtils;
import com.monits.agilefant.util.IterationUtils;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by edipasquale on 25/09/15.
 */
public class TaskItemViewHolder extends WorkItemViewHolder<Task> {

	@Bind(R.id.column_name)
	/* default */ TextView columnName;

	@Bind(R.id.column_state)
	/* default */ TextView columnState;

	@Nullable
	@Bind(R.id.column_context)
	/* default */ TextView columnContext;

	@Nullable
	@Bind(R.id.column_responsibles)
	/* default */ TextView columnResponsibles;

	@Nullable
	@Bind(R.id.column_original_estimate)
	/* default */ TextView columnOriginalEstimate;

	@Nullable
	@Bind(R.id.column_spent_effort)
	/* default */ TextView columnSpentEffort;

	@Nullable
	@Bind(R.id.column_effort_left)
	/* default */ TextView columnEffortLeft;

	@Inject
	/* default */ MetricsService metricsService;

	@Inject
	/* default */ IterationService iterationService;

	private Task task;
	private final TaskItemViewHolderUpdateTracker updater;
	private final FragmentActivity context;

	/**
	 * @param itemView view's Item
	 * @param context it's context
	 * @param updater It's an update listener
	 */
	public TaskItemViewHolder(final View itemView, final FragmentActivity context,
							final TaskItemViewHolderUpdateTracker updater) {
		super(itemView);
		this.updater = updater;
		this.context = context;
		ButterKnife.bind(this, itemView);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	/**
	 * @param task This view references that Task object
	 */
	@Override
	public void onBindView(final Task task) {

		this.task = task;

		// Initialize name column
		columnName.setText(task.getName());

		// Initialize state column
		columnState.setText(IterationUtils.getStateName(task.getState()));
		columnState.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		columnState.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));

		// Initialize context column
		final Iteration iteration = task.getIteration();

		if (columnContext != null) {
			if (iteration == null) {
				columnContext.setText(context.getResources().getString(R.string.minus_expanded));
			} else {
				columnContext.setText(iteration.getName());
			}
		}

		if (columnResponsibles != null) {
			columnResponsibles.setText(IterationUtils.getResposiblesDisplay(task.getResponsibles()));
		}

		if (columnEffortLeft != null) {
			columnEffortLeft.setText(HoursUtils.convertMinutesToHours(task.getEffortLeft()));
		}

		if (columnOriginalEstimate != null) {
			columnOriginalEstimate.setText(HoursUtils.convertMinutesToHours(task.getOriginalEstimate()));
		}

		if (columnSpentEffort != null) {
			columnSpentEffort.setText(HoursUtils.convertMinutesToHours(task.getEffortSpent()));
		}
	}

	/**
	 * Click listener for state column
	 */
	@OnClick(R.id.column_state)
	/* default */ void createChangeStateDialog() {
		final DialogInterface.OnClickListener onChoiceSelectedListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				metricsService.taskChangeState(
						StateKey.values()[which], task,
						new Response.Listener<Task>() {
							@Override
							public void onResponse(final Task updatedTask) {
								update(R.string.feedback_successfully_updated_state, updatedTask);
							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(final VolleyError arg0) {
								Toast.makeText(context, R.string.feedback_failed_update_state,
										Toast.LENGTH_SHORT).show();
							}
						});

				dialog.dismiss();
			}
		};

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_state_title);
		builder.setSingleChoiceItems(
				StateKey.getDisplayStates(), task.getState().ordinal(), onChoiceSelectedListener);
		builder.show();
	}

	/**
	 * Click listener for name column
	 */
	@OnClick(R.id.column_name)
	/* default */ void createTrackingDialog() {
		final AlertDialog.Builder confirmStartTrackingBuilder = new AlertDialog.Builder(context);
		confirmStartTrackingBuilder.setMessage(R.string.start_tracking_task_time);
		confirmStartTrackingBuilder.setPositiveButton(
				R.string.dialog_start_tracking_task_time_positive,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						final Intent intent = new Intent(context, TaskTimeTrackingService.class);
						intent.putExtra(TaskTimeTrackingService.EXTRA_TASK, task);
						context.startService(intent);
					}
				});

		confirmStartTrackingBuilder.setNegativeButton(
				R.string.dialog_start_tracking_task_time_negative,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
					}
				});

		confirmStartTrackingBuilder.create().show();
	}

	/**
	 * Click listener for context column
	 */
	@Nullable
	@OnClick(R.id.column_context)
	/* default */ void getIterationDetails() {
		final Iteration iteration = task.getIteration();

		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(context.getString(R.string.loading));
		progressDialog.show();

		iterationService.getIteration(
				iteration.getId(),
				new Response.Listener<Iteration>() {
					@Override
					public void onResponse(final Iteration response) {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						final Intent intent = new Intent(context, IterationActivity.class);
						intent.putExtra(IterationActivity.ITERATION, response);
						context.startActivity(intent);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError arg0) {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Nullable
	@OnClick(R.id.column_spent_effort)
	/* default */ void spentEffort() {
		final FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
		transaction.add(android.R.id.content, SpentEffortFragment.newInstance(task));
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Nullable
	@OnClick(R.id.column_effort_left)
	/* default */ void createPromptDialogFragment() {
		// Agilefant's tasks that are already done, can't have it's EL changed.
		if (task.getState() != StateKey.DONE) {

			final PromptDialogFragment dialogFragment = PromptDialogFragment.newInstance(
					R.string.dialog_effortleft_title,
					// Made this way to avoid strings added in utils method.
					String.valueOf((float) task.getEffortLeft() / 60),
					InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

			dialogFragment.setPromptDialogListener(new PromptDialogFragment.PromptDialogListener() {
				@Override
				public void onAccept(final String inputValue) {
					metricsService.changeEffortLeft(
							InputUtils.parseStringToDouble(inputValue),
							task,
							new Response.Listener<Task>() {
								@Override
								public void onResponse(final Task response) {
									update(R.string.feedback_succesfully_updated_effort_left, response);
								}
							},
							new Response.ErrorListener() {
								@Override
								public void onErrorResponse(final VolleyError arg0) {
									Toast.makeText(
											context, R.string.feedback_failed_update_effort_left, Toast.LENGTH_SHORT)
											.show();
								}
							});
				}
			});

			dialogFragment.show(context.getSupportFragmentManager(), "effortLeftDialog");
		}
	}

	@Nullable
	@OnClick(R.id.column_responsibles)
	/* default */ void createUserChooser() {
		final Fragment fragment = UserChooserFragment.newInstance(
				task.getResponsibles(),
				new UserChooserFragment.OnUsersSubmittedListener() {

					@Override
					public void onSubmitUsers(final List<User> users) {
						metricsService.changeTaskResponsibles(
								users,
								task,
								new Response.Listener<Task>() {
									@Override
									public void onResponse(final Task response) {
										update(R.string.feedback_success_updated_project, response);
									}
								},
								new Response.ErrorListener() {
									@Override
									public void onErrorResponse(final VolleyError arg0) {
										Toast.makeText(
												context, R.string.feedback_failed_update_project, Toast.LENGTH_SHORT)
												.show();
									}
								});
					}
				});

		context.getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, fragment)
				.addToBackStack(null)
				.commit();
	}

	private void update(@StringRes final int message, final Task updatedTask) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		updater.onUpdate(updatedTask);
	}

	/**
	 * @return Object's description as a string
	 */
	@Override
	public String toString() {
		return new StringBuilder("Task Item id ").append(task.getId())
				.append(", Task item name ").append(task.getName())
				.append(", Task item iteration ").append(task.getIteration().getName())
				.toString();
	}
}
