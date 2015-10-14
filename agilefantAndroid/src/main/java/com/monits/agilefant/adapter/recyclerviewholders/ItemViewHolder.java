package com.monits.agilefant.adapter.recyclerviewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.activity.ProjectActivity;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.backlog.BacklogType;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdeira on 18/09/15.
 */
public class ItemViewHolder extends BacklogViewHolder {
	@Bind(R.id.txt_title)
	TextView title;
	@Bind(R.id.txt_icon)
	TextView icon;
	Backlog backlog;
	final Context context;

	/**
	 * Item View Holder
	 * @param itemView Inflate view
	 * @param context Context
	 */
	public ItemViewHolder(final View itemView, final Context context) {
		super(itemView);
		ButterKnife.bind(this, itemView);
		this.context = context;
		itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(final View v) {
				if (backlog.getType() == BacklogType.PROJECT) {
					final Intent intent = new Intent(context, ProjectActivity.class);
					intent.putExtra(ProjectActivity.EXTRA_BACKLOG, backlog);
					context.startActivity(intent);
					return true;
				}
				return false;
			}
		});

	}

	@Override
	public void onBindViewHolder(final Backlog backlog) {
		this.backlog = backlog;
		title.setText(backlog.getTitle());
		setIconExpanded();
	}

	/**
	 * Set Icon Expanded or not
	 */
	private void setIconExpanded() {
		if (backlog.isExpanded()) {
			icon.setText(context.getResources().getString(R.string.minus_expanded));
		} else {
			icon.setText(context.getResources().getString(R.string.plus_expanded));
		}
	}

	@Override
	public void onItemClick() {
		backlog.setExpanded(!backlog.isExpanded());
		setIconExpanded();
	}

	@Override
	public String toString() {
		return new StringBuilder("Item View Holder id: ").append(backlog.getId()).append(", title: ")
				.append(backlog.getName()).append(' ').append(super.toString())
				.toString();
	}
}
