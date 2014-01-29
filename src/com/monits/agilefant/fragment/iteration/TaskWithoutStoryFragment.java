package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.TaskWithoutStoryAdapter;
import com.monits.agilefant.dialog.PromptDialogFragment;
import com.monits.agilefant.dialog.PromptDialogFragment.PromptDialogListener;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment.OnUsersSubmittedListener;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.util.InputUtils;

public class TaskWithoutStoryFragment extends RoboFragment implements Observer {

	@Inject
	private MetricsService metricsService;

	private List<Task> taskWithoutStory;

	private ListView taskWithoutStoryListView;

	private TaskWithoutStoryAdapter taskWithoutStoryAdapter;


	public static TaskWithoutStoryFragment newInstance(final ArrayList<Task> taskWithoutStory) {
		final Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("TASK_WITHOUT_STORIES", taskWithoutStory);

		final TaskWithoutStoryFragment taskWithoutStoryFragment = new TaskWithoutStoryFragment();
		taskWithoutStoryFragment.setArguments(bundle);

		return taskWithoutStoryFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle arguments = getArguments();
		this.taskWithoutStory= arguments.getParcelableArrayList("TASK_WITHOUT_STORIES");
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_task_without_story, container, false);

		taskWithoutStoryListView = (ListView)rootView.findViewById(R.id.task_without_story);
		taskWithoutStoryListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		taskWithoutStoryAdapter = new TaskWithoutStoryAdapter(getActivity(), taskWithoutStory);
		taskWithoutStoryAdapter.setOnActionListener(new AdapterViewActionListener<Task>() {

			@Override
			public void onAction(final View view, final Task object) {
				object.addObserver(TaskWithoutStoryFragment.this);

				switch (view.getId()) {
				case R.id.task_effort_left:

					// Agilefant's tasks that are already done, can't have it's EL changed.
					if (!object.getState().equals(StateKey.DONE)) {

						final PromptDialogFragment dialogFragment = PromptDialogFragment.newInstance(
								R.string.dialog_effortleft_title,
								String.valueOf((float) object.getEffortLeft() / 60), // Made this way to avoid strings added in utils method.
								InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);

						dialogFragment.setPromptDialogListener(new PromptDialogListener() {

							@Override
							public void onAccept(final String inputValue) {
								metricsService.changeEffortLeft(
										InputUtils.parseStringToDouble(inputValue),
										object,
										new Listener<Task>() {

											@Override
											public void onResponse(final Task arg0) {
												Toast.makeText(
														getActivity(), R.string.feedback_succesfully_updated_effort_left, Toast.LENGTH_SHORT).show();
											}
										},
										new ErrorListener() {

											@Override
											public void onErrorResponse(final VolleyError arg0) {
												Toast.makeText(
														getActivity(), R.string.feedback_failed_update_effort_left, Toast.LENGTH_SHORT).show();
											}
										});
							}
						});

						dialogFragment.show(getFragmentManager(), "effortLeftDialog");
					}

					break;

				case R.id.task_spend_effort:

					final FragmentTransaction transaction = getParentFragment().getFragmentManager().beginTransaction();
					transaction.add(R.id.container, SpentEffortFragment.newInstance(object));
					transaction.addToBackStack(null);
					transaction.commit();

					break;

				case R.id.task_state:

					final OnClickListener onChoiceSelectedListener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog, final int which) {
							metricsService.taskChangeState(
									StateKey.values()[which],
									object,
									new Listener<Task>() {

										@Override
										public void onResponse(final Task arg0) {
											Toast.makeText(
													getActivity(), R.string.feedback_successfully_updated_state, Toast.LENGTH_SHORT).show();
										};
									},
									new ErrorListener() {

										@Override
										public void onErrorResponse(final VolleyError arg0) {
											Toast.makeText(
													getActivity(), R.string.feedback_failed_update_state, Toast.LENGTH_SHORT).show();
										};
									});

							dialog.dismiss();
						}
					};

					final AlertDialog.Builder builder = new Builder(getActivity());
					builder.setTitle(R.string.dialog_state_title);
					builder.setSingleChoiceItems(
							StateKey.getDisplayStates(), object.getState().ordinal(), onChoiceSelectedListener);
					builder.show();

					break;

				case R.id.task_responsibles:
					final Fragment fragment = UserChooserFragment.newInstance(
							object.getResponsibles(),
							new OnUsersSubmittedListener() {

								@Override
								public void onSubmitUsers(final List<User> users) {
									final Context context = getActivity();
									metricsService.changeTaskResponsibles(
											users,
											object,
											new Listener<Task>() {

												@Override
												public void onResponse(final Task project) {
													Toast.makeText(context, R.string.feedback_success_updated_project, Toast.LENGTH_SHORT).show();
												}
											},
											new ErrorListener() {

												@Override
												public void onErrorResponse(final VolleyError arg0) {
													Toast.makeText(context, R.string.feedback_failed_update_project, Toast.LENGTH_SHORT).show();
												}
											});
								}
							});

					getActivity().getSupportFragmentManager().beginTransaction()
						.add(android.R.id.content, fragment)
						.addToBackStack(null)
						.commit();

					break;
				}
			}
		});

		taskWithoutStoryListView.setAdapter(taskWithoutStoryAdapter);
		return rootView;
	}

	@Override
	public void update(final Observable observable, final Object data) {
		if (isVisible()) {
			taskWithoutStoryAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}

}
