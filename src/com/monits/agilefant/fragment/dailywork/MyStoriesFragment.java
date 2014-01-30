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
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.adapter.MyStoriesAdapter;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;

public class MyStoriesFragment extends RoboFragment implements Observer {

	private static final String STORIES_KEY = "STORIES";

	@Inject
	private IterationService iterationService;

	@Inject
	private MetricsService metricsService;

	private MyStoriesAdapter storiesAdapter;

	private List<Story> mStories;

	public static MyStoriesFragment newInstance(final List<Story> stories) {
		final MyStoriesFragment storiesFragment = new MyStoriesFragment();
		final Bundle arguments = new Bundle();

		final ArrayList<Story> myStories = new ArrayList<Story>();
		myStories.addAll(stories);
		arguments.putParcelableArrayList(STORIES_KEY, myStories);

		storiesFragment.setArguments(arguments);

		return storiesFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mStories = getArguments().getParcelableArrayList(STORIES_KEY);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_stories, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ExpandableListView expandableStories = (ExpandableListView) view.findViewById(R.id.my_stories_expandable);
		final View emptyView = view.findViewById(R.id.my_stories_empty_view);

		storiesAdapter = new MyStoriesAdapter(getActivity(), mStories);
		storiesAdapter.setOnGroupActionListener(new AdapterViewActionListener<Story>() {

			@Override
			public void onAction(final View view, final Story object) {
				object.addObserver(MyStoriesFragment.this);

				final FragmentActivity context = getActivity();
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
						final OnClickListener onStoryStateSelectedListener = new DialogInterface.OnClickListener() {

							@Override
							public void onClick(final DialogInterface dialog, final int which) {
								final StateKey state = StateKey.values()[which];

								if (state == StateKey.DONE) {
									final AlertDialog.Builder builder = new Builder(getActivity());
									builder.setTitle(R.string.dialog_tasks_to_done_title)
										.setMessage(R.string.dialog_tasks_to_done_msg)
										.setPositiveButton(android.R.string.yes, new OnClickListener() {

											@Override
											public void onClick(final DialogInterface dialog, final int which) {
												executeUpdateStoryTask(state, object, true);
											}
										})
										.setNegativeButton(android.R.string.no, new OnClickListener() {

											@Override
											public void onClick(final DialogInterface dialog, final int which) {
												executeUpdateStoryTask(state, object, false);
											}
										});

									builder.show();
								} else {
									executeUpdateStoryTask(state, object, false);
								}

								dialog.dismiss();
							}
						};

						final AlertDialog.Builder builder = new Builder(getActivity());
						builder.setTitle(R.string.dialog_state_title);
						builder.setSingleChoiceItems(
								StateKey.getDisplayStates(), object.getState().ordinal(), onStoryStateSelectedListener);
						builder.show();

						break;
				}
			}
		});

		storiesAdapter.setOnChildActionListener(new AdapterViewActionListener<Task>() {

			@Override
			public void onAction(final View view, final Task object) {
				object.addObserver(MyStoriesFragment.this);

				switch (view.getId()) {
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

		expandableStories.setAdapter(storiesAdapter);
		expandableStories.setEmptyView(emptyView);
	}

	/**
	 * Configures and executes the update story task.
	 *
	 * @param state
	 * @param story
	 * @param allTasksToDone
	 */
	private void executeUpdateStoryTask(final StateKey state, final Story story, final boolean allTasksToDone) {

		metricsService.changeStoryState(
				state,
				story,
				allTasksToDone,
				new Listener<Story>() {

					@Override
					public void onResponse(final Story arg0) {
						Toast.makeText(
								getActivity(), R.string.feedback_successfully_updated_story, Toast.LENGTH_SHORT).show();
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(
								getActivity(), R.string.feedback_failed_update_story, Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public void update(final Observable observable, final Object data) {

		if (isVisible()) {
			storiesAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}
}