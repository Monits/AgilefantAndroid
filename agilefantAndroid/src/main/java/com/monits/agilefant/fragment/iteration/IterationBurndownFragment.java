package com.monits.agilefant.fragment.iteration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.service.AgilefantService;
import com.monits.agilefant.view.ScaleImageView;

import java.util.TimeZone;

import javax.inject.Inject;

public class IterationBurndownFragment extends BaseDetailTabFragment {


	private static final String PARAMS_ID = "id";

	private static final String URL = "/drawIterationBurndown.action?backlogId=%backlogId%&timeZoneOffset=%timeZone%";

	private long id;

	@Inject
	/* default */ ImageLoader imageLoader;

	@Inject
	/* default */ AgilefantService agilefantService;

	/**
	 * Creates a new IterationBurndownFragment for the given iteration id
	 * @param id The iteration id
	 * @return the new fragment
	 */
	public static IterationBurndownFragment newInstance(final long id) {
		final Bundle bundle = new Bundle();
		bundle.putLong(PARAMS_ID, id);

		final IterationBurndownFragment iterationBurndownFragment = new IterationBurndownFragment();
		iterationBurndownFragment.setArguments(bundle);

		return iterationBurndownFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AgilefantApplication.getObjectGraph().inject(this);

		final Bundle arguments = getArguments();
		id = arguments.getLong(PARAMS_ID, 0);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_iteration_burndown, container, false);
		final ScaleImageView burndown = (ScaleImageView) rootView.findViewById(R.id.burndown);

		final int timeZone = TimeZone.getDefault().getRawOffset() / (60 * 1000);

		String url = agilefantService.getHost() + URL.replace("%backlogId%", String.valueOf(id));
		url = url.replace("%timeZone%", String.valueOf(timeZone));

		imageLoader.get(url,
				ImageLoader.getImageListener(burndown, R.drawable.agilefant_logo_home, R.drawable.agilefant_logo_home));

		return rootView;
	}

	@Override
	public int getTitleResourceId() {
		return R.string.burndown;
	}

	@Override
	public String toString() {
		return "IterationBurndownFragment { id= " + id + '}';
	}

}
