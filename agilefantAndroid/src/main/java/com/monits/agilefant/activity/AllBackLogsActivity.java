package com.monits.agilefant.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.BacklogsPagerAdapter;
import com.monits.agilefant.adapter.search.BacklogSearchAdapter;
import com.monits.agilefant.fragment.backlog.AllBacklogsFragment;
import com.monits.agilefant.fragment.backlog.MyBacklogsFragment;
import com.monits.agilefant.listeners.AllBacklogsSearchListener;
import com.monits.agilefant.listeners.SuggestionListener;

import java.util.ArrayList;
import java.util.List;

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

		setUpTabLayout(viewPager);

	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Adapter
		final BacklogSearchAdapter backlogSearchAdapter =
				new BacklogSearchAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		// Suggestion listener
		final SuggestionListener suggestionListener = new SuggestionListener(this);

		// SearchManager
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		// Get search action from menu and we make it visible
		final MenuItem menuItem = menu.findItem(R.id.action_search);

		menuItem.setVisible(true);

		final SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

		searchView.setSearchableInfo(searchableInfo);
		searchView.setSuggestionsAdapter(backlogSearchAdapter);
		searchView.setSubmitButtonEnabled(true);

		searchView.setOnQueryTextListener(new AllBacklogsSearchListener(this, backlogSearchAdapter,
				suggestionListener));
		searchView.setOnSuggestionListener(suggestionListener);

		return true;
	}
}
