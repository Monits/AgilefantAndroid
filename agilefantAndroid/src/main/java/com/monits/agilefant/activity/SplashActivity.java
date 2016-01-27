package com.monits.agilefant.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.flurry.android.FlurryAgent;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.UserService;

import javax.inject.Inject;

public class SplashActivity extends Activity {

	@Inject
	/* default */ UserService userService;

	@Inject
	/* default */ SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		AgilefantApplication.getObjectGraph().inject(this);

		if (userService.isLoggedIn()) {
			userService.login(
				sharedPreferences.getString(UserService.DOMAIN_KEY, ""),
				sharedPreferences.getString(UserService.USER_NAME_KEY, ""),
				sharedPreferences.getString(UserService.PASSWORD_KEY, ""),
				new Listener<User>() {

					@Override
					public void onResponse(final User arg0) {
						startActivity(AllBackLogsActivity.getIntent(SplashActivity.this));
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						startHomeActivity();
					}
				});

		} else {
			startHomeActivity();
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

	private void startHomeActivity() {
		startActivity(HomeActivity.getIntent(this));
	}
}
