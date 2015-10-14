package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.Project;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectAdapter extends AbstractExpandableListAdapter<Project, Iteration> {

	private final LayoutInflater inflater;
	private final int groupResId;
	private final int childResId;

	/**
	 * Constructor
	 * @param context The context
	 */
	public ProjectAdapter(final Context context) {
		this(context, null);
	}

	/**
	 * Constructor
	 * @param context The context
	 * @param projectList The list of projects.
	 */
	public ProjectAdapter(final Context context, final List<Project> projectList) {
		this(context, projectList, R.layout.default_project_item, R.layout.default_iteration_item);
	}

	/**
	 * Constructor
	 * @param context The context
	 * @param projectList The list of projects.
	 * @param groupResId The group id
	 * @param childResId THe child id
	 */
	public ProjectAdapter(final Context context, final List<Project> projectList, final int groupResId,
			final int childResId) {
		super(context);

		setProjects(projectList);

		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groupResId = groupResId;
		this.childResId = childResId;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			final boolean isLastChild, final View convertView, final ViewGroup parent) {

		final View ret;
		final HolderChild holder;
		if (null == convertView) {
			ret = inflater.inflate(childResId, null);
			holder = new HolderChild(ret);

			ret.setTag(holder);
		} else {
			ret = convertView;
			holder = (HolderChild) convertView.getTag();
		}

		final Iteration iteration = getChild(groupPosition, childPosition);

		holder.title.setText(iteration.getTitle());

		return ret;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			final View convertView, final ViewGroup parent) {

		final View ret;
		final HolderGroup holder;
		if (null == convertView) {
			ret = inflater.inflate(groupResId, null);
			holder = new HolderGroup(ret);

			ret.setTag(holder);
		} else {
			ret = convertView;
			holder = (HolderGroup) convertView.getTag();
		}

		final Project project = getGroup(groupPosition);

		holder.title.setText(project.getTitle());

		if (isExpanded) {
			holder.icon.setText("-");
		} else {
			holder.icon.setText("+");
		}

		return ret;
	}

	/**
	 * set the projects to display.
	 *
	 * @param projectList the projects.
	 */
	public final void setProjects(final List<Project> projectList) {
		if (projectList != null) {
			for (final Project project : projectList) {
				super.addGroup(project);
				for (final Iteration iteration : project.getIterationList()) {
					super.addChildToGroup(project, iteration);
				}
			}
		}

		notifyDataSetChanged();
	}

	static class HolderGroup {
		@Bind(R.id.txt_title)
		TextView title;
		@Bind(R.id.txt_icon)
		TextView icon;

		public HolderGroup(final View view) {
			ButterKnife.bind(this, view);
		}
	}

	static class HolderChild {
		@Bind(R.id.txt_title)
		TextView title;

		public HolderChild(final View view) {
			ButterKnife.bind(this, view);
		}
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		final Iteration child = getChild(groupPosition, childPosition);
		return child == null ? -1 : child.getId();
	}

	@Override
	public long getGroupId(final int groupPosition) {
		final Project group = getGroup(groupPosition);
		return group == null ? -1 : group.getId();
	}

	@Override
	public String toString() {
		return "ProjectAdapter{"
				+ "inflater=" + inflater
				+ ", groupResId=" + groupResId
				+ ", childResId=" + childResId
				+ '}';
	}
}