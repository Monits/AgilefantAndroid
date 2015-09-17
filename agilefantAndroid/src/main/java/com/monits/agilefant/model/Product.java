package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.monits.agilefant.model.backlog.BacklogType;

public class Product extends Backlog implements Serializable {

	private static final long serialVersionUID = 2998260609210275811L;

	@SerializedName("title")
	private String title;

	@SerializedName("children")
	private List<Project> projectList;

	/**
	 * Constructor
	 * @param id The product id
	 * @param title The product title
	 * @param projectList Project List
	 */
	public Product(final long id, final String title, final List<Project> projectList) {
		super(id, title);
		this.title = title;
		this.projectList = projectList;
	}

	/**
	 * @return The product title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The product title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return The projectList
	 */
	public List<Project> getProjectList() {
		return this.projectList;
	}

	/**
	 * @param projectList the projectList to set
	 */
	public void setProjectList(final List<Project> projectList) {
		this.projectList = projectList;
	}

	@Override
	public BacklogType getType() {
		return BacklogType.PRODUCT;
	}

	@Override
	public String toString() {
		final StringBuilder projectListToStringBuilder = new StringBuilder("[");
		for (final Project project : projectList) {
			projectListToStringBuilder.append(project).append(", ");
		}
		projectListToStringBuilder.append(']');

		return new StringBuilder("Product [title: ").append(title)
				.append(", projectList: ").append(projectListToStringBuilder.toString())
				.append(' ').append(super.toString())
				.toString();
	}
}