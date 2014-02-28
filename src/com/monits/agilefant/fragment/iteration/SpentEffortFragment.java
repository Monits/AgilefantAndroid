package com.monits.agilefant.fragment.iteration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import roboguice.fragment.RoboFragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.inject.Inject;
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

public class SpentEffortFragment extends RoboFragment {

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

	private static final String TASK = "task";
	private static final String MINUTES_SPENT = "minutes_spent";

	@Inject
	private MetricsService metricsService;

	@Inject
	private UserService userService;

	private TextView mDateInput;
	private Button mSubmitButton;
	private EditText mResponsiblesInput;
	private EditText mHoursInput;
	private EditText mCommentInput;
	private ImageButton mTriggerPickerButton;
	private EditText mEffortLeftInput;


	private SimpleDateFormat dateFormatter;

	private Task task;
	private long minutesSpent = -1;

	private Listener<String> spentRequestSuccessCallback;
	private ErrorListener spentRequestFailedCallback;


	public static SpentEffortFragment newInstance(final Task task) {
		final Bundle arguments = new Bundle();
		arguments.putSerializable(TASK, task);

		final SpentEffortFragment fragment = new SpentEffortFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

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

		final Bundle arguments = getArguments();
		task = (Task) arguments.getSerializable(TASK);
		minutesSpent = arguments.getLong(MINUTES_SPENT);

		dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.US);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_spent_effort, null);

		final Date time = Calendar.getInstance().getTime();
		final String formattedDate = dateFormatter.format(time);

		mDateInput = (TextView) view.findViewById(R.id.date);
		mDateInput.setText(formattedDate);

		mResponsiblesInput = (EditText) view.findViewById(R.id.responsibles);
		mResponsiblesInput.setText(userService.getLoggedUser().getInitials());

		mHoursInput = (EditText) view.findViewById(R.id.effort_spent);

		mEffortLeftInput = (EditText) view.findViewById(R.id.effort_left);
		mEffortLeftInput.setText(String.valueOf((float) task.getEffortLeft() / 60));

		if (minutesSpent != -1) {
			final float difference = (task.getEffortLeft() - minutesSpent) / 60.0f;

			mHoursInput.setText(
					HoursUtils.convertMinutesToHours(minutesSpent));
			mEffortLeftInput.setText(
					String.valueOf(difference < 0 ? 0 : difference));
		}

		mCommentInput = (EditText) view.findViewById(R.id.comment);

		mTriggerPickerButton = (ImageButton) view.findViewById(R.id.datepicker_button);
		mTriggerPickerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final DateTimePickerDialogFragment dateTimePickerDialogFragment = DateTimePickerDialogFragment.newInstance();
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

		mSubmitButton = (Button) view.findViewById(R.id.submit_btn);
		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final Context context = SpentEffortFragment.this.getActivity();
				final StateKey taskState = task.getState();

				if (Float.valueOf(mEffortLeftInput.getText().toString()) == 0
						&& taskState != StateKey.IMPLEMENTED
						&& taskState != StateKey.DONE) {

					final AlertDialog.Builder builder = new Builder(context);
					builder.setNegativeButton(R.string.dialog_update_task_state_negative, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int which) {
							dialog.dismiss();
						}
					});
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, final int which) {
							saveEffortLeft();

							metricsService.taskChangeState(
									StateKey.IMPLEMENTED,
									task,
									new Listener<Task>() {

										@Override
										public void onResponse(final Task arg0) {
											Toast.makeText(context, R.string.feedback_successfully_updated_state, Toast.LENGTH_SHORT).show();
										}
									},
									new ErrorListener() {

										@Override
										public void onErrorResponse(
												final VolleyError arg0) {
											Toast.makeText(context, R.string.feedback_failed_update_state, Toast.LENGTH_SHORT).show();
										}
									});
							dialog.dismiss();
						}
					});
					builder.setMessage(R.string.dialog_update_task_state);

					builder.show();

				} else {
					saveEffortLeft();
				}

			}

		});

		return view;
	}

	private void saveEffortLeft() {
		final Context context = SpentEffortFragment.this.getActivity();
		if (isValid()) {
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
								Toast.makeText(context, R.string.feedback_succesfully_updated_spent_effort, Toast.LENGTH_SHORT).show();
								getFragmentManager().popBackStack();

								if (spentRequestSuccessCallback != null) {
									spentRequestSuccessCallback.onResponse(arg0);
								}
							}
						},
						new ErrorListener() {

							@Override
							public void onErrorResponse(final VolleyError arg0) {
								Toast.makeText(context, R.string.feedback_failed_update_spent_effort, Toast.LENGTH_SHORT).show();

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
								Toast.makeText(context, R.string.feedback_succesfully_updated_effort_left, Toast.LENGTH_SHORT).show();
							}
						},
						new ErrorListener() {

							@Override
							public void onErrorResponse(final VolleyError arg0) {
								Toast.makeText(context, R.string.feedback_failed_update_effort_left, Toast.LENGTH_SHORT).show();
							}
						});
			}
	}

	public void setEffortSpentCallbacks(final Listener<String> listener, final ErrorListener error) {
		this.spentRequestSuccessCallback = listener;
		this.spentRequestFailedCallback = error;
	}

	private boolean isValid() {
		if (!ValidationUtils.validDate(DATE_PATTERN, mDateInput.getText().toString().trim())) {
			Toast.makeText(getActivity(), R.string.error_validation_date, Toast.LENGTH_SHORT).show();
			return false;
		} else {
			final String hoursInputValue = mHoursInput.getText().toString().trim();
			if (hoursInputValue.equals("—")
				|| hoursInputValue.equals("")
				|| hoursInputValue.equals("0")) {
				Toast.makeText(getActivity(), R.string.error_validate_effort_left, Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		return true;
	}
}
