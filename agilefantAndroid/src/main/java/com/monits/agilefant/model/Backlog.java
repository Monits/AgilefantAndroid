package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.monits.agilefant.model.backlog.BacklogType;

public abstract class Backlog implements Serializable {

	private static final long serialVersionUID = -2908515098376303451L;

	@SerializedName("id")
	private long id;

	@SerializedName("name")
	private String name;

	private boolean expanded;

	/**
	 * Default constructor.
	 */
	public Backlog() {
		// Default constructor.
	}

	/**
	 * Constructor
	 * @param id	The id of this backlog
	 * @param name	The human-readable name for this backlog
	 */
	public Backlog(final long id, final String name) {
		this.id = id;
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

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(final boolean expanded) {
		this.expanded = expanded;
	}

	/**
	 * Get Title
	 * @return title of Backlog
	 */
	public abstract String getTitle();

	/**
	 * Backlog type
	 * @return return the type of the backlog
	 */
	public abstract BacklogType getType();

	/**
	 * getParent method
	 * @return Backlog parent
	 */
	public abstract Backlog getParent();

	/**
	 * getChildren
	 * @return Backlog list of Children
	 */
	public abstract List<Backlog> getChildren();

	@Override
	public String toString() {
		return new StringBuilder("Backlog [id: ").append(id).append(", name: ").append(name)
				.append(", expanded: ").append(expanded).append(']').toString();
	}
}
