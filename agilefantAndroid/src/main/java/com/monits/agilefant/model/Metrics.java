package com.monits.agilefant.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Metrics implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2283324819991736310L;

	@SerializedName("effortLeft")
	private long effortLeft;

	@SerializedName("effortSpent")
	private long effortSpent;

	@SerializedName("originalEstimate")
	private long originalEstimate;

	/**
	 * Default constructor
	 */
	public Metrics() {
	}

	/**
	 * Constructor using fields
	 *
	 * @param effortLeft The effort left
	 * @param effortSpent The effort spent
	 * @param originalEstimate The original estimate
	 */
	public Metrics(final long effortLeft, final long effortSpent, final long originalEstimate) {
		super();
		this.effortLeft = effortLeft;
		this.effortSpent = effortSpent;
		this.originalEstimate = originalEstimate;
	}

	/**
	 * @return the effortLeft
	 */
	public long getEffortLeft() {
		return effortLeft;
	}

	/**
	 * @param effortLeft the effortLeft to set
	 */
	public void setEffortLeft(final long effortLeft) {
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
	public void setEffortSpent(final long effortSpent) {
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
	public void setOriginalEstimate(final long originalEstimate) {
		this.originalEstimate = originalEstimate;
	}
}