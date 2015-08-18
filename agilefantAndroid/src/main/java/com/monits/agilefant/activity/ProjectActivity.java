package com.monits.agilefant.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.monits.agilefant.R;
import com.monits.agilefant.fragment.project.ProjectFragment;
import com.monits.agilefant.model.Backlog;

public class ProjectActivity extends BaseToolbaredActivity {

	public static final String EXTRA_BACKLOG = "com.monits.agilefant.intent.extra.PROJECT";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iteration);

		final Backlog backlog = (Backlog) getIntent().getSerializableExtra(EXTRA_BACKLOG);

		final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, ProjectFragment.newInstance(backlog));
		transaction.commit();
	}
}
