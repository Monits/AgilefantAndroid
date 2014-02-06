package com.monits.agilefant.model;

import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Project implements Parcelable {

	public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
		@Override
		public Project createFromParcel(final Parcel in) {
			return new Project(in);
		}

		@Override
		public Project[] newArray(final int size) {
			return new Project[size];
		}
	};

	@SerializedName("id")
	private long id;

	@SerializedName("title")
	private String title;

	@SerializedName("children")
	private List<Iteration> iterationList;

	@SerializedName("startDate")
	private long startDate;

	@SerializedName("endDate")
	private long endDate;

	@SerializedName("assignees")
	private List<User> assignees;

	@SerializedName("root")
	private Backlog parent;

	public Project() {
	}

	/**
	 * Constructor
	 * @param id The project id
	 * @param title The project title
	 * @param iterationList Iteration List
	 */
	public Project(final long id, final String title, final List<Iteration> iterationList) {
		this.id = id;
		this.title = title;
		this.iterationList = iterationList;
	}

	public Project(final long id, final String title, final List<Iteration> iterationList,
			final long startDate, final long endDate, final List<User> assignees) {
		super();
		this.id = id;
		this.title = title;
		this.iterationList = iterationList;
		this.startDate = startDate;
		this.endDate = endDate;
		this.assignees = assignees;
	}

	private Project(final Parcel in) {
		this.id = in.readLong();
		this.title = in.readString();
		this.iterationList = new LinkedList<Iteration>();
		in.readList(iterationList, Iteration.class.getClassLoader());
		this.startDate = in.readLong();
		this.endDate = in.readLong();
		this.assignees = new LinkedList<User>();
		in.readList(assignees, User.class.getClassLoader());
		this.parent = in.readParcelable(Backlog.class.getClassLoader());
	}

	/**
	 * @return The project id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id The project id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @return The project title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The project title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return The iterationList
	 */
	public List<Iteration> getIterationList() {
		return iterationList;
	}

	/**
	 * @param iterationList The iterationList to set
	 */
	public void setIterationList(final List<Iteration> iterationList) {
		this.iterationList = iterationList;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(final long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(final long endDate) {
		this.endDate = endDate;
	}

	public List<User> getAssignees() {
		return assignees;
	}

	public void setAssignees(final List<User> assignees) {
		this.assignees = assignees;
	}

	/**
	 * @return the parent
	 */
	public Backlog getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(final Backlog parent) {
		this.parent = parent;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeLong(id);
		dest.writeString(title);
		dest.writeList(iterationList);
		dest.writeLong(startDate);
		dest.writeLong(endDate);
		dest.writeList(assignees);
		dest.writeParcelable(parent, flags);
	}
}
