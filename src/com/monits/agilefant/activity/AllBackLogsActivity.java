package com.monits.agilefant.activity;

import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.BacklogsAdapter;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.service.UserService;
import com.monits.agilefant.task.LoginAsyncTask;

@ContentView(R.layout.activity_all_backlogs)
public class AllBackLogsActivity extends RoboActivity{

	@InjectView(R.id.all_backlogs)
	private ExpandableListView allbackLogs;

	@InjectView(R.id.logout)
	private TextView logout;

	@Inject
	private UserService userService;

	private List<Product> productList;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		productList = (List<Product>) extras.getSerializable(LoginAsyncTask.ALL_BACKLOGS);

		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userService.logout();
				Intent intent = new Intent(AllBackLogsActivity.this, HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});

		if (productList != null) {
			allbackLogs.setAdapter(new BacklogsAdapter(this, allbackLogs, productList));
		}
	}
}
