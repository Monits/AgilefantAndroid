package com.monits.agilefant.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.listeners.OnDrawableClickListener;
import com.monits.agilefant.listeners.OnDrawableClickListener.DrawablePosition;
import com.monits.agilefant.model.User;

public class SelectedUsersAdapter extends BaseAdapter {

	private final Context context;
	private List<User> users;
	private OnRemoveUserListener onRemoveListener;

	public SelectedUsersAdapter(final Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return users != null ? users.size() : 0;
	}

	@Override
	public User getItem(final int position) {
		final int count = getCount();
		return count > 0 && position < count ? users.get(position) : null;
	}

	@Override
	public long getItemId(final int position) {
		final User user = getItem(position);
		return user != null ? user.getId() : 0;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {

		final TextView view;
		if (convertView == null) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			view = (TextView) inflater.inflate(R.layout.item_selected_user, parent, false);
		} else {
			view = (TextView) convertView;
		}

		final User user = getItem(position);

		view.setText(user.getFullName());
		view.setOnTouchListener(new OnDrawableClickListener(view, DrawablePosition.RIGHT) {

			@Override
			public boolean onDrawableClick(final MotionEvent event) {
				if (onRemoveListener != null) {
					onRemoveListener.OnRemoveUser(view, position, user);

					return true;
				}

				return false;
			}
		});

		return view;
	}

	public void setUsers(final List<User> users) {
		this.users = users;

		notifyDataSetChanged();
	}

	public void setOnRemoveUserListener(final OnRemoveUserListener listener) {
		this.onRemoveListener = listener;
	}

	public static interface OnRemoveUserListener {
		void OnRemoveUser(View view, int position, User user);
	}
}
