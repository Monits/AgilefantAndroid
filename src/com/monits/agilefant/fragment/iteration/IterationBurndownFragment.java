package com.monits.agilefant.fragment.iteration;

import roboguice.fragment.RoboFragment;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.connector.HttpConnection;
import com.monits.agilefant.service.AgilefantService;

public class IterationBurndownFragment extends RoboFragment {


	private static final String PARAMS_ID = "id";

	private static final String URL = "/drawIterationBurndown.action?backlogId=%backlogId%&timeZoneOffset=-180";

	private long id;
	private ImageView burndown;

	@Inject
	private AgilefantService agilefantService;

	public static IterationBurndownFragment newInstance(long id) {
		Bundle bundle = new Bundle();
		bundle.putLong(PARAMS_ID, id);

		IterationBurndownFragment iterationBurndownFragment = new IterationBurndownFragment();
		iterationBurndownFragment.setArguments(bundle);

		return iterationBurndownFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			Bundle arguments = getArguments();

			id = arguments.getLong(PARAMS_ID, 0);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_iteration_burndown, null);
		burndown = (ImageView) rootView.findViewById(R.id.burndown);

		AQuery burndownQ = new AQuery(burndown);
		Drawable preset = getResources().getDrawable(R.drawable.agilefant_logo_home);

		BitmapAjaxCallback bitmapAjaxCallback = new BitmapAjaxCallback();
		bitmapAjaxCallback.memCache(true);
		bitmapAjaxCallback.cookie("JSESSIONID", HttpConnection.cookie);
		bitmapAjaxCallback.fallback(R.drawable.agilefant_logo_home);
		bitmapAjaxCallback.preset(((BitmapDrawable) preset).getBitmap());
		String url = agilefantService.getHost() + URL.replace("%backlogId%", String.valueOf(id));
		bitmapAjaxCallback.url(url);

		burndownQ.image(bitmapAjaxCallback);

		return rootView;
	}
}
