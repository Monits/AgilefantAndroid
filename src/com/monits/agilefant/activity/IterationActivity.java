package com.monits.agilefant.activity;

import roboguice.inject.ContentView;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.iteration.IterationFragment;
import com.monits.agilefant.model.Iteration;

@ContentView(R.layout.activity_iteration)
public class IterationActivity extends BaseActivity {

	public static final String PROJECTNAME = "PROJECTNAME";
	public static final String ITERATION = "ITERATION";

	private Iteration iteration;
	private String projectName;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			return;
		}

		final Bundle bundle = getIntent().getExtras();
		iteration = (Iteration) bundle.getSerializable(ITERATION);
		projectName = bundle.getString(PROJECTNAME);

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, IterationFragment.newInstance(projectName, iteration));
		transaction.commit();
	}

}