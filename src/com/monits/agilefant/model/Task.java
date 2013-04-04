package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Task implements Serializable{

	private static final long serialVersionUID = 5089050545685421289L;

	@SerializedName("effortLeft")
	private long effortLeft;

	@SerializedName("effortSpent")
	private long effortSpent;

	@SerializedName("originalEstimate")
	private long originalEstimate;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private long id;

	@SerializedName("responsibles")
	private List<User> responsibles;

	@SerializedName("state")
	private String state;

	/**
	 * @return the effortLeft
	 */
	public long getEffortLeft() {
		return effortLeft;
	}

	/**
	 * @param effortLeft the effortLeft to set
	 */
	public void setEffortLeft(long effortLeft) {
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
	public void setEffortSpent(long effortSpent) {
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
	public void setOriginalEstimate(long originalEstimate) {
		this.originalEstimate = originalEstimate;
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
	public void setName(String name) {
		this.name = name;
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
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the responsibles
	 */
	public List<User> getResponsibles() {
		return responsibles;
	}

	/**
	 * @param responsibles the responsibles to set
	 */
	public void setResponsibles(List<User> responsibles) {
		this.responsibles = responsibles;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
}
