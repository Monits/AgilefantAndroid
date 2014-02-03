package com.monits.agilefant.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Backlog implements Parcelable {

	public static final Parcelable.Creator<Backlog> CREATOR = new Parcelable.Creator<Backlog>() {
		@Override
		public Backlog createFromParcel(final Parcel in) {
			return new Backlog(in);
		}

		@Override
		public Backlog[] newArray(final int size) {
			return new Backlog[size];
		}
	};

	@SerializedName("id")
	private long id;

	@SerializedName("name")
	private String name;

	public Backlog() {
	}

	public Backlog(final Project project) {
		this.id = project.getId();
		this.name = project.getTitle();
	}

	private Backlog(final Parcel in) {
		this.id = in.readLong();
		this.name = in.readString();
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
		dest.writeLong(id);
		dest.writeString(name);
	}
}
