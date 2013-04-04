package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Product implements Serializable{

	private static final long serialVersionUID = 2998260609210275811L;

	@SerializedName("id")
	private long id;

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
	public Product(long id, String title, List<Project> projectList) {
		this.id = id;
		this.title = title;
		this.projectList = projectList;
	}

	/**
	 * @return The product id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id The product id to set
	 */
	public void setId(long id) {
		this.id = id;
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
	public void setTitle(String title) {
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
	public void setProjectList(List<Project> projectList) {
		this.projectList = projectList;
	}
}