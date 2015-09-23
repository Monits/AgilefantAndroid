package com.monits.agilefant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.recyclerviewholders.ItemViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.IterationViewHolder;
import com.monits.agilefant.adapter.recyclerviewholders.BacklogViewHolder;
import com.monits.agilefant.model.Backlog;
import com.monits.agilefant.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdeira on 10/09/15.
 */

public class AllBacklogsAdapter extends RecyclerView.Adapter<BacklogViewHolder> {

	private final Context context;
	private final LayoutInflater inflater;
	private List<Backlog> backLogsList;

	/**
	 * Constructor
	 * @param context The context
	 */
	public AllBacklogsAdapter(final Context context) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public BacklogViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int type) {

		final View inflate = inflater.inflate(type, viewGroup, false);

		switch (type) {
		case R.layout.product_item:
		case R.layout.project_item:
			return new ItemViewHolder(inflate, context);
		case R.layout.iteration_item:
			return new IterationViewHolder(inflate, context);
		default:
			throw new AssertionError("can not find view type");
		}
	}

	@Override
	public void onBindViewHolder(final BacklogViewHolder holder, final int position) {
		final Backlog backlog = backLogsList.get(position);
		holder.onBindViewHolder(backlog);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (backlog.isExpanded()) {
					removeBacklogChildren(backlog, holder.getAdapterPosition() + 1);
				} else {
					addBackLogs(backlog, holder.getAdapterPosition() + 1);
				}
				holder.onItemClick();
			}
		});
	}

	@Override
	public int getItemCount() {
		return backLogsList == null ? 0 : backLogsList.size();
	}

	/**
	 * Add products to Backlogs
	 * @param productlist List of products
	 */
	public void setBacklogs(final List<Product> productlist) {
		this.backLogsList = new ArrayList<Backlog>(productlist);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(final int position) {
		final Backlog backlog = backLogsList.get(position);
		switch (backlog.getType()) {
		case PRODUCT:
			return R.layout.product_item;
		case PROJECT:
			return R.layout.project_item;
		case ITERATION:
			return R.layout.iteration_item;
		default:
			throw new AssertionError("can not find view type");
		}
	}

	private void addBackLogs(final Backlog backlog, final int position) {
		final List<Backlog> backlogs = backlog.getChildren();
		int i = 0;
		for (final Backlog backlogItem : backlogs) {
			backLogsList.add(position + i, backlogItem);
			i++;
		}
		notifyItemRangeInserted(position, backlogs.size());

	}

	private void removeBacklogChildren(final Backlog backlog, final int position) {

		final List<Backlog> backlogs = backlog.getChildren();
		backLogsList.removeAll(backlogs);
		int childrenRemoved = 0;
		for (final Backlog backlogItem : backlogs) {
			if (backlogItem.isExpanded()) {
				backlogItem.setExpanded(false);
				backLogsList.removeAll(backlogItem.getChildren());
				childrenRemoved += backlogItem.getChildren().size();
			}
		}
		notifyItemRangeRemoved(position, backlogs.size() + childrenRemoved);
	}

}
