package com.monits.agilefant.adapter.recyclerviewholders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.monits.agilefant.R;
import com.monits.agilefant.activity.SavingTaskTimeDialogActivity;
import com.monits.agilefant.service.TaskTimeTrackingService;

import butterknife.OnClick;

public class DailyWorkTaskItemViewHolder extends TaskItemViewHolder {

	/**
	 * @param itemView view's Item
	 * @param context  it's context
	 * @param updater  It's an update listener
	 */
	public DailyWorkTaskItemViewHolder(final View itemView, final FragmentActivity context,
			final WorkItemViewHolderUpdateTracker updater) {
		super(itemView, context, updater);
	}

	@OnClick(R.id.column_name)
	@Override
	/* default */ void createTrackingDialog() {

		final DialogInterface.OnClickListener onChoiceSelectedListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				switch (which) {
				// If update option
				case 0:
					startTrackingService();
					break;

				// If track option is selected
				case 1:
					startIntentSavingTaskTime();
					break;

				default:
					throw new AssertionError("If the option is not listed above then there's something broken");
				}
				dialog.dismiss();
			}
		};

		final AlertDialog.Builder selectOptionDialogBuilder = new AlertDialog.Builder(context);
		selectOptionDialogBuilder.setTitle(R.string.start_tracking_or_update);

		selectOptionDialogBuilder.setSingleChoiceItems(new String[] {
				context.getResources().getString(R.string.dialog_start_tracking_task_time_track),
				context.getResources().getString(R.string.dialog_start_tracking_task_effort_spent),
		}, -1, onChoiceSelectedListener);

		selectOptionDialogBuilder.setNegativeButton(
				R.string.dialog_start_tracking_task_time_negative,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
					}
				});

		selectOptionDialogBuilder.create().show();
	}

	private void startTrackingService() {
		final Intent intent = new Intent(context, TaskTimeTrackingService.class);
		intent.putExtra(TaskTimeTrackingService.EXTRA_TASK, task);
		context.startService(intent);
	}

	private void startIntentSavingTaskTime() {
		context.startActivity(SavingTaskTimeDialogActivity.getIntent(context, task));
	}
}
