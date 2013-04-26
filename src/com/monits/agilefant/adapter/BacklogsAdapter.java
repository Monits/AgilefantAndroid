package com.monits.agilefant.adapter;

import java.util.List;

import roboguice.RoboGuice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Product;
import com.monits.agilefant.task.GetIteration;
import com.monits.agilefant.view.ProductExpandableListView;

public class BacklogsAdapter extends BaseExpandableListAdapter{

	@Inject
	private GetIteration getIteration;

	private List<Product> productList;
	private Context context;
	private LayoutInflater inflater;
	private ProductExpandableListView listViewCache[];

	private static final int MAX_CHILDREN = 1024;

	public BacklogsAdapter(Context context, List<Product> productList) {
		if (productList != null) {
			this.productList = productList;
			this.listViewCache = new ProductExpandableListView[productList.size()];
		}

		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RoboGuice.injectMembers(context, this);
	}

	public BacklogsAdapter(Context context) {
		this(context, null);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return productList.get(groupPosition).getProjectList().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return (groupPosition * MAX_CHILDREN + childPosition);
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v;

		if (listViewCache[groupPosition] != null) {
			v = listViewCache[groupPosition];
		} else {
			ProductExpandableListView delv = new ProductExpandableListView(context);
			delv.setAdapter(new ProjectAdapter(context, productList.get(groupPosition).getProjectList()));
			delv.setIndicatorBounds(View.INVISIBLE, View.INVISIBLE);

			delv.setDivider(null);
			delv.setChildDivider(null);

			delv.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupLevel2Position, int childPosition, long id) {
					Iteration iteration = productList.get(groupPosition).getProjectList().get(groupLevel2Position).getIterationList().get(childPosition);
					String projectName = productList.get(groupPosition).getProjectList().get(groupLevel2Position).getTitle();
					getIteration.configure(projectName, iteration.getId());
					getIteration.execute();
					return true;
				}
			});

			listViewCache[groupPosition] = delv;
			v = delv;
		}

		return v;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return 1;
	}

	@Override
	public Object getGroup(int position) {
		return productList != null ? productList.get(position) : null;
	}

	@Override
	public int getGroupCount() {
		return productList != null ? productList.size() : 0;
	}

	@Override
	public long getGroupId(int position) {
		return productList != null ? productList.get(position).getId() : -1;
	}

	@Override
	public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
		Holder holder;
		if (null == convertView) {
			holder = new Holder();
			View inflate = inflater.inflate(R.layout.product_item, null);
			holder.title = (TextView) inflate.findViewById(R.id.title);
			holder.icon = (TextView) inflate.findViewById(R.id.icon);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		Product product = (Product) getGroup(position);

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
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	class Holder {
		public TextView title;
		public TextView icon;
	}

	public void setBacklogs(List<Product> productList) {
		this.productList = productList;
		this.listViewCache = new ProductExpandableListView[productList.size()];
		notifyDataSetChanged();
	}
}
