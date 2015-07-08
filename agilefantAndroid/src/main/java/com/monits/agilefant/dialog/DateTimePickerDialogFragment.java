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

	/**
	 * @return A new DateTimePickerDialogFragment with all the default values.
	 */
	public static DateTimePickerDialogFragment newInstance() {
		return new DateTimePickerDialogFragment();
	}

	/**
	 * A DateTimePickerDialogFragment with the given date as value
	 *
	 * @param date The date to set
	 *
	 * @return a DateTimePickerDialogFragment
	 */
	public static DateTimePickerDialogFragment newInstance(final Date date) {
		final Bundle arguments = new Bundle();
		arguments.putSerializable(DATE_KEY, date);

		final DateTimePickerDialogFragment dialogFragment = new DateTimePickerDialogFragment();
		dialogFragment.setArguments(arguments);

		return dialogFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCalendar = Calendar.getInstance();
		final Bundle arguments = getArguments();
		if (arguments != null) {
			final Date date = (Date) arguments.getSerializable(DATE_KEY);
			mCalendar.setTime(date);
		}
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final View view = View.inflate(getActivity(), R.layout.dialog_datetime_picker, null);

		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);

		mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
		mTimePicker.setIs24HourView(Boolean.TRUE);
		mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));

		final AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.dialog_datetime_title);
		builder.setView(view);

		builder.setPositiveButton(R.string.dialog_datetime_positive, new OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				if (mListener != null) {
					mListener.onDateSet(getDate());
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_datetime_negative, new OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				// Just dismiss
			}
		});

		return builder.create();
	}

	private Date getDate() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(
				mDatePicker.getYear(),
				mDatePicker.getMonth(),
				mDatePicker.getDayOfMonth(),
				mTimePicker.getCurrentHour(),
				mTimePicker.getCurrentMinute(),
				0);

		return calendar.getTime();
	}

	/**
	 * Set a listener for changes in the date.
	 * @param listener The listener to set
	 */
	public void setOnDateSetListener(final OnDateSetListener listener) {
		this.mListener = listener;
	}

	/**
	 * Listener for changes in the date
	 */
	public static interface OnDateSetListener {

		/**
		 * Called after the date is setup
		 * @param date the new date
		 */
		void onDateSet(Date date);
	}
}
