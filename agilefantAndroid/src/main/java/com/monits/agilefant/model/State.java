package com.monits.agilefant.model;

public class State {

	private String name;

	private int backgroundId;

	private int textColorId;

	/**
	 * Constructor
	 * @param name The name
	 * @param resourceId The resource id
	 * @param textColorId The text color id.
	 */
	public State(final String name, final int resourceId, final int textColorId) {
		this.name = name;
		this.backgroundId = resourceId;
		this.textColorId = textColorId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the backgroundId
	 */
	public int getBackgroundId() {
		return backgroundId;
	}

	/**
	 * @param backgroundId the backgroundId to set
	 */
	public void setBackgroundId(final int backgroundId) {
		this.backgroundId = backgroundId;
	}

	/**
	 * @return the textColorId
	 */
	public int getTextColorId() {
		return textColorId;
	}

	/**
	 * @param textColorId the textColorId to set
	 */
	public void setTextColorId(final int textColorId) {
		this.textColorId = textColorId;
	}
}