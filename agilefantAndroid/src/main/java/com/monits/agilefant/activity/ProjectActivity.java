package com.monits.agilefant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.project.ProjectDetailsFragment;
import com.monits.agilefant.fragment.project.ProjectLeafStoriesFragment;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.backlog.BacklogType;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectActivity extends BaseToolbaredActivity implements ViewPager.OnPageChangeListener {

	private static final String EXTRA_BACKLOG = "com.monits.agilefant.intent.extra.PROJECT";
	private static final String PROJECT = "project";

	@Bind(R.id.pager)
	/* default */ ViewPager viewPager;

	@Bind(R.id.pager_header)
	/* default */ TabLayout tabLayout;

	/**
	 * This factory method returns an intent of this class with it's necessary extra values
	 *
	 * @param context A Context of the application package implementing this class
	 * @param backlog A Backlog object for being sent to the returned intent
	 * @param project A Project object for being sent to the returned intent
	 * @return An intent that contains sent data as extra values
	 */
	public static Intent newIntentInstance(@NonNull final Context context, @NonNull final Backlog backlog,
										@NonNull final Project project) {
		final Intent intent = new Intent(context, ProjectActivity.class);
		intent.putExtra(EXTRA_BACKLOG, backlog);
		intent.putExtra(PROJECT, project);

		return intent;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iteration);
		ButterKnife.bind(this);

		final Backlog backlog = (Backlog) getIntent().getSerializableExtra(EXTRA_BACKLOG);
		final Project project = (Project) getIntent().getSerializableExtra(PROJECT);

		if (backlog == null || project == null) {
			// We didn't come through a proper intent, probably an old-version up navigation when this
			// intent did not exist in the backstack
			finish();
			return;
		}

		final ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		final View fabContainer = getLayoutInflater().inflate(R.layout.fab_iteration_menu_layout, content);
		initFABs(fabContainer, project.getId());
		populatePager(project);
	}

	private void populatePager(final Project project) {
		final List<Fragment> fragments = new ArrayList<>();
		fragments.add(ProjectDetailsFragment.newInstance(project));
		fragments.add(ProjectLeafStoriesFragment.newInstance(project));
		viewPager.setAdapter(new ScreenSlidePagerAdapter(this, getSupportFragmentManager(), fragments));
		setUpTabLayout(viewPager);
	}

	private void initFABs(final View fabContainer, final Long projectId) {
		final FloatingActionButton addFAB = (FloatingActionButton) fabContainer.findViewById(R.id.iteration_add_fab);
		addFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				final CreateStoryFragment createStoryFragment = CreateStoryFragment
						.newInstance(BacklogType.PROJECT, projectId);

				getSupportFragmentManager().beginTransaction()
						.replace(android.R.id.content, createStoryFragment)
						.addToBackStack(null)
						.commit();
			}
		});
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		final MenuItem menuItem = menu.findItem(R.id.action_search);
		menuItem.setVisible(false);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
		searchView.setMaxWidth(getMaxWidthScreen());

		return true;
	}

	@Override
	public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
		// Nothing to do here
	}

	@Override
	public void onPageSelected(final int position) {
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onPageScrollStateChanged(final int state) {
		// Nothing to do here
	}
}
