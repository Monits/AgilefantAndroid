package com.monits.agilefant.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Metrics implements Parcelable {

	public static final Parcelable.Creator<Metrics> CREATOR = new Parcelable.Creator<Metrics>() {
		@Override
		public Metrics createFromParcel(final Parcel in) {
			return new Metrics(in);
		}

		@Override
		public Metrics[] newArray(final int size) {
			return new Metrics[size];
		}
	};

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
	 * @param effortLeft
	 * @param effortSpent
	 * @param originalEstimate
	 */
	public Metrics(final long effortLeft, final long effortSpent, final long originalEstimate) {
		super();
		this.effortLeft = effortLeft;
		this.effortSpent = effortSpent;
		this.originalEstimate = originalEstimate;
	}

	private Metrics(final Parcel in) {
		this.effortLeft = in.readLong();
		this.effortSpent = in.readLong();
		this.originalEstimate = in.readLong();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeLong(effortLeft);
		dest.writeLong(effortSpent);
		dest.writeLong(originalEstimate);
	}
}