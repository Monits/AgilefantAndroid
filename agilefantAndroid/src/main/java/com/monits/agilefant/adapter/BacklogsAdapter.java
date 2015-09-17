package com.monits.agilefant.adapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.view.ProductExpandableListView;

import javax.inject.Inject;

public class BacklogsAdapter extends BaseExpandableListAdapter {

	@Inject
	IterationService iterationService;

	private List<Product> productList;
	private final Context context;
	private final LayoutInflater inflater;
	private ProductExpandableListView[] listViewCache;

	private static final int MAX_CHILDREN = 1024;

	/**
	 * Constructor
	 * @param context     The context
	 * @param productList The product list
	 */
	public BacklogsAdapter(final Context context, final List<Product> productList) {
		if (productList != null) {
			this.productList = productList;
			this.listViewCache = new ProductExpandableListView[productList.size()];
		}
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	/**
	 * Constructor
	 * @param context The context
	 */
	public BacklogsAdapter(final Context context) {
		this(context, null);
	}

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
		return productList.get(groupPosition).getProjectList().get(childPosition);
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return (groupPosition * MAX_CHILDREN + childPosition);
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild,
			final View convertView, final ViewGroup parent) {
		View v;

		if (listViewCache[groupPosition] == null) {
			final ProjectAdapter projectAdapter = new ProjectAdapter(context,
				productList.get(groupPosition).getProjectList(), R.layout.project_item, R.layout.iteration_item);
			final ProductExpandableListView delv = new ProductExpandableListView(context);
			delv.setAdapter(projectAdapter);
			delv.setIndicatorBounds(View.INVISIBLE, View.INVISIBLE);
			delv.setGroupIndicator(null);
			delv.setDivider(null);
			delv.setChildDivider(null);
			delv.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(final AdapterView<?> adapter, final View view, final int position,
						final long id) {
					final long packedPosition = delv.getExpandableListPosition(position);
					final int positionType = ExpandableListView.getPackedPositionType(packedPosition);

					if (positionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
						final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);

						final Project project = projectAdapter.getGroup(groupPosition);

						final Intent intent = new Intent(context, ProjectActivity.class);
						intent.putExtra(ProjectActivity.EXTRA_BACKLOG, project);
						context.startActivity(intent);

						return true;
					}

					return false;
				}
			});

			delv.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(final ExpandableListView parent, final View v,
						final int groupLevel2Position, final int childPosition, final long id) {
					final Iteration iteration = productList.get(groupPosition).getProjectList().get(groupLevel2Position)
							.getIterationList().get(childPosition);
					final Project project = productList.get(groupPosition).getProjectList().get(groupLevel2Position);

					final ProgressDialog progressDialog = new ProgressDialog(context);
					progressDialog.setIndeterminate(true);
					progressDialog.setCancelable(false);
					progressDialog.setMessage(context.getString(R.string.loading));
					progressDialog.show();
					iterationService.getIteration(iteration.getId(), getSuccessListener(project, progressDialog),
						getErrorListener(progressDialog));

					return true;
				}
			});

			listViewCache[groupPosition] = delv;
			v = delv;
		} else {
			v = listViewCache[groupPosition];
		}

		return v;
	}

	private Listener<Iteration> getSuccessListener(final Project project, final ProgressDialog progressDialog) {
		return new Listener<Iteration>() {

			@Override
			public void onResponse(final Iteration response) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				final Intent intent = new Intent(context, IterationActivity.class);

				// Workaround that may be patchy,
				// but it depends on the request whether it comes or not, and how to get it.
				response.setParent(project);

				intent.putExtra(IterationActivity.ITERATION, response);

				context.startActivity(intent);
			}
		};
	}

	private ErrorListener getErrorListener(final ProgressDialog progressDialog) {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError arg0) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
			}
		};
	}

	@Override
	public int getChildrenCount(final int arg0) {
		return 1;
	}

	@Override
	public Object getGroup(final int position) {
		return productList == null ? null : productList.get(position);
	}

	@Override
	public int getGroupCount() {
		return productList == null ? 0 : productList.size();
	}

	@Override
	public long getGroupId(final int position) {
		return productList == null ? -1 : productList.get(position).getId();
	}

	@SuppressWarnings("checkstyle:finalparameters")
	@Override
	public View getGroupView(final int position, final boolean isExpanded, View convertView, final ViewGroup parent) {
		final Holder holder;
		if (null == convertView) {
			final View inflate = inflater.inflate(R.layout.product_item, null);
			holder = new Holder(inflate);
			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final Product product = (Product) getGroup(position);

		holder.title.setText(product.getTitle());

		if (isExpanded) {
			holder.icon.setText("-");
		} else {
			holder.icon.setText("+");
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(final int arg0, final int arg1) {
		return true;
	}

	static class Holder {
		@Bind(R.id.txt_title)
		TextView title;
		@Bind(R.id.txt_icon)
		TextView icon;

		public Holder(final View view) {
			ButterKnife.bind(this, view);
		}
	}

	/**
	 * Set the backlogs
	 * @param productList The backlogs to set
	 */
	public void setBacklogs(final List<Product> productList) {
		this.productList = productList;
		this.listViewCache = new ProductExpandableListView[productList.size()];
		notifyDataSetChanged();
	}
}
