/**
 * 
 */
package com.monits.agilefant.dialog;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.monits.agilefant.R;


/**
 * @author gmuniz
 *
 */
public class DateTimePickerDialogFragment extends DialogFragment {

	private static final String DATE_KEY = "date_key";

	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private OnDateSetListener mListener;
	private Calendar mCalendar;

	public static DateTimePickerDialogFragment newInstance() {
		return new DateTimePickerDialogFragment();
	}

	public static DateTimePickerDialogFragment newInstance(Date date) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(DATE_KEY, date);

		DateTimePickerDialogFragment dialogFragment = new DateTimePickerDialogFragment();
		dialogFragment.setArguments(arguments);

		return dialogFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCalendar = Calendar.getInstance();
		Bundle arguments = getArguments();
		if (arguments != null) {
			Date date = (Date) arguments.getSerializable(DATE_KEY);
			mCalendar.setTime(date);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.dialog_datetime_picker, null);

		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);

		mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentHour(mCalendar.get(Calendar.MINUTE));

		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.dialog_datetime_title);
		builder.setView(view);

		builder.setPositiveButton(R.string.dialog_datetime_positive, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mListener != null) {
					mListener.onDateSet(getDate());
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_datetime_negative, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Just dismiss
			}
		});

		return builder.create();
	}

	private Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(
				mDatePicker.getYear(),
				mDatePicker.getMonth(),
				mDatePicker.getDayOfMonth(),
				mTimePicker.getCurrentHour(),
				mTimePicker.getCurrentMinute(),
				0);

		return calendar.getTime();
	}

	public void setOnDateSetListener(OnDateSetListener listener) {
		this.mListener = listener;
	}

	public static interface OnDateSetListener {
		void onDateSet(Date date);
	}
}
