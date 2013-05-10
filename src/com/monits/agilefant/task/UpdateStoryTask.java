package com.monits.agilefant.task;

import java.util.List;

import roboguice.util.RoboAsyncTask;
import android.content.Context;

import com.google.inject.Inject;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.service.MetricsService;

public class UpdateStoryTask extends RoboAsyncTask<Story> {

	@Inject
	private MetricsService metricsService;

	// PARAMS
	private StateKey state;
	private Story story;
	private long backlogId;
	private long iterationId;
	private boolean tasksToDone;

	private boolean isSuccess;
	private TaskCallback<Story> callback;

	private Story fallbackStory;

	@Inject
	protected UpdateStoryTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		super.onPreExecute();

		fallbackStory = story.clone();
		story.setState(state);
		if (tasksToDone) {
			for (com.monits.agilefant.model.Task task : story.getTasks()) {
				task.setState(StateKey.DONE, true);
			}
		}
	}

	@Override
	public Story call() throws Exception {
		return metricsService.changeStoryState(state, story.getId(), backlogId, iterationId, tasksToDone);
	}

	@Override
	protected void onSuccess(Story t) throws Exception {
		super.onSuccess(t);

		isSuccess = true;
		story.setState(t.getState());
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		isSuccess = false;
	}

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();

		if (isSuccess) {
			if (callback != null) {
				callback.onSuccess(story);
			}
		} else {
			story.setState(fallbackStory.getState());
			if (tasksToDone) {
				List<com.monits.agilefant.model.Task> currentTasks = story.getTasks();
				List<com.monits.agilefant.model.Task> fallbackTasks = fallbackStory.getTasks();
				for (int i = 0; i < currentTasks.size(); i++) {
					currentTasks.get(i).setState(fallbackTasks.get(i).getState());
				}
			}

			if (callback != null) {
				callback.onError();
			}
		}
	}

	public void configure(StateKey state, Story story, boolean tasksToDone, TaskCallback<Story> callback) {
		this.state = state;
		this.story = story;
		this.backlogId = story.getIteration().getId();
		this.iterationId = story.getIteration().getId();
		this.tasksToDone = tasksToDone;
		this.callback = callback;
	}

}
