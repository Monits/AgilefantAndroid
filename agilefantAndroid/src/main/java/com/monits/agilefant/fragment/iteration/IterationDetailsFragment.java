package com.monits.agilefant.fragment.iteration;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.util.DateUtils;

public class IterationDetailsFragment extends BaseDetailTabFragment {

	private static final String PARAMS_ITERATION = "iteration";

	private Iteration iteration;

	/**
	 * Creates a new IterationFragment with the given iteration
	 * @param iteration The iteration
	 * @return a new IterationFragment with the given iteration
	 */
	public static IterationDetailsFragment newInstance(final Iteration iteration) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(PARAMS_ITERATION, iteration);

		final IterationDetailsFragment fragment = new IterationDetailsFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_iteration_details, container, false);

		if (iteration != null) {

			final TextView startDate = (TextView) rootView.findViewById(R.id.iteration_start_date);
			final TextView endDate = (TextView) rootView.findViewById(R.id.iteration_end_date);
			final TextView product = (TextView) rootView.findViewById(R.id.product);
			final TextView project = (TextView) rootView.findViewById(R.id.project);
			final TextView iterationNameTree = (TextView) rootView.findViewById(R.id.iteration_name_tree);

			final Backlog parent = iteration.getParent();
			if (parent == null) {
				rootView.findViewById(R.id.path_layout).setVisibility(View.GONE);
			} else {
				rootView.findViewById(R.id.path_layout).setVisibility(View.VISIBLE);

				product.setText(iteration.getRootIteration().getName());
				project.setText(parent.getName());
				iterationNameTree.setText(iteration.getName());

				project.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						final Intent intent = new Intent(getActivity(), ProjectActivity.class);
						intent.putExtra(ProjectActivity.EXTRA_BACKLOG, parent);
						startActivity(intent);
					}
				});
			}

			startDate.setText(DateUtils.formatDate(iteration.getStartDate(), DateUtils.DATE_PATTERN));
			endDate.setText(DateUtils.formatDate(iteration.getEndDate(), DateUtils.DATE_PATTERN));
		}

		return rootView;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle arguments = getArguments();
		iteration = (Iteration) arguments.getSerializable(PARAMS_ITERATION);
	}

	@Override
	public int getTitleBackgroundResourceId() {
		// TODO: Create its own background
		return R.drawable.gradient_burndown_title;
	}

	@Override
	public int getColorResourceId() {
		return android.R.color.white;
	}

	@Override
	public int getTitleResourceId() {
		return R.string.iteration_details;
	}

	@Override
	public String toString() {
		return "IterationDetailsFragment [iteration: " + iteration + ']';
	}
}
