package com.monits.agilefant.activity;

import roboguice.inject.ContentView;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.iteration.IterationFragment;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.task.GetIteration;

@ContentView(R.layout.activity_iteration)
public class IterationActivity extends BaseActivity {

	private Iteration iteration;
	private String projectName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			return;
		}

		Bundle bundle = getIntent().getExtras();
		iteration = (Iteration)bundle.getSerializable(GetIteration.ITERATION);
		projectName = bundle.getString(GetIteration.PROJECTNAME);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, IterationFragment.newInstance(projectName, iteration));
		transaction.commit();
	}

}