package com.monits.agilefant.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.util.ValidationUtils;

@ContentView(R.layout.activity_home)
public class HomeActivity extends RoboActivity {

	@InjectView(R.id.domain)
	private EditText domain;

	@InjectView(R.id.user_name)
	private EditText userName;

	@InjectView(R.id.password)
	private EditText password;

	@InjectView(R.id.login)
	private Button login;

	@Inject
	private UserService userService;

	@Inject
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
					userService.login(
							domainStr.trim(),
							usernameStr,
							passwordStr,
							new Listener<User>() {

								@Override
								public void onResponse(final User arg0) {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}

									final Intent intent = new Intent(HomeActivity.this, AllBackLogsActivity.class);
									HomeActivity.this.startActivity(intent);
								}
							},
							new ErrorListener() {

								@Override
								public void onErrorResponse(final VolleyError arg0) {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}

									Toast.makeText(HomeActivity.this, getResources().getString(R.string.login_error), Toast.LENGTH_LONG).show();
								}
							});

				}
			}
		});
	}

}