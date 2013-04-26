package com.monits.agilefant.activity;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.BacklogsAdapter;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.task.GetBacklogsTask;

@ContentView(R.layout.activity_all_backlogs)
public class AllBackLogsActivity extends BaseActivity {

	@InjectView(R.id.all_backlogs)
	private ExpandableListView allbackLogs;

	@Inject
	private GetBacklogsTask getBacklogsTask;

	private BacklogsAdapter backlogsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		backlogsAdapter = new BacklogsAdapter(this);
		allbackLogs.setAdapter(backlogsAdapter);

		getBacklogsTask.configure(new TaskCallback<List<Product>>() {

			@Override
			public void onSuccess(List<Product> response) {
				if (response != null) {
					backlogsAdapter.setBacklogs(response);
				}
			}

			@Override
			public void onError() {
				Toast.makeText(AllBackLogsActivity.this, R.string.error_retrieve_backlogs, Toast.LENGTH_SHORT).show();
			}
		});

		getBacklogsTask.execute();

	}
}
