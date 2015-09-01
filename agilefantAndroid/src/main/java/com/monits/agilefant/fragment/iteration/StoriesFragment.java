package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.StoriesAdapter;
import com.monits.agilefant.listeners.OnSwapRowListener;
import com.monits.agilefant.listeners.implementations.StoryAdapterViewActionListener;
import com.monits.agilefant.listeners.implementations.TaskAdapterViewActionListener;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.view.DynamicExpandableListView;

import javax.inject.Inject;

public class StoriesFragment extends BaseDetailTabFragment implements Observer {

	@Inject
	MetricsService metricsService;

	private static final String STORIES = "STORIES";
	private static final String ITERATION = "ITERATION";

	private List<Story> stories;

	private StoriesAdapter storiesAdapter;

	@SuppressWarnings("checkstyle:anoninnerlength")
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (AgilefantApplication.ACTION_TASK_UPDATED.equals(intent.getAction())
					&& !StoriesFragment.this.isDetached()) {

				final Task updatedTask = (Task) intent.getSerializableExtra(AgilefantApplication.EXTRA_TASK_UPDATED);

				for (final Story story : stories) {
					final List<Task> tasks = story.getTasks();
					final int indexOf = tasks.indexOf(updatedTask);
					if (indexOf != -1) {
						tasks.get(indexOf).updateValues(updatedTask);
						storiesAdapter.notifyDataSetChanged();
					}
				}
			}

			if (AgilefantApplication.ACTION_NEW_STORY.equals(intent.getAction())) {
				final Story newStory = (Story) intent.getSerializableExtra(AgilefantApplication.EXTRA_NEW_STORY);
				stories.add(newStory);

				storiesAdapter.setItems(stories);
				storiesAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * Return a new StoriesFragment with the given stories and iteration
	 * @param stories The stories
	 * @param mIteration The iteration
	 * @return a new StoriesFragment with the given stories and iteration
	 */
	public static StoriesFragment newInstance(final ArrayList<Story> stories, final Iteration mIteration) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(STORIES, stories);
		bundle.putSerializable(ITERATION, mIteration);

		final StoriesFragment storiesFragment = new StoriesFragment();
		storiesFragment.setArguments(bundle);

		return storiesFragment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AgilefantApplication.getObjectGraph().inject(this);
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AgilefantApplication.ACTION_TASK_UPDATED);
		intentFilter.addAction(AgilefantApplication.ACTION_NEW_STORY);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);

		final Bundle arguments = getArguments();
		this.stories = (List<Story>) arguments.getSerializable(STORIES);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stories, container, false);

		final DynamicExpandableListView storiesListView =
				(DynamicExpandableListView) rootView.findViewById(R.id.stories);
		storiesListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));

		final FragmentActivity context = getActivity();
		storiesAdapter = new StoriesAdapter(rootView.getContext(), stories);
		storiesAdapter.setOnChildActionListener(new TaskAdapterViewActionListener(context, StoriesFragment.this));
		storiesAdapter.setOnGroupActionListener(new StoryAdapterViewActionListener(context, StoriesFragment.this));
		storiesListView.setAdapter(storiesAdapter);
		storiesListView.setOnSwapRowListener(new OnSwapRowListener() {

			@Override
			public void onSwapPositions(final int itemPosition, final int targetPosition,
					final SwapDirection swapDirection, final long aboveItemId, final long belowItemId) {

				if (aboveItemId == -1 && swapDirection == SwapDirection.ABOVE_TARGET) {

					metricsService.rankStoryOver(
						storiesAdapter.getGroup(itemPosition), storiesAdapter.getGroup(targetPosition), stories,
						getSUccessListener(context), getErrorListener(context));
				} else {
					metricsService.rankStoryUnder(
						storiesAdapter.getGroup(itemPosition), storiesAdapter.getGroup(targetPosition), stories,
						getSUccessListener(context), getErrorListener(context));
				}
			}
		});

		return rootView;
	}

	private Listener<Story> getSUccessListener(final FragmentActivity context) {
		return new Listener<Story>() {

			@Override
			public void onResponse(final Story arg0) {
				Toast.makeText(context, R.string.feedback_success_update_story_rank, Toast.LENGTH_SHORT).show();
			}
		};
	}

	private ErrorListener getErrorListener(final FragmentActivity context) {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError arg0) {
				storiesAdapter.setItems(stories);

				Toast.makeText(context, R.string.feedback_failed_update_story_rank, Toast.LENGTH_SHORT).show();
			}
		};
	}

	@Override
	public void update(final Observable observable, final Object data) {

		if (isVisible()) {
			storiesAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public int getTitleBackgroundResourceId() {
		return R.drawable.gradient_stories_title;
	}

	@Override
	public int getColorResourceId() {
		return android.R.color.white;
	}

	@Override
	public int getTitleResourceId() {
		return R.string.stories;
	}
}
