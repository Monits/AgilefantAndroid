/**
 *
 */
package com.monits.agilefant.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.DailyWorkPagerAdapter;
import com.monits.agilefant.fragment.dailywork.MyQueueWorkFragment;
import com.monits.agilefant.fragment.dailywork.MyStoriesFragment;
import com.monits.agilefant.fragment.dailywork.MyTasksFragment;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.service.DailyWorkService;

import javax.inject.Inject;

/**
 * @author gmuniz
 *
 */
public class DailyWorkActivity extends BaseToolbaredActivity {

	@Inject
	DailyWorkService dailyWorkService;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.menu_dailywork, menu);
		return true;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AgilefantApplication.getObjectGraph().inject(this);

		setContentView(R.layout.activity_daily_work);

		final PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
		pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.color.all_backlogs_title_background_color));
		pagerTabStrip.setTabIndicatorColorResource(R.color.all_backlogs_title_text_color);
		pagerTabStrip.setDrawFullUnderline(true);

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.show();

		final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		dailyWorkService.getDailyWork(
			new Listener<DailyWork>() {

				@Override
				public void onResponse(final DailyWork response) {
					viewPager.setCurrentItem(0);
					viewPager.setVisibility(View.VISIBLE);

					final List<Fragment> fragments = new ArrayList<Fragment>();
					fragments.add(MyQueueWorkFragment.newInstance(response.getQueuedTasks()));
					fragments.add(MyStoriesFragment.newInstance(response.getStories()));
					fragments.add(MyTasksFragment.newInstance(response.getTaskWithoutStories()));

					viewPager.setAdapter(
							new DailyWorkPagerAdapter(DailyWorkActivity.this, getSupportFragmentManager(), fragments));

					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
				}
			},
			new ErrorListener() {

				@Override
				public void onErrorResponse(final VolleyError arg0) {
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}

					Toast.makeText(
						DailyWorkActivity.this, R.string.feedback_failed_to_retrieve_daily_work, Toast.LENGTH_SHORT)
							.show();
				}
			});
	}

}
