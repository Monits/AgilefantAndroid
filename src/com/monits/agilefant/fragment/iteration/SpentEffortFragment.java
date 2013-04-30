package com.monits.agilefant.fragment.iteration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import roboguice.fragment.RoboFragment;
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

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.dialog.DateTimePickerDialogFragment;
import com.monits.agilefant.dialog.DateTimePickerDialogFragment.OnDateSetListener;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.task.UpdateSpentEffortTask;
import com.monits.agilefant.util.DateUtils;
import com.monits.agilefant.util.HoursUtils;
import com.monits.agilefant.util.ValidationUtils;

public class SpentEffortFragment extends RoboFragment {

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";
	private static final String TASK = "task";

	@Inject
	private UserService userService;

	@Inject
	private UpdateSpentEffortTask updateSpentEffortTask;

	private TextView mDateInput;
	private Button mSubmitButton;
	private EditText mResponsiblesInput;
	private EditText mHoursInput;
	private EditText mCommentInput;

	private Task task;
	private SimpleDateFormat dateFormatter;
	private ImageButton mTriggerPickerButton;

	public static SpentEffortFragment newInstance(Task task) {
		Bundle arguments = new Bundle();
		arguments.putParcelable(TASK, task);

		SpentEffortFragment fragment = new SpentEffortFragment();
		fragment.setArguments(arguments);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		task = getArguments().getParcelable(TASK);
		dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.US);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_spent_effort, null);

		Date time = Calendar.getInstance().getTime();
		String formattedDate = dateFormatter.format(time);

		mDateInput = (TextView) view.findViewById(R.id.date);
		mDateInput.setText(formattedDate);

		mResponsiblesInput = (EditText) view.findViewById(R.id.responsibles);
		mResponsiblesInput.setText(userService.getLoggedUser().getInitials());

		mHoursInput = (EditText) view.findViewById(R.id.effort_spent);

		mCommentInput = (EditText) view.findViewById(R.id.comment);

		mTriggerPickerButton = (ImageButton) view.findViewById(R.id.datepicker_button);
		mTriggerPickerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DateTimePickerDialogFragment dateTimePickerDialogFragment = DateTimePickerDialogFragment.newInstance();
				dateTimePickerDialogFragment.setOnDateSetListener(new OnDateSetListener() {

					@Override
					public void onDateSet(Date date) {
						String formattedDate = DateUtils.formatDate(date, DATE_PATTERN);
						mDateInput.setText(formattedDate);
					}
				});

				dateTimePickerDialogFragment.show(SpentEffortFragment.this.getFragmentManager(), "datePickerDialog");
			}
		});

		mSubmitButton = (Button) view.findViewById(R.id.submit_btn);
		mSubmitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isValid()) {
					final long minutes =
							HoursUtils.convertHoursStringToMinutes(mHoursInput.getText().toString().trim());

					updateSpentEffortTask.configure(
							DateUtils.parseDate(mDateInput.getText().toString().trim(), DATE_PATTERN),
							minutes,
							mCommentInput.getText().toString(),
							task,
							userService.getLoggedUser().getId(),
							new TaskCallback<Boolean>() {

								@Override
								public void onError() {
									Toast.makeText(getActivity(), "Failed to update!", Toast.LENGTH_SHORT).show();
									getFragmentManager().popBackStack();
								}

								@Override
								public void onSuccess(Boolean response) {
									Toast.makeText(getActivity(), "Succesfully updated!", Toast.LENGTH_SHORT).show();
									getFragmentManager().popBackStack();
								}
							});

					updateSpentEffortTask.execute();
				}
			}
		});

		return view;
	}

	private boolean isValid() {
		if (!ValidationUtils.validDate(DATE_PATTERN, mDateInput.getText().toString().trim())) {
			Toast.makeText(getActivity(), R.string.error_validation_date, Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}
}
