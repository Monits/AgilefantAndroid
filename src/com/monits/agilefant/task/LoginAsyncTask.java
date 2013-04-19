package com.monits.agilefant.task;

import roboguice.util.RoboAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.UserService;

public class LoginAsyncTask extends RoboAsyncTask<User> {

	@Inject
	private UserService userService;

	private String domain;
	private String userName;
	private String password;

	private ProgressDialog progressDialog;
	private TaskCallback<User> callback;

	private boolean successfullyLoggedIn;
	private boolean showProgressDialog;
	private boolean isInterrupted;

	@Inject
	protected LoginAsyncTask(Context context) {
		super(context);
	}

	@Override
	protected void onPreExecute() throws Exception {
		isInterrupted = false;

		if (showProgressDialog) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(getContext().getString(R.string.loading));
			progressDialog.show();
		}

		super.onPreExecute();
	}

	@Override
	public User call() throws Exception {
		successfullyLoggedIn = userService.login(domain, userName, password);

		if (successfullyLoggedIn) {
			return userService.retrieveUser();
		}

		return null;
	}

	/**
	 * Initializes the parameters to execute task.
	 * 
	 * @param domain the domain to login.
	 * @param userName the user login name.
	 * @param password the user password.
	 * @param showProgressDialog whether this task should display a progress dialog or not.
	 * @param callback the listener to be called as callback.
	 */
	public void configure(String domain, String userName, String password,
			boolean showProgressDialog, TaskCallback<User> callback) {
		this.callback = callback;
		this.showProgressDialog = showProgressDialog;
		this.userName = userName;
		this.password = password;
		this.domain = domain;
	}

	@Override
	protected void onSuccess(User user) throws Exception {
		super.onSuccess(user);

		if (!isInterrupted) {
			if (user != null ) {
				userService.storeLoggedUser(user);

				if (callback != null) {
					callback.onSuccess(user);
				}

			} else {
				if (callback != null) {
					callback.onError();
				}
			}
		}
	}

	@Override
	protected void onException(Exception e) throws RuntimeException {
		super.onException(e);

		if (callback != null) {
			callback.onError();
		}
	}

	@Override
	protected void onFinally() throws RuntimeException {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		super.onFinally();
	}

	public void interrupt() {
		isInterrupted = true;
	}
}