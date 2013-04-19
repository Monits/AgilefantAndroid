package com.monits.agilefant.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.task.LoginAsyncTask;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RoboActivity {

	@Inject
	private UserService userService;

	@Inject
	private SharedPreferences sharedPreferences;

	@Inject
	private LoginAsyncTask loginTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!userService.isLoggedIn()) {
			startHomeActivity();
		} else {

			loginTask.configure(
					sharedPreferences.getString(UserService.DOMAIN_KEY, ""),
					sharedPreferences.getString(UserService.USER_NAME_KEY, ""),
					sharedPreferences.getString(UserService.PASSWORD_KEY, ""),
					false,
					new TaskCallback<User>() {

						@Override
						public void onSuccess(User user) {
							Intent intent = new Intent(SplashActivity.this, AllBackLogsActivity.class);
							SplashActivity.this.startActivity(intent);
						}

						@Override
						public void onError() {
							startHomeActivity();
						}
					});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (userService.isLoggedIn()) {
			loginTask.execute();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (loginTask != null) {
			loginTask.interrupt();
		}
	}

	private void startHomeActivity() {
		startActivity(new Intent(SplashActivity.this, HomeActivity.class));
	}
}
