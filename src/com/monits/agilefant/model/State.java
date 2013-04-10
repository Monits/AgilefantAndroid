package com.monits.agilefant.model;

public class State {

	private String name;

	private int backgroundId;

	private int textColorId;

	/**
	 * @param name
	 * @param resourceId
	 */
	public State(String name, int resourceId, int textColorId) {
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
	public void setName(String name) {
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
	public void setBackgroundId(int backgroundId) {
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
	public void setTextColorId(int textColorId) {
		this.textColorId = textColorId;
	}
}