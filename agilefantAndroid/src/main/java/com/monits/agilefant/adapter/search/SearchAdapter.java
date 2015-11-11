package com.monits.agilefant.adapter.search;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monits.agilefant.R;

/**
 * Created by edipasquale on 09/11/15.
 */
public class SearchAdapter extends CursorAdapter {

	/**
	 * Standard Adapter
	 * @param context The context
	 * @param c Cursor with data
	 * @param flags Flag that determinates adapter's behavior
	 */
	public SearchAdapter(final Context context, final Cursor c, final int flags) {
		super(context, c, flags);
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return inflater.inflate(R.layout.item_backlog_search, parent, false);
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {

		final SearchCursor searchCursor = (SearchCursor) cursor;

		final TextView type = (TextView) view.findViewById(R.id.suggest_type);
		final TextView label = (TextView) view.findViewById(R.id.suggest_label);

		type.setText(searchCursor.getType());
		label.setText(searchCursor.getLabel());
	}
}
