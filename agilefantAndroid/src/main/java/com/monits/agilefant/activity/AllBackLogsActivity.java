package com.monits.agilefant.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.BacklogsPagerAdapter;
import com.monits.agilefant.fragment.backlog.AllBacklogsFragment;
import com.monits.agilefant.fragment.backlog.MyBacklogsFragment;

public class AllBackLogsActivity extends BaseToolbaredActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_all_backlogs);

		final List<Fragment> fragments = new ArrayList<>();
		fragments.add(MyBacklogsFragment.newInstance());
		fragments.add(AllBacklogsFragment.newInstance());

		final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new BacklogsPagerAdapter(this, getSupportFragmentManager(), fragments));
		viewPager.setCurrentItem(0);

		final PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
		pagerTabStrip.setBackgroundDrawable(getResources().getDrawable(R.color.all_backlogs_title_background_color));
		pagerTabStrip.setTabIndicatorColorResource(R.color.all_backlogs_title_text_color);
		pagerTabStrip.setDrawFullUnderline(true);

	}
}
