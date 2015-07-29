package com.monits.agilefant.fragment.project;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.IterationAdapter;
import com.monits.agilefant.adapter.ProjectLeafStoriesAdapter;
import com.monits.agilefant.fragment.backlog.story.CreateLeafStoryFragment;
import com.monits.agilefant.listeners.AdapterViewOnLongActionListener;
import com.monits.agilefant.listeners.OnSwapRowListener;
import com.monits.agilefant.listeners.implementations.StoryAdapterViewActionListener;
import com.monits.agilefant.model.Backlog;
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

	private ProjectLeafStoriesAdapter storiesAdapter;
	private List<Story> stories;

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {

			if (AgilefantApplication.ACTION_NEW_STORY.equals(intent.getAction())) {
				final Story newStory = (Story) intent.getSerializableExtra(AgilefantApplication.EXTRA_NEW_STORY);
				stories.add(newStory);

				storiesAdapter.setStories(stories);
				storiesAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * Create a new ProjectLeafStoriesFragment with the given project
	 * @param projectBacklog The project
	 * @return a new ProjectLeafStoriesFragment with the given project
	 */
	public static ProjectLeafStoriesFragment newInstance(final Project projectBacklog) {
		final ProjectLeafStoriesFragment fragment = new ProjectLeafStoriesFragment();

		final Bundle args = new Bundle();
		args.putSerializable(BACKLOG, projectBacklog);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AgilefantApplication.ACTION_NEW_STORY);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);

		super.onResume();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		final Bundle arguments = getArguments();
		project = (Project) arguments.getSerializable(BACKLOG);

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.menu_project_new_element, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_story:
			this.getActivity().getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, CreateLeafStoryFragment.newInstance(project.getId()))
				.addToBackStack(null)
				.commit();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {

		final View view =
				LayoutInflater.from(getActivity()).inflate(R.layout.fragment_project_leaf_stories, container, false);

		setHasOptionsMenu(true);

		viewFlipper = (ViewFlipper) view.findViewById(R.id.root_flipper);

		final FragmentActivity context = getActivity();

		storiesAdapter = new ProjectLeafStoriesAdapter(context);

		final Backlog backlog = new Backlog(project);
		storiesAdapter.setOnActionListener(
				new StoryAdapterViewActionListener(context, ProjectLeafStoriesFragment.this, backlog));
		storiesAdapter.setOnLongActionListener(getOnLongActionListener(context));

		storiesListView = (DynamicListView) view.findViewById(R.id.stories_list);
		final View storiesEmptyView = view.findViewById(R.id.stories_empty_view);
		storiesListView.setEmptyView(storiesEmptyView);
		storiesListView.setAdapter(storiesAdapter);
		storiesListView.setOnSwapRowListener(new OnSwapLeafStoriesListener(context));

		return view;
	}

	@SuppressWarnings("checkstyle:anoninnerlength")
	private AdapterViewOnLongActionListener<Story> getOnLongActionListener(final FragmentActivity context) {
		return new AdapterViewOnLongActionListener<Story>() {

			@Override
			public boolean onLongAction(final View view, final Story object) {
				object.addObserver(ProjectLeafStoriesFragment.this);

				final List<Iteration> iterations = new LinkedList<>(project.getIterationList());

				final Iteration falseIteration = new Iteration();
				falseIteration.setName(project.getTitle());
				iterations.add(0, falseIteration);

				int currentIterationIndex = iterations.indexOf(object.getIteration());
				if (currentIterationIndex == -1) {
					currentIterationIndex = 0;
				}

				final IterationAdapter iterationAdapter =
						new IterationAdapter(context, iterations, currentIterationIndex);

				final ListView listView = new ListView(getActivity());
				listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				listView.setAdapter(iterationAdapter);

				final Builder builder = new Builder(context);
				builder.setTitle(R.string.iteration_);
				builder.setView(listView);
				final AlertDialog dialog = builder.create();

				listView.setOnItemClickListener(getOnItemClickListener(object, iterations, listView, dialog, context));

				dialog.show();

				return true;
			}
		};
	}

	@SuppressWarnings("checkstyle:anoninnerlength")
	private OnItemClickListener getOnItemClickListener(final Story object, final List<Iteration> iterations,
			final ListView listView, final AlertDialog dialog, final FragmentActivity context) {
		return new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				for (int i = 0; i < listView.getCount(); i++) {
					listView.setItemChecked(i, false);
				}

				listView.setItemChecked(position, true);

				final Iteration iteration = position == 0 ? null : iterations.get(position);
				metricsService.moveStory(object, iteration,
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
		};
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
					Toast.makeText(context, R.string.feedback_success_update_story_rank, Toast.LENGTH_SHORT).show();
				}
			};

			final ErrorListener errorListener = new ErrorListener() {
				@Override
				public void onErrorResponse(final VolleyError arg0) {
					storiesAdapter.setStories(stories);
					Toast.makeText(context, R.string.feedback_failed_update_story_rank, Toast.LENGTH_SHORT).show();
				}
			};

			if (aboveItemId == -1 && swapDirection == SwapDirection.ABOVE_TARGET) {
				metricsService.rankStoryOver(
					storiesAdapter.getItem(itemPosition), storiesAdapter.getItem(targetPosition), project.getId(),
					stories, successListener, errorListener);
			} else {
				metricsService.rankStoryUnder(
					storiesAdapter.getItem(itemPosition), storiesAdapter.getItem(targetPosition), project.getId(),
					stories, successListener, errorListener);
			}

		}

	}
}
