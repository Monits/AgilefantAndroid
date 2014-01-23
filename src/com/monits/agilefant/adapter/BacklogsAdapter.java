package com.monits.agilefant.adapter;

import java.util.List;

import roboguice.RoboGuice;
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
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.activity.IterationActivity;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.view.ProductExpandableListView;

public class BacklogsAdapter extends BaseExpandableListAdapter{

	@Inject
	private IterationService iterationService;

	private List<Product> productList;
	private final Context context;
	private final LayoutInflater inflater;
	private ProductExpandableListView listViewCache[];

	private static final int MAX_CHILDREN = 1024;

	public BacklogsAdapter(final Context context, final List<Product> productList) {
		if (productList != null) {
			this.productList = productList;
			this.listViewCache = new ProductExpandableListView[productList.size()];
		}

		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RoboGuice.injectMembers(context, this);
	}

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
	public View getChildView(final int groupPosition, final int childPosition,
			final boolean isLastChild, final View convertView, final ViewGroup parent) {
		View v;

		if (listViewCache[groupPosition] != null) {
			v = listViewCache[groupPosition];
		} else {
			final ProjectAdapter projectAdapter = new ProjectAdapter(
					context, productList.get(groupPosition).getProjectList(), R.layout.project_item, R.layout.iteration_item);
			final ProductExpandableListView delv = new ProductExpandableListView(context);
			delv.setAdapter(projectAdapter);
			delv.setIndicatorBounds(View.INVISIBLE, View.INVISIBLE);
			delv.setGroupIndicator(null);
			delv.setDivider(null);
			delv.setChildDivider(null);

			delv.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(final AdapterView<?> adapter, final View view, final int position, final long id) {
					final int positionType = ExpandableListView.getPackedPositionType(position);

					if (positionType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
						final int groupPosition = ExpandableListView.getPackedPositionGroup(id);
						final Project project = projectAdapter.getGroup(groupPosition);

						final Intent intent = new Intent(context, ProjectActivity.class);
						intent.putExtra(ProjectActivity.EXTRA_BACKLOG, new Backlog(project));
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
					final Iteration iteration = productList.get(groupPosition).getProjectList().get(groupLevel2Position).getIterationList().get(childPosition);
					final Project project = productList.get(groupPosition).getProjectList().get(groupLevel2Position);

					final ProgressDialog progressDialog = new ProgressDialog(context);
					progressDialog.setIndeterminate(true);
					progressDialog.setCancelable(false);
					progressDialog.setMessage(context.getString(R.string.loading));
					progressDialog.show();
					iterationService.getIteration(
							iteration.getId(),
							new Listener<Iteration>() {

								@Override
								public void onResponse(final Iteration response) {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}

									final Intent intent = new Intent(context, IterationActivity.class);

									// Workaround that may be patchy, but it depends on the request whether it comes or not, and how to get it.
									final Backlog backlog = new Backlog(project);
									response.setParent(backlog);

									intent.putExtra(IterationActivity.ITERATION, response);

									context.startActivity(intent);
								}
							},
							new ErrorListener() {

								@Override
								public void onErrorResponse(final VolleyError arg0) {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}

									Toast.makeText(context, R.string.feedback_failed_retrieve_iteration, Toast.LENGTH_SHORT).show();
								}
							});

					return true;
				}
			});

			listViewCache[groupPosition] = delv;
			v = delv;
		}

		return v;
	}

	@Override
	public int getChildrenCount(final int arg0) {
		return 1;
	}

	@Override
	public Object getGroup(final int position) {
		return productList != null ? productList.get(position) : null;
	}

	@Override
	public int getGroupCount() {
		return productList != null ? productList.size() : 0;
	}

	@Override
	public long getGroupId(final int position) {
		return productList != null ? productList.get(position).getId() : -1;
	}

	@Override
	public View getGroupView(final int position, final boolean isExpanded, View convertView, final ViewGroup parent) {
		Holder holder;
		if (null == convertView) {
			holder = new Holder();
			final View inflate = inflater.inflate(R.layout.product_item, null);
			holder.title = (TextView) inflate.findViewById(R.id.title);
			holder.icon = (TextView) inflate.findViewById(R.id.icon);

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

	class Holder {
		public TextView title;
		public TextView icon;
	}

	public void setBacklogs(final List<Product> productList) {
		this.productList = productList;
		this.listViewCache = new ProductExpandableListView[productList.size()];
		notifyDataSetChanged();
	}
}
