package com.monits.agilefant.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RootIteration implements Parcelable {

	public static final Parcelable.Creator<RootIteration> CREATOR = new Parcelable.Creator<RootIteration>() {
		@Override
		public RootIteration createFromParcel(final Parcel in) {
			return new RootIteration(in);
		}

		@Override
		public RootIteration[] newArray(final int size) {
			return new RootIteration[size];
		}
	};

	@SerializedName("name")
	private String name;

	public RootIteration() {
	}

	private RootIteration(final Parcel in) {
		this.name = in.readString();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(name);
	}
}
