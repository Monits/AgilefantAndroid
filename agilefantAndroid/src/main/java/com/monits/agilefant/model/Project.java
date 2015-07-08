package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Project implements Serializable {

	private static final long serialVersionUID = 6674750903458874065L;

	@SerializedName("id")
	private long id;

	@SerializedName("title")
	private String title;

	@SerializedName("name")
	private String name;

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

	/**
	 * Default constructor.
	 */
	public Project() {
		// Default constructor.
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

	/**
	 * Constructor
	 * @param id The id
	 * @param title The title
	 * @param iterationList The iterations
	 * @param startDate The start date
	 * @param endDate The end date
	 * @param assignees The assignees
	 */
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
	 *
	 */
	public String getTitle() {
		//Agilefant gives name in one endpoint and title in another.
		return name == null ? title : name;
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

	/**
	 * @return The start date
	 */
	public long getStartDate() {
		return startDate;
	}

	/**
	 * Set the start date
	 * @param startDate The date to set
	 */
	public void setStartDate(final long startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return The end date
	 */
	public long getEndDate() {
		return endDate;
	}

	/**
	 * Set the end date
	 * @param endDate The date to set
	 */
	public void setEndDate(final long endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return The assignees
	 */
	public List<User> getAssignees() {
		return assignees;
	}

	/**
	 * Set the assignees
	 * @param assignees The users to set
	 */
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
	public String toString() {
		final StringBuilder iterationListToStringBuilder = new StringBuilder("[");
		if (iterationList != null && !iterationList.isEmpty()) {
			for (final Iteration iteration : iterationList) {
				iterationListToStringBuilder.append(iteration).append(", ");
			}
		}
		iterationListToStringBuilder.append(']');

		final StringBuilder assigneesToStringBuilder = new StringBuilder("[");
		if (assignees != null && !assignees.isEmpty()) {
			for (final User responsible : assignees) {
				assigneesToStringBuilder.append(responsible).append(", ");
			}
		}
		assigneesToStringBuilder.append(']');

		return new StringBuilder("Project [id: ").append(id)
				.append(", title: ").append(title)
				.append(", name: ").append(name)
				.append(", iterationList: ").append(iterationListToStringBuilder.toString())
				.append(", startDate: ").append(startDate)
				.append(", endDate: ").append(endDate)
				.append(", assignees: ").append(assigneesToStringBuilder.toString())
				.append(", parent: ").append(parent)
				.append(']')
				.toString();
	}
}
