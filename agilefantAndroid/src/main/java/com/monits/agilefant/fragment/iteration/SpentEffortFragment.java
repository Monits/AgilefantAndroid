package com.monits.agilefant.fragment.iteration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.dialog.DateTimePickerDialogFragment;
import com.monits.agilefant.dialog.DateTimePickerDialogFragment.OnDateSetListener;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.util.DateUtils;
import com.monits.agilefant.util.HoursUtils;
import com.monits.agilefant.util.InputUtils;
import com.monits.agilefant.util.ValidationUtils;

import javax.inject.Inject;

public class SpentEffortFragment extends Fragment {

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

	private static final String TASK = "task";
	private static final String MINUTES_SPENT = "minutes_spent";
	public static final int MINUTES = 60;

	@Inject
	MetricsService metricsService;

	@Inject
	UserService userService;

	@Bind(R.id.date)
	TextView mDateInput;
	@Bind(R.id.effort_spent)
	EditText mHoursInput;
	@Bind(R.id.comment)
	EditText mCommentInput;
	@Bind(R.id.effort_left)
	EditText mEffortLeftInput;


	private SimpleDateFormat dateFormatter;

	private Task task;
	private long minutesSpent = -1;

	private Listener<String> spentRequestSuccessCallback;
	private ErrorListener spentRequestFailedCallback;


	/**
	 * Creates a new SpentEffortFragment with the given task.
	 * @param task The task
	 * @return a new SpentEffortFragment with the given task.
	 */
	public static SpentEffortFragment newInstance(final Task task) {
		final Bundle arguments = new Bundle();
		arguments.putSerializable(TASK, task);

		final SpentEffortFragment fragment = new SpentEffortFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	/**
	 * Creates a new SpentEffortFragment with the given task and minutes.
	 * @param task The tasks
	 * @param minutes The minutes.
	 * @return a new SpentEffortFragment with the given task and minutes.
	 */
	public static SpentEffortFragment newInstance(final Task task, final long minutes) {
		final Bundle arguments = new Bundle();
		arguments.putSerializable(TASK, task);
		arguments.putLong(MINUTES_SPENT, minutes);

		final SpentEffortFragment fragment = new SpentEffortFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgilefantApplication.getObjectGraph().inject(this);

		final Bundle arguments = getArguments();
		task = (Task) arguments.getSerializable(TASK);
		minutesSpent = arguments.getLong(MINUTES_SPENT);

		dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.US);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_spent_effort, null);
		ButterKnife.bind(this, view);
		final Date time = Calendar.getInstance().getTime();
		final String formattedDate = dateFormatter.format(time);

		mDateInput.setText(formattedDate);

		final EditText mResponsiblesInput = (EditText) view.findViewById(R.id.responsibles);
		mResponsiblesInput.setText(userService.getLoggedUser().getInitials());

		mEffortLeftInput = (EditText) view.findViewById(R.id.effort_left);
		mEffortLeftInput.setText(String.valueOf((float) task.getEffortLeft() / MINUTES));

		if (minutesSpent != -1) {
			final float difference = (task.getEffortLeft() - minutesSpent) / 60.0f;

			mHoursInput.setText(HoursUtils.convertMinutesToHours(minutesSpent));
			mEffortLeftInput.setText(String.valueOf(difference < 0 ? 0 : difference));
		}

		final ImageButton mTriggerPickerButton = (ImageButton) view.findViewById(R.id.datepicker_button);
		mTriggerPickerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final DateTimePickerDialogFragment dateTimePickerDialogFragment =
						DateTimePickerDialogFragment.newInstance();
				dateTimePickerDialogFragment.setOnDateSetListener(new OnDateSetListener() {

					@Override
					public void onDateSet(final Date date) {
						final String formattedDate = DateUtils.formatDate(date, DATE_PATTERN);
						mDateInput.setText(formattedDate);
					}
				});

