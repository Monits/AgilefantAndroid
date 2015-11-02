package com.monits.agilefant.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.flurry.android.FlurryAgent;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.service.UserService;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity {

	@Inject
	/* default */ UserService userService;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgilefantApplication.getObjectGraph().inject(this);
	}

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
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.logout))
					.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, final int id) {
							userService.logout();
							final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							finish();
						}
					})
					.setNegativeButton(getString(R.string.no), null)
					.show();

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
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, AgilefantApplication.FLURRY_API_KEY);
	}

	@Override
	protected void onStop() {
		FlurryAgent.onEndSession(this);
		super.onStop();
	}
}