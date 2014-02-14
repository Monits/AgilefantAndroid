package com.monits.agilefant.activity;

import java.util.Date;

import roboguice.activity.RoboActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.TaskTimeTrackingService;
import com.monits.agilefant.service.UserService;

public class SavingTaskTimeDialogActivity extends RoboActivity {

	private static final long AGILEFANT_MIN_SPENT_EFFORT = 6;
	public static final String EXTRA_TASK = "com.monits.agilefant.intent.extra.TASK";
	public static final String EXTRA_ELAPSED_MILLIS = "com.monits.agilefant.intent.extra.ELAPSED_MILLIS";

	@Inject
	private MetricsService metricsService;

	@Inject
	private UserService userService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = this.getIntent();
		final Bundle extras = intent.getExtras();
		final Task trackedTask = extras.getParcelable(EXTRA_TASK);
		final long elapsedMillis = extras.getLong(EXTRA_ELAPSED_MILLIS);

		final Builder confirmSubmitTimeBuilder = new Builder(this);
		confirmSubmitTimeBuilder.setMessage(R.string.dialog_set_task_time);
		confirmSubmitTimeBuilder.setPositiveButton(R.string.dialog_start_tracking_task_time_positive, new OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				metricsService.taskChangeSpentEffort(
						new Date(),
						getSpentEffort(millisToMinutes(elapsedMillis)),
						"",
						trackedTask,
						userService.getLoggedUser().getId(),
						new Listener<String>() {

							@Override
							public void onResponse(final String arg0) {
								Toast.makeText(SavingTaskTimeDialogActivity.this, R.string.time_saved, Toast.LENGTH_SHORT).show();

								stopService(new Intent(SavingTaskTimeDialogActivity.this, TaskTimeTrackingService.class));
							}

						},
						new ErrorListener() {
							@Override
							public void onErrorResponse(final VolleyError arg0) {
								Toast.makeText(SavingTaskTimeDialogActivity.this, R.string.error_saving_time, Toast.LENGTH_SHORT).show();
							}

						});

				SavingTaskTimeDialogActivity.this.finish();
				dialog.dismiss();
			}
		});
		confirmSubmitTimeBuilder.setNegativeButton(R.string.dialog_start_tracking_task_time_negative, new OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				sendBroadcast(new Intent(TaskTimeTrackingService.ACTION_START_TRACKING));

				SavingTaskTimeDialogActivity.this.finish();
				dialog.dismiss();
			}
		});

		final AlertDialog confirmSubmitTaskTimeDialog = confirmSubmitTimeBuilder.create();
		confirmSubmitTaskTimeDialog.show();
	}

	private long millisToMinutes(final long millis) {
		return Math.round((millis / 1000) / 60);
	}

	private long getSpentEffort(final long minutes) {
		if (minutes < AGILEFANT_MIN_SPENT_EFFORT) {
			return AGILEFANT_MIN_SPENT_EFFORT;
		}

		return minutes;
	}
}
