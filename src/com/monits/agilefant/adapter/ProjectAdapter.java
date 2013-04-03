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

public class ProjectAdapter extends AbstractExpandableListAdapter<Project, Iteration>{

	private LayoutInflater inflater;

	public ProjectAdapter(Context context, List<Project> projectList) {
		super(context);
		for (Project project : projectList) {
			super.addGroup(project);
			for (Iteration iteration : project.getIterationList()) {
				super.addChildToGroup(project, iteration);
			}
		}
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		holderChild holder;
		if (null == convertView) {
			holder = new holderChild();
			View inflate = inflater.inflate(R.layout.iteration_item, null);
			holder.title = (TextView) inflate.findViewById(R.id.title);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (holderChild) convertView.getTag();
		}

		Iteration iteration = (Iteration) getChild(groupPosition, childPosition);

		holder.title.setText(iteration.getTitle());

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		holderGroup holder;
		if (null == convertView) {
			holder = new holderGroup();
			View inflate = inflater.inflate(R.layout.project_item, null);
			holder.title = (TextView) inflate.findViewById(R.id.title);

			convertView = inflate;
			convertView.setTag(holder);
		} else {
			holder = (holderGroup) convertView.getTag();
		}

		Project project = (Project) getGroup(groupPosition);

		holder.title.setText(project.getTitle());

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	class holderGroup {
		public TextView title;
	}

	class holderChild {
		public TextView title;
	}
}