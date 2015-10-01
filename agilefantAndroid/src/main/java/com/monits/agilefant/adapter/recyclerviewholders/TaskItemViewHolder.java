package com.monits.agilefant.adapter.recyclerviewholders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.TaskTimeTrackingService;
import com.monits.agilefant.util.IterationUtils;


import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by edipasquale on 25/09/15.
 */
public class TaskItemViewHolder extends RecyclerView.ViewHolder {

	private final Context context;

	@Bind(R.id.column_name)
	TextView columnName;

	@Bind(R.id.column_state)
	TextView columnState;

	@Bind(R.id.column_context)
	TextView columnContext;

	@Inject
	MetricsService metricsService;

	@Inject
	IterationService iterationService;

	private Task task;

	private final TaskItemViewHolderUpdateTracker updater;

	/**
	 * @param itemView view's Item
	 * @param context it's context
	 * @param updater It's an update listener
	 */
	public TaskItemViewHolder(final View itemView, final Context context,
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
	public void onBindView(final Task task) {

		this.task = task;

		// Initialize name column
		columnName.setText(task.getName());

		// Initialize state column
		columnState.setText(IterationUtils.getStateName(task.getState()));
		columnState.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(task.getState())));
		columnState.setBackgroundResource(IterationUtils.getStateBackground(task.getState()));

		// Initialize context column
		if (task.getIteration() == null) {
			columnContext.setText(context.getResources().getString(R.string.minus_expanded));
		} else {
			columnContext.setText(task.getIteration().getName());
		}
	}

	/**
	 * Click listener for state column
	 */
	@OnClick(R.id.column_state)
	void createChangeStateDialog() {
		final DialogInterface.OnClickListener onChoiceSelectedListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				metricsService.taskChangeState(
						StateKey.values()[which], task,
						new Response.Listener<Task>() {
							@Override
							public void onResponse(final Task updatedTask) {
								Toast.makeText(
										context, R.string.feedback_successfully_updated_state,
										Toast.LENGTH_SHORT).show();
								updater.onUpdate(updatedTask);
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
	void createTrackingDialog() {
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
	@OnClick(R.id.column_context)
	void getIterationDetails() {
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
