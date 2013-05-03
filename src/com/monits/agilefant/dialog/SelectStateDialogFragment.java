/**
 * 
 */
package com.monits.agilefant.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.monits.agilefant.R;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;

/**
 * @author gmuniz
 *
 */
public class SelectStateDialogFragment extends DialogFragment {

	private static final String ARGUMENT_TASK = "argument_task";
	private Task mTask;

	public static SelectStateDialogFragment newInstance(Task task) {
		SelectStateDialogFragment dialogFragment = new SelectStateDialogFragment();

		Bundle arguments = new Bundle();
		arguments.putSerializable(ARGUMENT_TASK, task);
		dialogFragment.setArguments(arguments);

		return dialogFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTask = (Task) getArguments().getSerializable(ARGUMENT_TASK);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		OnClickListener onChoiceSelectedListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTask.setState(StateKey.values()[which]);
				dialog.dismiss();
			}
		};

		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.dialog_state_title);
		builder.setSingleChoiceItems(
				StateKey.getDisplayStates(), mTask.getState().ordinal(), onChoiceSelectedListener);

		return builder.create();
	}
}