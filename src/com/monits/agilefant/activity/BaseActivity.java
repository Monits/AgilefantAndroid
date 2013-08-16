package com.monits.agilefant.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.util.AQUtility;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.service.UserService;

public class BaseActivity extends RoboSherlockFragmentActivity {

	@Inject
	private UserService userService;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this, AllBackLogsActivity.class));
			return true;
		case R.id.actionbar_logout:
			userService.logout();
			Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

			return true;

		case R.id.actionbar_dailywork:
			Intent toDailyWorkIntent = new Intent(this, DailyWorkActivity.class);
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