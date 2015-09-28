/**
 *
 */
package com.monits.agilefant.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.monits.agilefant.fragment.backlog.task.CreateDailyWorkTaskFragment;
import com.monits.agilefant.model.DailyWork;
import com.monits.agilefant.service.DailyWorkService;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author gmuniz
 *
 */
public class DailyWorkActivity extends BaseToolbaredActivity {

	private final static String DAILYWORK = "dailywork";

	private DailyWork dailyWork;

	@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL",
			justification = "The field is necessary for dependency injection")
	@Inject
	DailyWorkService dailyWorkService;

	@Bind(R.id.pager)
	ViewPager viewPager;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.menu_dailywork, menu);
		return true;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_work);

		ButterKnife.bind(this);

		AgilefantApplication.getObjectGraph().inject(this);


		final PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
		pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.color.all_backlogs_title_background_color));
		pagerTabStrip.setTabIndicatorColorResource(R.color.all_backlogs_title_text_color);
		pagerTabStrip.setDrawFullUnderline(true);
		viewPager.setVisibility(View.VISIBLE);

		if (savedInstanceState == null) {

			getDailyWork();

		} else {

			dailyWork = (DailyWork) savedInstanceState.getSerializable(DAILYWORK);

			viewPager.setAdapter(
					new DailyWorkPagerAdapter(getSupportFragmentManager(), dailyWork, DailyWorkActivity.this));
		}

		initializeFab();


	}

	private void getDailyWork() {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.show();

		dailyWorkService.getDailyWork(
				new Listener<DailyWork>() {

					@Override
					public void onResponse(final DailyWork response) {
						viewPager.setCurrentItem(0);

						dailyWork = response;
						viewPager.setAdapter(new DailyWorkPagerAdapter(getSupportFragmentManager(), response,
								DailyWorkActivity.this));

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
								DailyWorkActivity.this, R.string.feedback_failed_to_retrieve_daily_work,
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void initializeFab() {
		final FloatingActionButton addTaskWithOutStoryFAB =
				(FloatingActionButton) findViewById(R.id.daily_work_add_fab);
		addTaskWithOutStoryFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				getSupportFragmentManager().beginTransaction()
						.replace(android.R.id.content, CreateDailyWorkTaskFragment.newInstance())
						.addToBackStack(null)
						.commit();
			}
		});
	}


	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(DAILYWORK, dailyWork);
	}
}