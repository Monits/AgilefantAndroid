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

	/**
	 * Constructor
	 * @param id The project id
	 * @param title The project title
	 * @param iterationList Iteration List
	 */
	public Project(long id, String title, List<Iteration> iterationList) {
		this.id = id;
		this.title = title;
		this.iterationList = iterationList;
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
	public void setId(long id) {
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
	public void setTitle(String title) {
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
	public void setIterationList(List<Iteration> iterationList) {
		this.iterationList = iterationList;
	}
}
