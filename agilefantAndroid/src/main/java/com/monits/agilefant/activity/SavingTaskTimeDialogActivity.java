package com.monits.agilefant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.flurry.android.FlurryAgent;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.fragment.iteration.SpentEffortFragment;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.TaskTimeTrackingService;

public class SavingTaskTimeDialogActivity extends FragmentActivity {

	private static final long AGILEFANT_MIN_SPENT_EFFORT = 6;
	public static final String EXTRA_TASK = "com.monits.agilefant.intent.extra.TASK";
	public static final String EXTRA_ELAPSED_MILLIS = "com.monits.agilefant.intent.extra.ELAPSED_MILLIS";
	public static final int MILLIS_IN_SECOND = 1000;
	public static final int SECONDS_IN_MINUTE = 60;

	/**
	 * This factory method returns an intent of this class with it's necessary extra values and flags
	 *
	 * @param context A Context of the application package implementing this class
	 * @param task A Task object for being sent to the returned intent
	 * @return An intent that contains sent data as extra values
	 */
	public static Intent getIntent(@NonNull final Context context, @NonNull final Task task) {
		//We have to use -1 to avoid getting Effort Left in the UI initialized
		//with the minimum value instead of being empty
		return getIntent(context, task, -1);
	}

	/**
	 * This factory method returns an intent of this class with it's necessary extra values and flags
	 *
	 * @param context A Context of the application package implementing this class
	 * @param task A Task object for being sent to the returned intent
	 * @param elapsedTime a number representing time in millis
	 * @return An intent that contains sent data as extra values
	 */
	public static Intent getIntent(@NonNull final Context context, @NonNull final Task task,
								final long elapsedTime) {
		final Intent intent = new Intent(context, SavingTaskTimeDialogActivity.class);
		intent.putExtra(EXTRA_TASK, task);
		intent.putExtra(EXTRA_ELAPSED_MILLIS, elapsedTime);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_MULTIPLE_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

		return intent;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = this.getIntent();
		final Bundle extras = intent.getExtras();
		final Task task = (Task) extras.getSerializable(EXTRA_TASK);
		final long millis = extras.getLong(EXTRA_ELAPSED_MILLIS);
		final long minutes;
		//IF clause is necessary because getSpentEffort will return the minimum value constant if millis
		//is smaller than it. If millis equals -1 we need to avoid getSpentEffort
		if (millis == -1) {
			minutes = millis;
		} else {
			minutes = getSpentEffort(millisToMinutes(millis));
		}
		final SpentEffortFragment spentEffortFragment = SpentEffortFragment.newInstance(task, minutes);
		spentEffortFragment.setEffortSpentCallbacks(
			new Listener<String>() {

				@Override
				public void onResponse(final String arg0) {
					sendBroadcast(TaskTimeTrackingService.getIntent(task));

					sendBroadcast(AgilefantApplication.updateTaskTimeBroadcastIntent(task,
							AgilefantApplication.ACTION_TASK_UPDATED));

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
		return (millis / MILLIS_IN_SECOND) / SECONDS_IN_MINUTE;
	}

	private long getSpentEffort(final long minutes) {
		if (minutes < AGILEFANT_MIN_SPENT_EFFORT) {
			return AGILEFANT_MIN_SPENT_EFFORT;
		}

		return minutes;
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, AgilefantApplication.FLURRY_API_KEY);
	}

	@Override
	protected void onStop() {
		FlurryAgent.onEndSession(this);
		super.onStop();
	}
}
