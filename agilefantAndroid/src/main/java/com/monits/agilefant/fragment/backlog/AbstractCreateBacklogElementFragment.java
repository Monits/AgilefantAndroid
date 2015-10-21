package com.monits.agilefant.fragment.backlog;

import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.AutoCompleteUsersAdapter;
import com.monits.agilefant.adapter.SelectedUsersAdapter;
import com.monits.agilefant.adapter.SelectedUsersAdapter.OnRemoveUserListener;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.UserChooser;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.ui.component.AutoCompleteUserChooserTextView;
import com.monits.agilefant.util.IterationUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class AbstractCreateBacklogElementFragment extends Fragment {

	private static final String ARGUMENT_BACKLOG_ID = "backlog_id";
	private static final String ARGUMENT_ITERATION_ID = "iteration_id";

	@Inject
	/* default */ UserService userService;

	@Bind(R.id.story_name)
	/* default */ EditText storyName;

	@Bind(R.id.responsibles)
	/* default */ AutoCompleteUserChooserTextView mResponsiblesInput;

	@Bind(R.id.state)
	/* default */ TextView storyState;

	@Bind(R.id.view_switcher)
	/* default */ ViewSwitcher viewSwitcher;

	private AutoCompleteUsersAdapter autoCompleteUsersAdapter;
	private SelectedUsersAdapter selectedUsersAdapter;

	private StateKey stateKey;

	protected Long backlogId;
	protected Long iterationId;

	protected static <T extends AbstractCreateBacklogElementFragment> void prepareFragmentForBacklog(
			final Long backlogId, final T fragment) {
		final Bundle args = new Bundle();
		args.putLong(ARGUMENT_BACKLOG_ID, backlogId);
		fragment.setArguments(args);
	}

	protected static <T extends AbstractCreateBacklogElementFragment> void prepareFragmentForIteration(
			final Long iterationId, final T fragment) {
		final Bundle args = new Bundle();
		args.putLong(ARGUMENT_ITERATION_ID, iterationId);
		fragment.setArguments(args);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		final Bundle arguments = getArguments();
		final boolean hasArguments = arguments != null;
		AgilefantApplication.getObjectGraph().inject(this);
		if (hasArguments && arguments.containsKey(ARGUMENT_BACKLOG_ID)) {
			backlogId = arguments.getLong(ARGUMENT_BACKLOG_ID);
		}

		if (hasArguments && arguments.containsKey(ARGUMENT_ITERATION_ID)) {
			iterationId = arguments.getLong(ARGUMENT_ITERATION_ID);
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_create_abstract, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Context context = getActivity();

		final TextView title = (TextView) view.findViewById(R.id.dialog_title);
		title.setText(getTitleResourceId());

		stateKey = StateKey.NOT_STARTED;
		autoCompleteUsersAdapter = new AutoCompleteUsersAdapter(context);
		mResponsiblesInput.setAdapter(autoCompleteUsersAdapter);

		userService.getFilterableUsers(
				new Listener<List<UserChooser>>() {
					@Override
					public void onResponse(final List<UserChooser> response) {
						viewSwitcher.setDisplayedChild(1);

						autoCompleteUsersAdapter.setFilterableUsers(response);
					}
				},
				new ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(getActivity(), R.string.feedback_failed_retrieve_users, Toast.LENGTH_SHORT)
								.show();

						getFragmentManager().popBackStackImmediate();
					}
				});

		mResponsiblesInput.setOnUserChooserActionListener(
			new AutoCompleteUserChooserTextView.OnUserChooserActionListener() {
				@Override
				public void onUserChooserAction(final List<User> selected) {
					selectedUsersAdapter.setUsers(selected);
				}
			}
		);

		final ListView usersList = (ListView) view.findViewById(R.id.users_list);
		selectedUsersAdapter = new SelectedUsersAdapter(getActivity());
		selectedUsersAdapter.setOnRemoveUserListener(new OnRemoveUserListener() {
			@Override
			public void onRemoveUser(final View view, final int position, final User user) {
				mResponsiblesInput.removeUser(user);
			}
		});

		usersList.setAdapter(selectedUsersAdapter);

		storyState.setText(IterationUtils.getStateName(StateKey.NOT_STARTED));
		storyState.setTextColor(
			context.getResources().getColor(IterationUtils.getStateTextColor(StateKey.NOT_STARTED)));
		storyState.setBackgroundResource(IterationUtils.getStateBackground(StateKey.NOT_STARTED));
		storyState.setOnClickListener(getOnStateClickListener(context));

		final Button submitBtn = (Button) view.findViewById(R.id.submit_btn);
		submitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final BacklogElementParameters parameters = new BacklogElementParameters.Builder()
						.backlogId(backlogId)
						.iterationId(iterationId)
						.stateKey(stateKey)
						.selectedUsers(mResponsiblesInput.getSelectedUsers())
						.name(storyName.getText().toString())
						.build();

				onSubmit(parameters);
			}
		});
	}

	@Override
	public String toString() {
		return "AbstractCreateBacklogElementFragment {"
				+ "stateKey=" + stateKey
				+ ", backlogId=" + backlogId
				+ ", iterationId=" + iterationId
				+ '}';
	}

	private OnClickListener getOnStateClickListener(final Context context) {

		final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				final StateKey state = StateKey.values()[which];

				storyState.setText(IterationUtils.getStateName(state));
				storyState.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(state)));
				storyState.setBackgroundResource(IterationUtils.getStateBackground(state));
				stateKey = state;

				dialog.dismiss();
			}
		};

		return new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final AlertDialog.Builder builder = new Builder(context);
				builder.setTitle(R.string.dialog_state_title);
				builder.setSingleChoiceItems(
						StateKey.getDisplayStates(), StateKey.NOT_STARTED.ordinal(), onClickListener);

				builder.show();
			}
		};
	}

	/**
	 *
	 * @return int id from values for title of fragment
	 */
	protected abstract int getTitleResourceId();

	/**
	 *
	 * @param parameters Parameters to submit new tasks or stories
	 */
	protected abstract void onSubmit(BacklogElementParameters parameters);
}