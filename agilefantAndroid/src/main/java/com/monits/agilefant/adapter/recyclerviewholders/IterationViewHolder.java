package com.monits.agilefant.adapter.recyclerviewholders;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.service.IterationService;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdeira on 15/09/15.
 */
public class IterationViewHolder extends BacklogViewHolder {

	@Bind(R.id.txt_title)
	TextView title;
	final Context context;
	Backlog backlog;

	@Inject
	IterationService iterationService;

	/**
	 * Iteration View Holder
	 * @param itemView Inflate view
	 * @param context	Context
	 */
	public IterationViewHolder(final View itemView, final Context context) {
		super(itemView);
		ButterKnife.bind(this, itemView);
		AgilefantApplication.getObjectGraph().inject(this);
		this.context = context;

	}

	private Response.Listener<Iteration> getSuccessListener(final Backlog backlog,
															final ProgressDialog progressDialog) {
		return new Response.Listener<Iteration>() {

			@Override
			public void onResponse(final Iteration response) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				final Intent intent = new Intent(context, IterationActivity.class);

				// Workaround that may be patchy,
				// but it depends on the request whether it comes or not, and how to get it.
				response.setParent(backlog);

				intent.putExtra(IterationActivity.ITERATION, response);

				context.startActivity(intent);
			}
		};
	}

	private Response.ErrorListener getErrorListener(final ProgressDialog progressDialog) {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError arg0) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				Toast.makeText(context, R.string.feedback_failed_retrieve_iteration,
						Toast.LENGTH_SHORT).show();

			}
		};
	}

	@Override
	public void onBindViewHolder(final Backlog backlog) {
		this.backlog = backlog;
		title.setText(backlog.getTitle());
	}

	@Override
	public void onItemClick() {
		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(context.getString(R.string.loading));
		progressDialog.show();
		iterationService.getIteration(backlog.getId(),
				getSuccessListener(backlog.getParent(), progressDialog),
				getErrorListener(progressDialog));
	}

	@Override
	public String toString() {
		return new StringBuilder("Iteration View Holder id: ").append(backlog.getId())
				.append(", title: ").append(backlog.getName()).append(' ').append(super.toString())
				.toString();
	}
}
