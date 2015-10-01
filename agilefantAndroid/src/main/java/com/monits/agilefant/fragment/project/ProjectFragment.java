package com.monits.agilefant.fragment.project;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.ProjectService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ProjectFragment extends Fragment {

	private static final String BACKLOG = "backlog";

	@Inject
	ProjectService projectService;

	private Backlog backlog;

	/**
	 * Return a new instance of ProjectFragment
	 * @param backlog the backlog
	 * @return a new ProjectFragment object
	 */
	public static ProjectFragment newInstance(final Backlog backlog) {
		final Bundle bundle = new Bundle();
		bundle.putSerializable(BACKLOG, backlog);

		final ProjectFragment fragment = new ProjectFragment();
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		final Bundle arguments = getArguments();
		backlog = (Backlog) arguments.getSerializable(BACKLOG);
		AgilefantApplication.getObjectGraph().inject(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_iteration, container, false);

		final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);

		if (backlog != null) {

			// FIXME: We should avoid re-do the request each time we create this view.
			projectService.getProjectData(
				backlog.getId(),
				new Response.Listener<Project>() {

					@Override
					public void onResponse(final Project project) {
						final List<Fragment> fragments = new ArrayList<>();

						fragments.add(ProjectDetailsFragment.newInstance(project));
						fragments.add(ProjectLeafStoriesFragment.newInstance(project));

						viewPager.setAdapter(
								new ScreenSlidePagerAdapter(getActivity(), getChildFragmentManager(), fragments));

						final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.pager_header);
						tabLayout.setupWithViewPager(viewPager);
					}
				},
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(
							getActivity(), R.string.failed_to_retrieve_project_details, Toast.LENGTH_SHORT).show();
					}
				});
		}

		return rootView;
	}

	@Override
	public String toString() {
		return "ProjectFragment [backlog: " + backlog + ']';
	}
}
