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

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.task.LoginAsyncTask;

@ContentView(R.layout.activity_home)
public class HomeActivity extends RoboActivity {

	@InjectView(R.id.user_name)
	private EditText userName;

	@InjectView(R.id.password)
	private EditText password;

	@InjectView(R.id.login)
	private Button login;

	@Inject
	private LoginAsyncTask loginAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CookieSyncManager.createInstance(this);
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginAsyncTask.configure(userName.getText().toString(), password.getText().toString());
				loginAsyncTask.execute();
			}
		});
	}
}