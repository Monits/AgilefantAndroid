package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Project implements Serializable{

	private static final long serialVersionUID = 3724474795941262305L;

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
}
