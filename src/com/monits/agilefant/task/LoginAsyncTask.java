package com.monits.agilefant.task;

import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;
import com.monits.agilefant.activity.AllBackLogsActivity;
import com.monits.agilefant.connector.HttpConnection;
import com.monits.agilefant.exception.RequestException;
import com.monits.agilefant.service.AgilefantService;
import com.monits.agilefant.service.UserService;

public class LoginAsyncTask extends RoboAsyncTask<String>{

	@Inject
	private UserService userService;

	@Inject
	private AgilefantService agilefantService;

	private String userName;
	private String password;

	private ProgressDialog progressDialog;

	@Inject
	protected LoginAsyncTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Cargando");
		progressDialog.show();
		super.onPreExecute();
	}

	@Override
	public String call() {
		try {
			String  login = userService.login(userName, password);
			if (login.equals(HttpConnection.LOGIN)) {
				Intent intent = new Intent(this.context, AllBackLogsActivity.class);
				this.context.startActivity(intent);
			}
		} catch (RequestException e) {
			Ln.e(e);
		}
		return null;
	}

	public void configure(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	@Override
	protected void onFinally() throws RuntimeException {
		super.onFinally();
		progressDialog.dismiss();

	}

}
