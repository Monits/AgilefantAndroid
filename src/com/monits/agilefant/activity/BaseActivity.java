package com.monits.agilefant.activity;

import roboguice.activity.RoboFragmentActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.androidquery.util.AQUtility;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.service.UserService;

public class BaseActivity extends RoboFragmentActivity {

	@Inject
	private UserService userService;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actionbar_logout:
			userService.logout();
			Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		if (isTaskRoot()) {
			AQUtility.cleanCacheAsync(this);
		}
	}
}
