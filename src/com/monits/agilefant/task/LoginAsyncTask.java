package com.monits.agilefant.task;

import java.util.ArrayList;
import java.util.List;

import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.AllBackLogsActivity;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.service.BacklogService;
import com.monits.agilefant.service.UserService;

public class LoginAsyncTask extends RoboAsyncTask<String>{

	public static final String ALL_BACKLOGS = "allBackLogs";

	@Inject
	private UserService userService;

	@Inject
	private BacklogService backlogService;

	private String userName;
	private String password;

	private ProgressDialog progressDialog;

	private boolean isLoggedIn;

	private List<Product> allBacklogs;

	private String domain;

	@Inject
	protected LoginAsyncTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getContext().getString(R.string.loading));
		progressDialog.show();
		super.onPreExecute();
	}

	@Override
	public String call() {
		try {
			isLoggedIn = userService.login(domain, userName, password);

			if (isLoggedIn) {
				allBacklogs = backlogService.getAllBacklogs();
			}

		} catch (RequestException e) {
			onException(e);
		}
		return null;
	}

	public void configure(String domain, String userName, String password) {
		this.userName = userName;
		this.password = password;
		this.domain = domain;
	}

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();
		progressDialog.dismiss();
		if (isLoggedIn) {
			Intent intent = new Intent(this.context, AllBackLogsActivity.class);
			intent.putExtra(ALL_BACKLOGS,new ArrayList<Product>(allBacklogs));
			this.context.startActivity(intent);
		} else {
			Toast.makeText(context, context.getResources().getString(R.string.login_error), Toast.LENGTH_LONG).show();
		}

	}
}