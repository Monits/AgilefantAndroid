package com.monits.agilefant.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.service.AgilefantService;
import com.monits.agilefant.task.LoginAsyncTask;

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
	private AgilefantService agilefantService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CookieSyncManager.createInstance(this);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (domain.getText() == null || userName.getText() == null || password.getText() == null) {
					Toast.makeText(HomeActivity.this, "All the fields are required", Toast.LENGTH_LONG).show();
				} else {
					agilefantService.setDomain(domain.getText().toString());

					loginAsyncTask.configure(userName.getText().toString(), password.getText().toString());
					loginAsyncTask.execute();
				}
			}
		});
	}
}