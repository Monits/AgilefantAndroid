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

	/**
	 * Constructor
	 * @param context The context
	 */
	public SelectedUsersAdapter(final Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return users == null ? 0 : users.size();
	}

	@Override
	public User getItem(final int position) {
		final int count = getCount();
		return count > 0 && position < count ? users.get(position) : null;
	}

	@Override
	public long getItemId(final int position) {
		final User user = getItem(position);
		return user == null ? 0 : user.getId();
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
					onRemoveListener.onRemoveUser(view, position, user);

					return true;
				}

				return false;
			}
		});

		return view;
	}

	/**
	 * Set the users
	 * @param users The users to set
	 */
	public void setUsers(final List<User> users) {
		this.users = users;

		notifyDataSetChanged();
	}

	/**
	 * Set a listener for removal of users.
	 * @param listener The listener
	 */
	public void setOnRemoveUserListener(final OnRemoveUserListener listener) {
		this.onRemoveListener = listener;
	}

	/**
	 * Interface for changes in the users list
	 */
	public interface OnRemoveUserListener {
		
		/**
		 * Called after a user removal
		 * @param view The view
		 * @param position The item position
		 * @param user The user
		 */
		void onRemoveUser(View view, int position, User user);
	}
}
