package com.monits.agilefant.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.task.LoginAsyncTask;
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
	private LoginAsyncTask loginAsyncTask;

	@Inject
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		domain.setText(sharedPreferences.getString(UserService.DOMAIN_KEY, ""));
		userName.setText(sharedPreferences.getString(UserService.USER_NAME_KEY, ""));

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String domainStr = domain.getText().toString();
				String usernameStr = userName.getText().toString();
				String passwordStr = password.getText().toString();

				if (ValidationUtils.isNullOrEmpty(domainStr, usernameStr, passwordStr)) {
					Toast.makeText(HomeActivity.this, "All the fields are required", Toast.LENGTH_LONG).show();
				} else {

					loginAsyncTask.configure(
							domainStr.trim(),
							usernameStr,
							passwordStr,
							true,
							new TaskCallback<User>() {

								@Override
								public void onSuccess(User user) {
									Intent intent = new Intent(HomeActivity.this, AllBackLogsActivity.class);
									HomeActivity.this.startActivity(intent);
								}

								@Override
								public void onError() {
									Toast.makeText(HomeActivity.this, getResources().getString(R.string.login_error), Toast.LENGTH_LONG).show();
								}
							});

					loginAsyncTask.execute();
				}
			}
		});
	}
}