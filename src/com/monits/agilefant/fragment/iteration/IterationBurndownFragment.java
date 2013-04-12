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

public class IterationBurndownFragment extends RoboFragment{


	private static final String URL = "/drawIterationBurndown.action?backlogId=%backlogId%&timeZoneOffset=-180";

	private ImageView burndown;

	@Inject
	private AgilefantService agilefantService;

	private long id;

	public IterationBurndownFragment(long id) {
		this.id = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_iteration_burndown, container, false);
		burndown = (ImageView) rootView.findViewById(R.id.burndown);

		AQuery burndownQ = new AQuery(burndown);
		Drawable preset = getResources().getDrawable(R.drawable.agilefant_logo_home);

		BitmapAjaxCallback.setReuseHttpClient(true);
		BitmapAjaxCallback.clearCache();
		BitmapAjaxCallback bitmapAjaxCallback = new BitmapAjaxCallback();
		bitmapAjaxCallback.memCache(false);
		bitmapAjaxCallback.cookie("JSESSIONID", HttpConnection.cookie);
		bitmapAjaxCallback.fallback(R.drawable.agilefant_logo_home);
		bitmapAjaxCallback.preset(((BitmapDrawable) preset).getBitmap());
		String url = agilefantService.getHost() + URL.replace("%backlogId%", String.valueOf(id));
		bitmapAjaxCallback.networkUrl(url);
		burndownQ.image(bitmapAjaxCallback);
		return rootView;
	}
}
