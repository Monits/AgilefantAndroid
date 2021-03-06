package com.monits.agilefant.adapter.recyclerviewholders;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.WorkItem;
import com.monits.agilefant.service.IterationService;

import javax.inject.Inject;

public abstract class WorkItemViewHolder<T extends WorkItem> extends RecyclerView.ViewHolder {

	protected IterationService iterationService;

	/**
	 * Constructor
	 * @param itemView view's Item
	 */
	public WorkItemViewHolder(final View itemView) {
		super(itemView);
	}

	/**
	 * Method to be called when the view is bind
	 * @param item The item
	 */
	public abstract void onBindView(final T item);

	/**
	 * Set the iteration service
	 * @param iterationService The iteration service to set
	 */
	@Inject
	public void setIterationService(final IterationService iterationService) {
		this.iterationService = iterationService;
	}

	protected void getIterationDetails(final Iteration iteration, final Context context) {
		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(context.getString(R.string.loading));
		progressDialog.show();

		iterationService.getIteration(
			iteration.getId(),
			new Response.Listener<Iteration>() {
				@Override
				public void onResponse(final Iteration response) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					// Iteration response doesnt have a parent so we fill it
					response.setParent(iteration.getParent());
					context.startActivity(IterationActivity.getIntent(context, response));
				}
			},
			new Response.ErrorListener() {
				@Override
				public void onErrorResponse(final VolleyError arg0) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}

					Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
				}
			});
	}


}
