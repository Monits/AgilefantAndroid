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
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.monits.agilefant.R;

/**
 * @author gmuniz
 *
 */
public class PromptDialogFragment extends DialogFragment {

	private static final String INPUT_TYPE_KEY = "inputType_key";
	private static final String DEF_VALUE_KEY = "def_value_key";
	private static final String TITLE_KEY = "title_key";

	private int mTitle;
	private String mDefaultValue;
	private int mInputType;

	private EditText mDialogInput;
	private PromptDialogListener mDialogListener;

	/**
	 * Returns a new PromptDialogFragment with the given arguments
	 * @param title The title
	 * @param defValue The default value
	 * @param inputType The input type
	 *
	 * @return a new PromptDialogFragment with the given arguments
	 */
	public static PromptDialogFragment newInstance(final int title, final String defValue, final int inputType) {
		final PromptDialogFragment dialogFragment = new PromptDialogFragment();

		final Bundle arguments = new Bundle();
		arguments.putInt(TITLE_KEY, title);
		arguments.putString(DEF_VALUE_KEY, defValue);
		arguments.putInt(INPUT_TYPE_KEY, inputType);

		dialogFragment.setArguments(arguments);

		return dialogFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle arguments = getArguments();
		mTitle = arguments.getInt(TITLE_KEY);
		mDefaultValue = arguments.getString(DEF_VALUE_KEY);
		mInputType = arguments.getInt(INPUT_TYPE_KEY, InputType.TYPE_CLASS_TEXT);
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final View view = View.inflate(getActivity(), R.layout.dialog_prompt, null);

		mDialogInput = (EditText) view.findViewById(R.id.dialog_prompt_input);
		mDialogInput.setInputType(mInputType);
		mDialogInput.setText(mDefaultValue);

		final AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle(mTitle);
		builder.setView(view);

		builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				if (mDialogListener != null) {
					mDialogListener.onAccept(mDialogInput.getText().toString());
				}
			}
		});

		builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				// Just dismiss
			}
		});

		return builder.create();
	}

	/**
	 * Setter for the listener to handle dialog callbacks.
	 * 
	 * @param listener the listener.
	 */
	public void setPromptDialogListener(final PromptDialogListener listener) {
		mDialogListener = listener;
	}

	public interface PromptDialogListener {

		/**
		 * Called when the dialog is accepted.
		 * 
		 * @param inputValue the value from dialog's input.
		 */
		void onAccept(String inputValue);
	}
}
