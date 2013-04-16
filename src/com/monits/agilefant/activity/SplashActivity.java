package com.monits.agilefant.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.util.RoboAsyncTask;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.task.LoginAsyncTask;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RoboActivity {

	@Inject
	private UserService userService;

	@Inject
	private SharedPreferences sharedPreferences;

	@Inject
	private BacklogService backlogService;

	private LoginTask loginTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!userService.isLoggedIn()) {
			startHomeActivity();
		} else {
			loginTask = new LoginTask(this);
			loginTask.execute();
		}
	}

	@Override
	protected void onPause() {
		if (loginTask != null) {
			loginTask.cancel(true);
		}
		super.onPause();
	}

	private void startHomeActivity() {
		startActivity(new Intent(SplashActivity.this, HomeActivity.class));
	}

	class LoginTask extends RoboAsyncTask<List<Product>> {

		private boolean successfullyLoggedIn;
		private boolean isInterrupted;
		private List<Product> backlogs;

		protected LoginTask(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() throws Exception {
			isInterrupted = false;
			super.onPreExecute();
		}

		@Override
		public List<Product> call() throws Exception {
			successfullyLoggedIn = userService.login(
					sharedPreferences.getString(UserService.DOMAIN_KEY, ""),
					sharedPreferences.getString(UserService.USER_NAME_KEY, ""),
					sharedPreferences.getString(UserService.PASSWORD_KEY, ""));

			if (successfullyLoggedIn) {
				return backlogService.getAllBacklogs();
			}

			return null;
		}

		@Override
		protected void onSuccess(List<Product> backlogs) throws Exception {
			this.backlogs = backlogs;
			super.onSuccess(backlogs);
		}

		@Override
		protected void onException(Exception e) throws RuntimeException {
			super.onException(e);
			Log.e(this.getClass().getName(), "Failed to retrieve backlogs", e);
		}

		@Override
		protected void onInterrupted(Exception e) {
			isInterrupted = true;
			super.onInterrupted(e);
		}
		@Override
		protected void onFinally() throws RuntimeException {
			if (!isInterrupted) {
				if (successfullyLoggedIn) {
					Intent intent = new Intent(SplashActivity.this, AllBackLogsActivity.class);
					intent.putExtra(LoginAsyncTask.ALL_BACKLOGS, new ArrayList<Product>(backlogs));
					startActivity(intent);
				} else {
					startHomeActivity();
				}
			}

			super.onFinally();
		}
	}
}
