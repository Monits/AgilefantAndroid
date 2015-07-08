package com.monits.agilefant.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.BacklogsPagerAdapter;
import com.monits.agilefant.fragment.backlog.AllBacklogsFragment;
import com.monits.agilefant.fragment.backlog.MyBacklogsFragment;

@ContentView(R.layout.activity_all_backlogs)
public class AllBackLogsActivity extends BaseActivity {

	@InjectView(R.id.pager)
	private ViewPager viewPager;

	@InjectView(R.id.pager_header)
	private PagerTabStrip pagerTabStrip;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final List<Fragment> fragments = new ArrayList<>();
		fragments.add(MyBacklogsFragment.newInstance());
		fragments.add(AllBacklogsFragment.newInstance());

		this.viewPager.setAdapter(
				new BacklogsPagerAdapter(this, getSupportFragmentManager(), fragments));

		pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.color.all_backlogs_title_background_color));
		pagerTabStrip.setTabIndicatorColorResource(R.color.all_backlogs_title_text_color);
		pagerTabStrip.setDrawFullUnderline(true);

	}
}
