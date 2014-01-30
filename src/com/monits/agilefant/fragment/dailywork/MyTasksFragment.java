package com.monits.agilefant.fragment.dailywork;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.adapter.MyTasksAdapter;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.DailyWorkTask;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;

public class MyTasksFragment extends RoboFragment implements Observer {

	private static final String TASKS_KEY = "TASKS";

	@Inject
	private IterationService iterationService;

	@Inject
	private MetricsService metricsService;

	private List<DailyWorkTask> mTasks;

	private MyTasksAdapter tasksAdapter;

	public static MyTasksFragment newInstance(final List<DailyWorkTask> taskWithoutStories) {
		final MyTasksFragment tasksFragment = new MyTasksFragment();
		final Bundle arguments = new Bundle();

		final ArrayList<DailyWorkTask> tasks = new ArrayList<DailyWorkTask>();
		tasks.addAll(taskWithoutStories);
		arguments.putParcelableArrayList(TASKS_KEY, tasks);

		tasksFragment.setArguments(arguments);

		return tasksFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTasks = getArguments().getParcelableArrayList(TASKS_KEY);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_tasks, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ListView tasksListView = (ListView) view.findViewById(R.id.tasks_list);
		final View emptyView = view.findViewById(R.id.tasks_empty_view);

		tasksAdapter = new MyTasksAdapter(getActivity(), mTasks);
		tasksAdapter.setOnActionListener(new AdapterViewActionListener<DailyWorkTask>() {

			@Override
			public void onAction(final View view, final DailyWorkTask object) {
				final FragmentActivity context = getActivity();

				object.addObserver(MyTasksFragment.this);

				switch (view.getId()) {
					case R.id.column_context:
						final Iteration iteration = object.getIteration();

						final ProgressDialog progressDialog = new ProgressDialog(context);
						progressDialog.setIndeterminate(true);
						progressDialog.setCancelable(false);
						progressDialog.setMessage(context.getString(R.string.loading));
						progressDialog.show();
						iterationService.getIteration(
								iteration.getId(),
								new Listener<Iteration>() {

									@Override
									public void onResponse(final Iteration response) {
										if (progressDialog != null && progressDialog.isShowing()) {
											progressDialog.dismiss();
										}

										final Intent intent = new Intent(context, IterationActivity.class);

										// Workaround that may be patchy, but it depends on the request whether it comes or not, and how to get it.
										response.setParent(iteration.getParent());

										intent.putExtra(IterationActivity.ITERATION, response);

										context.startActivity(intent);
									}
								},
								new ErrorListener() {

									@Override
									public void onErrorResponse(final VolleyError arg0) {
										if (progressDialog != null && progressDialog.isShowing()) {
											progressDialog.dismiss();
										}

										Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
									}
								});
						break;
					case R.id.column_state:
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
				}
			}
		});
		tasksListView.setAdapter(tasksAdapter);
		tasksListView.setEmptyView(emptyView);
	}

	@Override
	public void update(final Observable observable, final Object arg1) {

		if (isVisible()) {
			tasksAdapter.notifyDataSetChanged();
			observable.deleteObserver(MyTasksFragment.this);
		}
	}
}