package com.monits.agilefant.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(final Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(final int size) {
			return new User[size];
		}
	};

	@SerializedName("id")
	private long id;

	@SerializedName("initials")
	private String initials;

	@SerializedName("fullName")
	private String fullName;

	public User() {
	}

	private User(final Parcel in) {
		id = in.readLong();
		initials = in.readString();
		fullName = in.readString();
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @return the initials
	 */
	public String getInitials() {
		return initials;
	}

	/**
	 * @param initials the initials to set
	 */
	public void setInitials(final String initials) {
		this.initials = initials;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeLong(id);
		dest.writeString(initials);
		dest.writeString(fullName);
	}

	@Override
	public String toString() {
		// This value is shown when in landscape and using autocomplete.
		return fullName;
	}
}