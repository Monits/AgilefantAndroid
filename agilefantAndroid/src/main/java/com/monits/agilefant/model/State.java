package com.monits.agilefant.model;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class State {

	private int name;

	private int backgroundId;

	private int textColorId;

	/**
	 * Constructor
	 * @param name The name
	 * @param resourceId The resource id
	 * @param textColorId The text color id.
	 */
	public State(@StringRes final int name, @DrawableRes final int resourceId,
					@ColorRes final int textColorId) {
		this.name = name;
		this.backgroundId = resourceId;
		this.textColorId = textColorId;
	}

	/**
	 * @return the name
	 */
	@StringRes
	public int getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(@StringRes final int name) {
		this.name = name;
	}

	/**
	 * @return the backgroundId
	 */
	@DrawableRes
	public int getBackgroundId() {
		return backgroundId;
	}

	/**
	 * @param backgroundId the backgroundId to set
	 */
	public void setBackgroundId(@DrawableRes final int backgroundId) {
		this.backgroundId = backgroundId;
	}

	/**
	 * @return the textColorId
	 */
	@ColorRes
	public int getTextColorId() {
		return textColorId;
	}

	/**
	 * @param textColorId the textColorId to set
	 */
	public void setTextColorId(@ColorRes final int textColorId) {
		this.textColorId = textColorId;
	}

	@Override
	public String toString() {
		return new StringBuilder("State [name: ").append(name)
				.append(", backgroundId: ").append(backgroundId)
				.append(", textColorId: ").append(textColorId)
				.append(']').toString();
	}
}