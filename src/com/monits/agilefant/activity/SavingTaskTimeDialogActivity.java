package com.monits.agilefant.activity;

import roboguice.activity.RoboFragmentActivity;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.fragment.iteration.SpentEffortFragment;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.TaskTimeTrackingService;

public class SavingTaskTimeDialogActivity extends RoboFragmentActivity {

	private static final long AGILEFANT_MIN_SPENT_EFFORT = 6;
	public static final String EXTRA_TASK = "com.monits.agilefant.intent.extra.TASK";
	public static final String EXTRA_ELAPSED_MILLIS = "com.monits.agilefant.intent.extra.ELAPSED_MILLIS";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = this.getIntent();
		final Bundle extras = intent.getExtras();
		final Task task = (Task) extras.getSerializable(EXTRA_TASK);
		final long millis = extras.getLong(EXTRA_ELAPSED_MILLIS);
		final long minutes = getSpentEffort(millisToMinutes(millis));

		final SpentEffortFragment spentEffortFragment = SpentEffortFragment.newInstance(task, minutes);
		spentEffortFragment.setEffortSpentCallbacks(
				new Listener<String>() {

					@Override
					public void onResponse(final String arg0) {
						stopService(new Intent(SavingTaskTimeDialogActivity.this, TaskTimeTrackingService.class));

						final Intent taskUpdatedIntent = new Intent(AgilefantApplication.ACTION_TASK_UPDATED);
						taskUpdatedIntent.putExtra(AgilefantApplication.EXTRA_TASK_UPDATED, task);
						sendBroadcast(taskUpdatedIntent);

						SavingTaskTimeDialogActivity.this.finish();
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						SavingTaskTimeDialogActivity.this.finish();
					}
				});

		getSupportFragmentManager().beginTransaction()
			.replace(android.R.id.content, spentEffortFragment)
			.commit();
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
