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
	private ExpandableListView topExpList;
	private ProductExpandableListView listViewCache[];

	private static final int MAX_CHILDREN = 1024;

	public BacklogsAdapter(Context context,ExpandableListView topExpList, List<Product> productList) {
		this.context = context;
		this.productList = productList;
		this.topExpList = topExpList;
		this.listViewCache = new ProductExpandableListView[productList.size()];
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RoboGuice.injectMembers(context, this);
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
			ProductExpandableListView delv = new ProductExpandableListView(context, 25);
			delv.setRows(calculateRowCount(groupPosition, null));
			delv.setAdapter(new ProjectAdapter(context, productList.get(groupPosition).getProjectList()));
			delv.setIndicatorBounds(View.INVISIBLE, View.INVISIBLE);
			delv.setOnGroupClickListener(new Level2GroupExpandListener(groupPosition));

			delv.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupLevel2Position, int childPosition, long id) {
					Iteration iteration = productList.get(groupPosition).getProjectList().get(groupLevel2Position).getIterationList().get(childPosition);
					getIteration.configure(iteration.getId());
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
		return productList.get(position);
	}

	@Override
	public int getGroupCount() {
		return productList.size();
	}

	@Override
	public long getGroupId(int position) {
		return productList.get(position).getId();
	}

	@Override
	public View getGroupView(int position, boolean a, View convertView, ViewGroup parent) {
		Holder holder;
		if (null == convertView) {
			holder = new Holder();
			View inflate = inflater.inflate(R.layout.product_item, null);
			holder.title = (TextView) inflate.findViewById(R.id.title);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		Product product = (Product) getGroup(position);

		holder.title.setText(product.getTitle());

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
	}

	private int calculateRowCount(int level1, ExpandableListView level2view) {
		int level2GroupCount = productList.get(level1).getProjectList().size();
		int rowCtr = 0;
		for (int i = 0; i < level2GroupCount; i++) {
			rowCtr++;	   // for the group row
			if (level2view != null && level2view.isGroupExpanded(i)) {
				rowCtr += productList.get(level1).getProjectList().get(i).getIterationList().size();	// then add the children too
			}
		}
		return rowCtr;
	}

	class Level2GroupExpandListener implements ExpandableListView.OnGroupClickListener {
		private int level1GroupPosition;

		public Level2GroupExpandListener(int level1GroupPosition) {
			this.level1GroupPosition = level1GroupPosition;
		}

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			if (parent.isGroupExpanded(groupPosition)) {
				parent.collapseGroup(groupPosition);
			} else {
				parent.expandGroup( groupPosition );
			}

			if (parent instanceof ProductExpandableListView) {
				ProductExpandableListView dev = (ProductExpandableListView) parent;
				dev.setRows(calculateRowCount(level1GroupPosition, parent));
			}

			topExpList.requestLayout();

			return true;
		}
	}
}
