package com.monits.agilefant.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseExpandableListAdapter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract Expandable List Adapter
 * @author Ivan Corbalan
 *
 * @param <G> Group
 * @param <C> Children
 */
public abstract class AbstractExpandableListAdapter<G, C> extends BaseExpandableListAdapter {

	protected Context context;

	@SuppressFBWarnings(value = "DLC_DUBIOUS_LIST_COLLECTION", justification = "We do care about the insertion order")
	protected List<G> groups;

	protected List<List<C>> children;

	/**
	 * Constructor
	 * @param context Context
	 */
	public AbstractExpandableListAdapter(final Context context) {
		super();
		this.context = context;
		this.groups = new ArrayList<>();
		this.children = new ArrayList<>();

	}

	/**
	 * Constructor
	 * @param context Context
	 * @param groups Groups
	 * @param children Children
	 */
	public AbstractExpandableListAdapter(final Context context, final List<G> groups, final List<List<C>> children) {
		super();
		this.context = context;
		this.groups = groups;
		this.children = children;
	}

	/**
	 * Remove item from the list
	 * @param object Children
	 */
	public void removeItem(final C object) {
		for (final List<C> group : children) {
			if (group.contains(object)) {
				final int index = group.indexOf(object);
				group.remove(index);
			}
		}
	}

	@Override
	public C getChild(final int groupPosition, final int childPosition) {
		final List<C> groupChildren = children.get(groupPosition);
		return groupChildren != null
				&& groupChildren.size() > 0 && childPosition >= 0 && childPosition < groupChildren.size()
					? groupChildren.get(childPosition) : null;
	}

	@Override
	public abstract long getChildId(final int groupPosition, final int childPosition);

	@Override
	public int getChildrenCount(final int groupPosition) {
		return children == null ? 0 : children.get(groupPosition).size();
	}

	@Override
	public G getGroup(final int groupPosition) {
		final int groupCount = getGroupCount();
		return groupCount > 0 && groupPosition >= 0 && groupPosition < groupCount ? groups.get(groupPosition) : null;
	}

	@Override
	public int getGroupCount() {
		return groups == null ? 0 : groups.size();
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

	protected void addGroup(final G group) {
		this.groups.add(group);
		this.children.add(new ArrayList<C>());
	}

	protected void addChildToGroup(final G group, final C child) {
		if (!groups.contains(group)) {
			groups.add(group);
		}

		final int index = groups.indexOf(group);
		if (children.size() < index + 1) {
			children.add(new ArrayList<C>());
		}

		children.get(index).add(child);
	}

	/**
	 * Clear lists to avoid double data in listviews
	 *
	 */
	public void clear() {
		groups.clear();
		children.clear();
	}
}
