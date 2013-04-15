package com.monits.agilefant.activity;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.BacklogsAdapter;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.task.LoginAsyncTask;

@ContentView(R.layout.activity_all_backlogs)
public class AllBackLogsActivity extends BaseActivity {

	@InjectView(R.id.all_backlogs)
	private ExpandableListView allbackLogs;

	private List<Product> productList;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		productList = (List<Product>) extras.getSerializable(LoginAsyncTask.ALL_BACKLOGS);

		if (productList != null) {
			allbackLogs.setAdapter(new BacklogsAdapter(this, allbackLogs, productList));
		}
	}
}
