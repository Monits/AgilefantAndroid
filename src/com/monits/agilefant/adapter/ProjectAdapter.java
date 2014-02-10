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

public class ProjectAdapter extends AbstractExpandableListAdapter<Project, Iteration> {

	private final LayoutInflater inflater;
	private final int groupResId;
	private final int childResId;

	/**
	 * @param context
	 */
	public ProjectAdapter(Context context) {
		this(context, null);
	}

	public ProjectAdapter(Context context, List<Project> projectList) {
		this(context, projectList, R.layout.default_project_item, R.layout.default_iteration_item);
	}

	public ProjectAdapter(Context context, List<Project> projectList, int groupResId, int childResId) {
		super(context);

		setProjects(projectList);

		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groupResId = groupResId;
		this.childResId = childResId;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		HolderChild holder;
		if (null == convertView) {
			holder = new HolderChild();
			View inflate = inflater.inflate(childResId, null);
			holder.title = (TextView) inflate.findViewById(R.id.title);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (HolderChild) convertView.getTag();
		}

		Iteration iteration = getChild(groupPosition, childPosition);

		holder.title.setText(iteration.getTitle());

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		HolderGroup holder;
		if (null == convertView) {
			holder = new HolderGroup();
			View inflate = inflater.inflate(groupResId, null);
			holder.title = (TextView) inflate.findViewById(R.id.title);
			holder.icon = (TextView) inflate.findViewById(R.id.icon);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (HolderGroup) convertView.getTag();
		}

		Project project = getGroup(groupPosition);

		holder.title.setText(project.getTitle());

		if (isExpanded) {
			holder.icon.setText("-");
		} else {
			holder.icon.setText("+");
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	/**
	 * set the projects to display.
	 *
	 * @param projectList the projects.
	 */
	public void setProjects(List<Project> projectList) {
		if (projectList != null) {
			for (Project project : projectList) {
				super.addGroup(project);
				for (Iteration iteration : project.getIterationList()) {
					super.addChildToGroup(project, iteration);
				}
			}
		}

		notifyDataSetChanged();
	}

	class HolderGroup {
		public TextView title;
		public TextView icon;
	}

	class HolderChild {
		public TextView title;
	}
}