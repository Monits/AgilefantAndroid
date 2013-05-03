package com.monits.agilefant.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.widget.BaseExpandableListAdapter;

/**
 * Abstract Expandable List Adapter
 * @author Ivan Corbalan
 *
 * @param <Tgroup> Group
 * @param <Tchildren> Children
 */
public abstract class AbstractExpandableListAdapter<Tgroup, Tchildren> extends BaseExpandableListAdapter{

	protected Context context;

	protected ArrayList<Tgroup> groups;

	protected ArrayList<ArrayList<Tchildren>> children;

	/**
	 * Constructor
	 * @param context Context
	 */
	public AbstractExpandableListAdapter(Context context) {
		super();
		this.context = context;
		this.groups = new ArrayList<Tgroup>();
		this.children = new ArrayList<ArrayList<Tchildren>>();

	}

	/**
	 * Constructor
	 * @param context Context
	 * @param groups Groups
	 * @param children Children
	 */
	public AbstractExpandableListAdapter(Context context,
			ArrayList<Tgroup> groups, ArrayList<ArrayList<Tchildren>> children) {
		super();
		this.context = context;
		this.groups = groups;
		this.children = children;
	}

	/**
	 * Remove item from the list
	 * @param class
	 */
	public void removeItem(Tchildren object) {
		for (ArrayList<Tchildren> group : children) {
			if (group.contains(object)) {
				int index = group.indexOf(object);
				group.remove(index);
			}
		}
	}

	@Override
	public Tchildren getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return children != null ? children.get(groupPosition).size() : 0;
	}

	@Override
	public Tgroup getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups != null ? groups.size() : 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}


	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}

	protected void addGroup(Tgroup group) {
		this.groups.add(group);
		this.children.add(new ArrayList<Tchildren>());
	}

	protected void addChildToGroup(Tgroup group, Tchildren child) {
		if (!groups.contains(group)) {
			groups.add(group);
		}

		int index = groups.indexOf(group);
		if (children.size() < index + 1) {
			children.add(new ArrayList<Tchildren>());
		}
		children.get(index).add(child);


	}
}
