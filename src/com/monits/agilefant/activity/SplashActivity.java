package com.monits.agilefant.activity;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.task.LoginAsyncTask;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RoboActivity{

	@Inject
	private UserService userService;

	@Inject
	private SharedPreferences sharedPreferences;

	@Inject
	private BacklogService backlogService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!userService.isLoggedIn()) {
			startHomeActivity();
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						boolean isLoggedIn = userService.login(sharedPreferences.getString(UserService.DOMAIN_KEY, ""), sharedPreferences.getString(UserService.USER_NAME_KEY, ""), sharedPreferences.getString(UserService.PASSWORD_KEY, ""));

						if (isLoggedIn) {
							List<Product> allBacklogs = backlogService.getAllBacklogs();
							Intent intent = new Intent(SplashActivity.this, AllBackLogsActivity.class);
							intent.putExtra(LoginAsyncTask.ALL_BACKLOGS, new ArrayList<Product>(allBacklogs));
							startActivity(intent);
						} else {
							startHomeActivity();
						}


					} catch (RequestException e) {
						startHomeActivity();
					}
				}
			}).start();
		}

	}

	private void startHomeActivity() {
		startActivity(new Intent(SplashActivity.this, HomeActivity.class));
	}
}
