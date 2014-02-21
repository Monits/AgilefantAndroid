package com.monits.agilefant.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseExpandableListAdapter;

/**
 * Abstract Expandable List Adapter
 * @author Ivan Corbalan
 *
 * @param <Tgroup> Group
 * @param <Tchildren> Children
 */
public abstract class AbstractExpandableListAdapter<Tgroup, Tchildren> extends BaseExpandableListAdapter {

	protected Context context;

	protected List<Tgroup> groups;

	protected List<List<Tchildren>> children;

	/**
	 * Constructor
	 * @param context Context
	 */
	public AbstractExpandableListAdapter(final Context context) {
		super();
		this.context = context;
		this.groups = new ArrayList<Tgroup>();
		this.children = new ArrayList<List<Tchildren>>();

	}

	/**
	 * Constructor
	 * @param context Context
	 * @param groups Groups
	 * @param children Children
	 */
	public AbstractExpandableListAdapter(final Context context,
			final List<Tgroup> groups, final List<List<Tchildren>> children) {
		super();
		this.context = context;
		this.groups = groups;
		this.children = children;
	}

	/**
	 * Remove item from the list
	 * @param class
	 */
	public void removeItem(final Tchildren object) {
		for (final List<Tchildren> group : children) {
			if (group.contains(object)) {
				final int index = group.indexOf(object);
				group.remove(index);
			}
		}
	}

	@Override
	public Tchildren getChild(final int groupPosition, final int childPosition) {
		final List<Tchildren> groupChildren = children.get(groupPosition);
		return groupChildren != null
				&& groupChildren.size() > 0 && childPosition >= 0 && childPosition < groupChildren.size() ?
						groupChildren.get(childPosition) : null;
	}

	@Override
	public abstract long getChildId(final int groupPosition, final int childPosition);

	@Override
	public int getChildrenCount(final int groupPosition) {
		return children != null ? children.get(groupPosition).size() : 0;
	}

	@Override
	public Tgroup getGroup(final int groupPosition) {
		final int groupCount = getGroupCount();
		return groupCount > 0 && groupPosition >= 0 && groupPosition < groupCount ?
				groups.get(groupPosition) : null;
	}

	@Override
	public int getGroupCount() {
		return groups != null ? groups.size() : 0;
	}

	@Override
	public abstract long getGroupId(final int groupPosition);


	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(final int arg0, final int arg1) {
		return true;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	protected void addGroup(final Tgroup group) {
		this.groups.add(group);
		this.children.add(new ArrayList<Tchildren>());
	}

	protected void addChildToGroup(final Tgroup group, final Tchildren child) {
		if (!groups.contains(group)) {
			groups.add(group);
		}

		final int index = groups.indexOf(group);
		if (children.size() < index + 1) {
			children.add(new ArrayList<Tchildren>());
		}

		children.get(index).add(child);
	}
}