				dateTimePickerDialogFragment.show(SpentEffortFragment.this.getFragmentManager(), "datePickerDialog");
			}
		});

		final Button mSubmitButton = (Button) view.findViewById(R.id.submit_btn);
		mSubmitButton.setOnClickListener(getOnClickListener());

		return view;
	}

	@SuppressWarnings("checkstyle:anoninnerlength")
	private OnClickListener getOnClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Context context = SpentEffortFragment.this.getActivity();
				final StateKey taskState = task.getState();

				float el = 0;
				final String effortLeft = mEffortLeftInput.getText().toString();
				if (!"".equals(effortLeft)) {
					el = Float.parseFloat(effortLeft);
				}

				if (el == 0 && taskState != StateKey.IMPLEMENTED && taskState != StateKey.DONE) {

					final Builder builder = new Builder(context);
					builder.setNegativeButton(R.string.dialog_update_task_state_negative,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog, final int which) {
								dialog.dismiss();
							}
						});

					builder.setPositiveButton(R.string.ok, getDialogClickListener(context));

					builder.setMessage(R.string.dialog_update_task_state);
					builder.show();

				} else {
					if (isValid()) {
						saveEffortLeft();
					}
				}
			}
		};
	}

	@SuppressWarnings("checkstyle:anoninnerlength")
	private DialogInterface.OnClickListener getDialogClickListener(final Context context) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				if (isValid()) {
					saveEffortLeft();

					metricsService.taskChangeState(
						StateKey.IMPLEMENTED,
						task,
						new Listener<Task>() {
							@Override
							public void onResponse(final Task arg0) {
								Toast.makeText(context, R.string.feedback_successfully_updated_state,
										Toast.LENGTH_SHORT).show();
							}
						},
						new ErrorListener() {
							@Override
							public void onErrorResponse(final VolleyError arg0) {
								Toast.makeText(context, R.string.feedback_failed_update_state,
										Toast.LENGTH_SHORT).show();
							}
						});
					dialog.dismiss();
				}
			}
		};
	}

	private void saveEffortLeft() {
		final Context context = SpentEffortFragment.this.getActivity();
		final long minutes = HoursUtils.convertHoursStringToMinutes(mHoursInput.getText().toString().trim());

		metricsService.taskChangeSpentEffort(
			DateUtils.parseDate(mDateInput.getText().toString().trim(), DATE_PATTERN),
			minutes,
			mCommentInput.getText().toString(),
			task,
			userService.getLoggedUser().getId(),
			new Listener<String>() {

				@Override
				public void onResponse(final String arg0) {
					Toast.makeText(context, R.string.feedback_succesfully_updated_spent_effort,
							Toast.LENGTH_SHORT).show();
					getFragmentManager().popBackStack();

					if (spentRequestSuccessCallback != null) {
						spentRequestSuccessCallback.onResponse(arg0);
					}
				}
			},
			new ErrorListener() {

				@Override
				public void onErrorResponse(final VolleyError arg0) {
					Toast.makeText(context, R.string.feedback_failed_update_spent_effort,
							Toast.LENGTH_SHORT).show();

					if (spentRequestFailedCallback != null) {
						spentRequestFailedCallback.onErrorResponse(arg0);
					}
				}
			});

		metricsService.changeEffortLeft(
			InputUtils.parseStringToDouble(mEffortLeftInput.getText().toString()),
			task,
			new Listener<Task>() {

				@Override
				public void onResponse(final Task arg0) {
					Toast.makeText(context, R.string.feedback_succesfully_updated_effort_left,
							Toast.LENGTH_SHORT).show();
				}
			},
			new ErrorListener() {

				@Override
				public void onErrorResponse(final VolleyError arg0) {
					Toast.makeText(context, R.string.feedback_failed_update_effort_left,
							Toast.LENGTH_SHORT).show();
				}
			});
	}

	/**
	 * Sets the effort spent callbacks
	 * @param listener The success callback
	 * @param error The error callback
	 */
	public void setEffortSpentCallbacks(final Listener<String> listener, final ErrorListener error) {
		this.spentRequestSuccessCallback = listener;
		this.spentRequestFailedCallback = error;
	}

	private boolean isValid() {
		if (ValidationUtils.validDate(DATE_PATTERN, mDateInput.getText().toString().trim())) {
			final String hoursInputValue = mHoursInput.getText().toString().trim();
			if ("—".equals(hoursInputValue) || "".equals(hoursInputValue) || "0".equals(hoursInputValue)) {
				Toast.makeText(getActivity(), R.string.error_validate_effort_left, Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			Toast.makeText(getActivity(), R.string.error_validation_date, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
