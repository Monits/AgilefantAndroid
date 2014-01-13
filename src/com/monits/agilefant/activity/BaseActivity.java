package com.monits.agilefant.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.androidquery.util.AQUtility;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.service.UserService;

public class BaseActivity extends RoboActionBarActivity {

	@Inject
	private UserService userService;

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpTo(this, new Intent(this, AllBackLogsActivity.class));
				return true;
			case R.id.actionbar_logout:
				userService.logout();
				final Intent intent = new Intent(this, HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();

				return true;

			case R.id.actionbar_dailywork:
				final Intent toDailyWorkIntent = new Intent(this, DailyWorkActivity.class);
				startActivity(toDailyWorkIntent);

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (isTaskRoot()) {
			AQUtility.cleanCacheAsync(this);
		}
	}
}