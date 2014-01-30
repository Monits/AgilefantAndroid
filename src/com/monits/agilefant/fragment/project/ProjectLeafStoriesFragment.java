package com.monits.agilefant.fragment.project;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.adapter.ProjectLeafStoriesAdapter;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment.OnUsersSubmittedListener;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.ProjectService;

public class ProjectLeafStoriesFragment extends RoboFragment implements Observer {

	private static final String BACKLOG = "PROJECT_BACKLOG";

	private Backlog backlog;

	@Inject
	private ProjectService projectService;

	@Inject
	private MetricsService metricsService;

	@Inject
	private IterationService iterationService;

	private ViewFlipper viewFlipper;
	private ListView storiesListView;
	private View storiesEmptyView;

	private ProjectLeafStoriesAdapter storiesAdapter;

	public static ProjectLeafStoriesFragment newInstance(final Backlog projectBacklog) {
		final ProjectLeafStoriesFragment fragment = new ProjectLeafStoriesFragment();

		final Bundle args = new Bundle();
		args.putSerializable(BACKLOG, projectBacklog);

		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		final Bundle arguments = getArguments();
		backlog = (Backlog) arguments.getSerializable(BACKLOG);

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view = LayoutInflater.from(getActivity())
			.inflate(R.layout.fragment_project_leaf_stories, container, false);

		viewFlipper = (ViewFlipper) view.findViewById(R.id.root_flipper);

		final FragmentActivity context = getActivity();
		storiesAdapter = new ProjectLeafStoriesAdapter(context);
		storiesAdapter.setOnActionListener(new AdapterViewActionListener<Story>() {

			@Override
			public void onAction(final View view, final Story object) {

				object.addObserver(ProjectLeafStoriesFragment.this);

				switch (view.getId()) {
					case R.id.column_responsibles:
						final Fragment fragment = UserChooserFragment.newInstance(
								object.getResponsibles(),
								new OnUsersSubmittedListener() {

									@Override
									public void onSubmitUsers(final List<User> users) {
										metricsService.changeStoryResponsibles(
												users,
												object,
												new Listener<Story>() {

													@Override
													public void onResponse(final Story project) {
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

						context.getSupportFragmentManager().beginTransaction()
							.add(android.R.id.content, fragment)
							.addToBackStack(null)
							.commit();

						break;
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

								// In this case, setting story to done, doesn't prompt if you want to set all tasks to done
								executeUpdateStoryTask(state, object, false);

								dialog.dismiss();
							}
						};

						final AlertDialog.Builder builder = new Builder(context);
						builder.setTitle(R.string.dialog_state_title);
						builder.setSingleChoiceItems(
								StateKey.getDisplayStates(), object.getState().ordinal(), onStoryStateSelectedListener);
						builder.show();

						break;
				}
			}
		});

		storiesListView = (ListView) view.findViewById(R.id.stories_list);
		storiesEmptyView = view.findViewById(R.id.stories_empty_view);
		storiesListView.setEmptyView(storiesEmptyView);
		storiesListView.setAdapter(storiesAdapter);

		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		projectService.getProjectLeafStories(
				backlog.getId(),
				new Listener<List<Story>>() {

					@Override
					public void onResponse(final List<Story> stories) {
						storiesAdapter.setStories(stories);

						viewFlipper.setDisplayedChild(1);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(getActivity(), "Failed to retrieve leaf stories.", Toast.LENGTH_SHORT).show();
					}
				});

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void update(final Observable observable, final Object data) {
		if (isVisible()) {
			storiesAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}

	/**
	 * Configures and executes the {@link UpdateStoryTask}.
	 *
	 * @param state
	 * @param story
	 * @param allTasksToDone
	 */
	private void executeUpdateStoryTask(final StateKey state, final Story story, final boolean allTasksToDone) {
		final FragmentActivity context = getActivity();
		metricsService.changeStoryState(
				state,
				story,
				allTasksToDone,
				new Listener<Story>() {

					@Override
					public void onResponse(final Story arg0) {
						Toast.makeText(
								context, R.string.feedback_successfully_updated_story, Toast.LENGTH_SHORT).show();
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(
								context, R.string.feedback_failed_update_story, Toast.LENGTH_SHORT).show();
					}
				});
	}
}
