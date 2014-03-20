package com.monits.agilefant.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.flurry.android.FlurryAgent;
import com.google.inject.Inject;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.UserService;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RoboActivity {

	@Inject
	private UserService userService;

	@Inject
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!userService.isLoggedIn()) {
			startHomeActivity();
		} else {

			userService.login(
					sharedPreferences.getString(UserService.DOMAIN_KEY, ""),
					sharedPreferences.getString(UserService.USER_NAME_KEY, ""),
					sharedPreferences.getString(UserService.PASSWORD_KEY, ""),
					new Listener<User>() {

						@Override
						public void onResponse(final User arg0) {
							final Intent intent = new Intent(SplashActivity.this, AllBackLogsActivity.class);
							SplashActivity.this.startActivity(intent);
						}
					},
					new ErrorListener() {

						@Override
						public void onErrorResponse(final VolleyError arg0) {
							startHomeActivity();
						}
					});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, AgilefantApplication.FLURRY_API_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	private void startHomeActivity() {
		startActivity(new Intent(SplashActivity.this, HomeActivity.class));
	}
}
