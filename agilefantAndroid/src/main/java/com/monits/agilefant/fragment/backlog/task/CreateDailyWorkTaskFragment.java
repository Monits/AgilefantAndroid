/**
 *
 */
package com.monits.agilefant.fragment.backlog.task;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.AutoCompleteIterationsAdapter;
import com.monits.agilefant.fragment.backlog.AbstractCreateBacklogElementFragment;
import com.monits.agilefant.model.FilterableIteration;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.backlog.BacklogElementParameters;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.WorkItemService;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author gmuniz
 *
 */
public class CreateDailyWorkTaskFragment extends AbstractCreateBacklogElementFragment {

	@Inject
	/* default */ IterationService iterationService;

	@Inject
	/* default */ WorkItemService workItemService;

	@Bind(R.id.context)
	/* default */ AutoCompleteTextView autocompleteIterations;

	private Iteration iterationSelected;

	/**
	 * @return a new CreateDailyWorkTaskFragment
	 */
	public static CreateDailyWorkTaskFragment newInstance() {
		return new CreateDailyWorkTaskFragment();
	}

	@Override
	protected int getTitleResourceId() {
		return R.string.new_task;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_create_daily_task, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		final ViewSwitcher viewSwitcher = (ViewSwitcher) view.findViewById(R.id.view_switcher);
		ButterKnife.bind(this, view);

		final FragmentActivity context = getActivity();
		final AutoCompleteIterationsAdapter autoCompleteIterationsAdapter =
				new AutoCompleteIterationsAdapter(context);
		autocompleteIterations.setAdapter(autoCompleteIterationsAdapter);
		autocompleteIterations.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> adapter, final View view, final int position,
					final long id) {
				final FilterableIteration iteration =
						autoCompleteIterationsAdapter.getItem(position);

				iterationSelected = iteration.getIteration();
				iterationId = iterationSelected.getId();

				autocompleteIterations.setText(iteration.getName());
			}
		});

		iterationService.getCurrentFilterableIterations(
				new Listener<List<FilterableIteration>>() {

					@Override
					public void onResponse(final List<FilterableIteration> arg0) {
						viewSwitcher.setDisplayedChild(1);

						autoCompleteIterationsAdapter.setItems(arg0);
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						context.getFragmentManager().popBackStack();

						Toast.makeText(
								context, R.string.feedback_failed_current_iterations_retrieval, Toast.LENGTH_SHORT)
								.show();
					}
				});

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected void onSubmit(final BacklogElementParameters parameters) {
		final FragmentActivity activity = getActivity();

		final String iterationName = autocompleteIterations.getText().toString().trim();

		if (TextUtils.isEmpty(parameters.getName())) {
			Toast.makeText(activity, R.string.validation_empty_name, Toast.LENGTH_LONG)
					.show();
		} else if (TextUtils.isEmpty(iterationName)) {
			iterationId = null;

			Toast.makeText(
					activity, R.string.validation_empty_context, Toast.LENGTH_SHORT)
					.show();
		} else {

			workItemService.createTask(
					parameters,
					new Listener<Task>() {

						@Override
						public void onResponse(final Task newTask) {
							newTask.setIteration(iterationSelected);
							activity.sendBroadcast(AgilefantApplication.updateTaskTimeBroadcastIntent(
									newTask, AgilefantApplication.ACTION_NEW_TASK_WITHOUT_STORY));

							Toast.makeText(
									activity, R.string.saved_task, Toast.LENGTH_SHORT)
									.show();

							getFragmentManager().popBackStackImmediate();
						}
					},
					new ErrorListener() {

						@Override
						public void onErrorResponse(final VolleyError arg0) {
							Toast.makeText(
									activity, R.string.saved_task, Toast.LENGTH_SHORT)
									.show();
						}
					});
		}
	}

	@Override
	public String toString() {
		return "CreateDailyWorkTaskFragment {"
				+ "iterationSelected=" + iterationSelected
				+ '}';
	}
}