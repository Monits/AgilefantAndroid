package com.monits.agilefant.service.rank;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.monits.agilefant.model.Rankable;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.TaskTypeRank;
import com.monits.agilefant.network.request.UrlGsonRequest;
import com.monits.agilefant.service.AgilefantService;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Created by edipasquale on 26/11/15.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class RankService {

	private static final String RANK_TASK_UNDER_ACTION = "%1$s/ajax/rankTaskAndMoveUnder.action";
	private static final String RANK_DAILY_TASK_UNDER_ACTION = "%1$s/ajax/rankDailyTaskAndMoveUnder.action";
	private static final String RANK_UNDER_ID = "rankUnderId";

	protected static final String TASK_ID = "taskId";

	private final AgilefantService agilefantService;

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	private final Gson gson;

	/**
	 * Default constructor
	 *
	 * @param agilefantService Injected by Dagger
	 * @param gson             Injected by Dagger
	 */
	public RankService(final AgilefantService agilefantService, final Gson gson) {
		this.agilefantService = agilefantService;
		this.gson = gson;
	}

	/**
	 * Updates Task's rank under the given target task
	 *
	 * @param taskTypeRank  Type of task being ranked
	 * @param params        The parameters to be sent
	 * @param allTasks      The tasks list, it's used for rollingback changes if request fails
	 * @param task          The moved task
	 * @param targetTask    The target task
	 * @param listener      Callback if the request was successful
	 * @param errorListener Callback if the request failed
	 */
	protected void rankTaskUnder(final TaskTypeRank taskTypeRank, final Map<String, String> params,
								final List<Task> allTasks, final Task task, final Task targetTask,
								final Response.Listener<Task> listener, final Response.ErrorListener errorListener) {

		final String url = taskTypeRank == TaskTypeRank.DAILY_TASK ? urlFormat(RANK_DAILY_TASK_UNDER_ACTION)
				: urlFormat(RANK_TASK_UNDER_ACTION);

		final List<Task> fallbackTasksList = new LinkedList<>();
		copyAndSetRank(allTasks, fallbackTasksList);

		params.put(TASK_ID, String.valueOf(task.getId()));
		params.put(RANK_UNDER_ID, String.valueOf(targetTask == null ? -1 : targetTask.getId()));

		final UrlGsonRequest<Task> request = new UrlGsonRequest<Task>(
				Request.Method.POST, url, gson, Task.class, listener,
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(final VolleyError volleyError) {
						rollbackRanks(allTasks, fallbackTasksList);
						errorListener.onErrorResponse(volleyError);
					}
				}, null) {

			@Override
			protected Map<String, String> getParams() {
				return params;
			}
		};

		agilefantService.addRequest(request);
	}

	/**
	 * Updates the ranks of the items in the source list with the ones in the fallbacklist.
	 *
	 * @param <T>          the class to rank
	 * @param source       the list to be updated.
	 * @param fallbackList the list containing the values to be updated with.
	 */
	protected <T extends Rankable<T>> void rollbackRanks(final List<T> source, final List<T> fallbackList) {

		for (final T currentTaskAt : source) {

			final int indexOfFallbackTask = fallbackList.indexOf(currentTaskAt);
			final T fallbackTask = fallbackList.get(indexOfFallbackTask);

			currentTaskAt.setRank(fallbackTask.getRank());
		}
	}

	/**
	 * This method clones the source items into the fallback list, and updates the ranks of the source list.
	 *
	 * @param <T>    the class to rank
	 * @param source the original list.
	 * @param copy   the list where cloned items will be added.
	 */
	protected <T extends Rankable<T>> void copyAndSetRank(final List<T> source, final List<T> copy) {

		for (int i = 0; i < source.size(); i++) {
			final T itemAt = source.get(i);

			copy.add(itemAt.getCopy());

			// Rank is equal to the index in the list.
			itemAt.setRank(i);
		}
	}

	protected String urlFormat(final String url) {
		return String.format(Locale.getDefault(), url, agilefantService.getHost());
	}
}
