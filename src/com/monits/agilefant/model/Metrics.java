package com.monits.agilefant.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Metrics implements Serializable{

	private static final long serialVersionUID = 8568693131774396328L;

	@SerializedName("effortLeft")
	private long effortLeft;

	@SerializedName("effortSpent")
	private long effortSpent;

	@SerializedName("originalEstimate")
	private long originalEstimate;

	/**
	 * @return the effortLeft
	 */
	public long getEffortLeft() {
		return effortLeft;
	}

	/**
	 * @param effortLeft the effortLeft to set
	 */
	public void setEffortLeft(long effortLeft) {
		this.effortLeft = effortLeft;
	}

	/**
	 * @return the effortSpent
	 */
	public long getEffortSpent() {
		return effortSpent;
	}

	/**
	 * @param effortSpent the effortSpent to set
	 */
	public void setEffortSpent(long effortSpent) {
		this.effortSpent = effortSpent;
	}

	/**
	 * @return the originalEstimate
	 */
	public long getOriginalEstimate() {
		return originalEstimate;
	}

	/**
	 * @param originalEstimate the originalEstimate to set
	 */
	public void setOriginalEstimate(long originalEstimate) {
		this.originalEstimate = originalEstimate;
	}
}