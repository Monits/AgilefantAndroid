package com.monits.agilefant.fragment.project;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.IterationAdapter;
import com.monits.agilefant.adapter.ProjectLeafStoriesAdapter;
import com.monits.agilefant.listeners.AdapterViewOnLongActionListener;
import com.monits.agilefant.listeners.OnSwapRowListener;
import com.monits.agilefant.listeners.implementations.StoryAdapterViewActionListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.service.ProjectService;
import com.monits.agilefant.view.DynamicListView;

public class ProjectLeafStoriesFragment extends RoboFragment implements Observer {

	private static final String BACKLOG = "PROJECT_BACKLOG";

	private Project project;

	@Inject
	private ProjectService projectService;

	@Inject
	private MetricsService metricsService;

	private ViewFlipper viewFlipper;
	private DynamicListView storiesListView;
	private View storiesEmptyView;

	private ProjectLeafStoriesAdapter storiesAdapter;

	private List<Story> stories;

	public static ProjectLeafStoriesFragment newInstance(final Project projectBacklog) {
		final ProjectLeafStoriesFragment fragment = new ProjectLeafStoriesFragment();

		final Bundle args = new Bundle();
		args.putSerializable(BACKLOG, projectBacklog);

		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		final Bundle arguments = getArguments();
		project = (Project) arguments.getSerializable(BACKLOG);

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
		storiesAdapter.setOnActionListener(new StoryAdapterViewActionListener(context, ProjectLeafStoriesFragment.this));
		storiesAdapter.setOnLongActionListener(new AdapterViewOnLongActionListener<Story>() {

			@Override
			public boolean onLongAction(final View view, final Story object) {
				object.addObserver(ProjectLeafStoriesFragment.this);

				final List<Iteration> iterations = new LinkedList<Iteration>(project.getIterationList());

				final Iteration falseIteration = new Iteration();
				falseIteration.setName(project.getTitle());
				iterations.add(0, falseIteration);

				int currentIterationIndex = iterations.indexOf(object.getIteration());
				if (currentIterationIndex == -1) {
					currentIterationIndex = 0;
				}

				final IterationAdapter iterationAdapter = new IterationAdapter(context, iterations, currentIterationIndex);

				final ListView listView = new ListView(getActivity());
				listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				listView.setAdapter(iterationAdapter);

				final AlertDialog.Builder builder = new Builder(context);
				builder.setTitle(R.string.iteration_);
				builder.setView(listView);
				final AlertDialog dialog = builder.create();

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
						for (int i = 0; i < listView.getCount(); i++) {
							listView.setItemChecked(i, false);
						}

						listView.setItemChecked(position, true);

						final Iteration iteration = position == 0 ? null : iterations.get(position);
						metricsService.moveStory(
								object,
								iteration,
								new Listener<Story>() {

									@Override
									public void onResponse(final Story response) {
										Toast.makeText(context, R.string.feedback_ok_to_move_story, Toast.LENGTH_SHORT).show();
									}
								},
								new ErrorListener() {
									@Override
									public void onErrorResponse(final VolleyError arg0) {
										Toast.makeText(context, R.string.feedback_failed_to_move_story, Toast.LENGTH_SHORT).show();
									}
								}
							);

						dialog.dismiss();
					}
				});

				dialog.show();

				return true;
			}
		});

		storiesListView = (DynamicListView) view.findViewById(R.id.stories_list);
		storiesEmptyView = view.findViewById(R.id.stories_empty_view);
		storiesListView.setEmptyView(storiesEmptyView);
		storiesListView.setAdapter(storiesAdapter);
		storiesListView.setOnSwapRowListener(new OnSwapLeafStoriesListener(context));

		return view;
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		projectService.getProjectLeafStories(
				project.getId(),
				new Listener<List<Story>>() {

					@Override
					public void onResponse(final List<Story> stories) {
						ProjectLeafStoriesFragment.this.stories = stories;
						storiesListView.setItems(stories);
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
	 * An implementation for stories in backlog swap.
	 */
	private final class OnSwapLeafStoriesListener implements OnSwapRowListener {
		private final FragmentActivity context;

		private OnSwapLeafStoriesListener(final FragmentActivity context) {
			this.context = context;
		}

		@Override
		public void onSwapPositions(final int itemPosition, final int targetPosition,
				final SwapDirection swapDirection, final long aboveItemId, final long belowItemId) {

			final Listener<Story> successListener = new Listener<Story>() {

				@Override
				public void onResponse(final Story arg0) {
					Toast.makeText(
							context,
							R.string.feedback_success_update_story_rank,
							Toast.LENGTH_SHORT)
							.show();
				}
			};

			final ErrorListener errorListener = new ErrorListener() {

				@Override
				public void onErrorResponse(final VolleyError arg0) {
					storiesAdapter.setStories(stories);

					Toast.makeText(
							context,
							R.string.feedback_failed_update_story_rank,
							Toast.LENGTH_SHORT)
							.show();
				}
			};

			if (aboveItemId == -1
					&& swapDirection.equals(SwapDirection.ABOVE_TARGET)) {

				metricsService.rankStoryOver(
						storiesAdapter.getItem(itemPosition),
						storiesAdapter.getItem(targetPosition),
						project.getId(),
						stories,
						successListener,
						errorListener);
			} else {
				metricsService.rankStoryUnder(
						storiesAdapter.getItem(itemPosition),
						storiesAdapter.getItem(targetPosition),
						project.getId(),
						stories,
						successListener,
						errorListener);
			}
		}
	}
}
