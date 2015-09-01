package com.monits.agilefant.fragment.iteration;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;


/**
 * Base fragment for iteration details tabs.
 */
public abstract class BaseDetailTabFragment extends Fragment {

	/**
	 * @return The id of the title background resource
	 */
	@DrawableRes
	public abstract int getTitleBackgroundResourceId();

	/**
	 * @return The id of the title color resource
	 */
	@ColorRes
	public abstract int getColorResourceId();

	/**
	 * @return The id of the title color resource
	 */
	@StringRes
	public abstract int getTitleResourceId();

}
