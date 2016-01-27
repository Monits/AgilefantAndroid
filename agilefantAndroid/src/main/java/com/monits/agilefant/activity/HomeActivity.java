package com.monits.agilefant.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.flurry.android.FlurryAgent;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.util.ValidationUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends Activity {

	@Bind(R.id.domain)
	/* default */ EditText domain;

	@Bind(R.id.user_name)
	/* default */ EditText userName;

	@Bind(R.id.password)
	/* default */ EditText password;

	@Bind(R.id.login)
	/* default */ Button login;

	@Inject
	/* default */ UserService userService;

	@Inject
	/* default */ SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		AgilefantApplication.getObjectGraph().inject(this);
		ButterKnife.bind(this);

		domain.setText(sharedPreferences.getString(UserService.DOMAIN_KEY, ""));
		userName.setText(sharedPreferences.getString(UserService.USER_NAME_KEY, ""));

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final String domainStr = domain.getText().toString();
				final String usernameStr = userName.getText().toString();
				final String passwordStr = password.getText().toString();

				if (ValidationUtils.isNullOrEmpty(domainStr, usernameStr, passwordStr)) {
					Toast.makeText(HomeActivity.this, "All the fields are required", Toast.LENGTH_LONG).show();
				} else {

					final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
					progressDialog.setIndeterminate(true);
					progressDialog.setCancelable(false);
					progressDialog.setMessage(HomeActivity.this.getString(R.string.loading));
					progressDialog.show();
					userService.login(domainStr.trim(), usernameStr, passwordStr,
						getSuccessListener(progressDialog), getErrorListener(progressDialog));
				}
			}
		});
	}

	private Listener<User> getSuccessListener(final ProgressDialog progressDialog) {
		return new Listener<User>() {

			@Override
			public void onResponse(final User arg0) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				startActivity(AllBackLogsActivity.getIntent(HomeActivity.this));


			}
		};
	}

	private ErrorListener getErrorListener(final ProgressDialog progressDialog) {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError arg0) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				String error = getResources().getString(R.string.login_error);
				if (arg0 instanceof NoConnectionError) {
					error = getResources().getString(R.string.connection_error);
				}

				Toast.makeText(
					HomeActivity.this, error, Toast.LENGTH_LONG).show();
			}
		};
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