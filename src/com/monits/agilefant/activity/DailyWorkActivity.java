/**
 *
 */
package com.monits.agilefant.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.DailyWorkPagerAdapter;
import com.monits.agilefant.fragment.dailywork.MyQueueWorkFragment;
import com.monits.agilefant.fragment.dailywork.MyStoriesFragment;
import com.monits.agilefant.fragment.dailywork.MyTasksFragment;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.task.GetDailyWorkTask;

/**
 * @author gmuniz
 *
 */
@ContentView(R.layout.activity_daily_work)
public class DailyWorkActivity extends BaseActivity {

	@InjectView(R.id.pager)
	private ViewPager viewPager;

	@InjectView(R.id.pager_header)
	private PagerTabStrip pagerTabStrip;

	@Inject
	private GetDailyWorkTask getDailyWorkTask;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.menu_dailywork, menu);
		return true;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.color.all_backlogs_title_background_color));
		pagerTabStrip.setTabIndicatorColorResource(R.color.all_backlogs_title_text_color);
		pagerTabStrip.setDrawFullUnderline(true);

		getDailyWorkTask.configure(new TaskCallback<DailyWork>() {

			@Override
			public void onSuccess(final DailyWork response) {
				viewPager.setCurrentItem(0);
				viewPager.setVisibility(View.VISIBLE);

				final List<Fragment> fragments = new ArrayList<Fragment>();
				fragments.add(MyQueueWorkFragment.newInstance(response.getQueuedTasks()));
				fragments.add(MyStoriesFragment.newInstance(response.getStories()));
				fragments.add(MyTasksFragment.newInstance(response.getTaskWithoutStories()));

				viewPager.setAdapter(
						new DailyWorkPagerAdapter(DailyWorkActivity.this, getSupportFragmentManager(), fragments));
			}

			@Override
			public void onError() {
				Toast.makeText(DailyWorkActivity.this, "Failed to retrieve daily work", Toast.LENGTH_SHORT).show();
			}
		});

		getDailyWorkTask.execute();
	}

}
