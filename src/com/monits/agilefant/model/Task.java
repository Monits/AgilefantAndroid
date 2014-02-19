package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;
import java.util.Observable;

import com.google.gson.annotations.SerializedName;

public class Task extends Observable implements Serializable, Rankable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2576001407807164868L;

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
	private StateKey state;

	@SerializedName("rank")
	private int rank;

	@SerializedName("iteration")
	private Iteration iteration;

	@SerializedName("story")
	private Backlog story;

	public Task() {
	}

	/**
	 * @param effortLeft
	 * @param effortSpent
	 * @param originalEstimate
	 * @param name
	 * @param id
	 * @param responsibles
	 * @param state
	 * @param rank
	 */
	public Task(final long effortLeft, final long effortSpent, final long originalEstimate,
			final String name, final long id, final List<User> responsibles, final StateKey state,
			final int rank) {
		super();
		this.effortLeft = effortLeft;
		this.effortSpent = effortSpent;
		this.originalEstimate = originalEstimate;
		this.name = name;
		this.id = id;
		this.responsibles = responsibles;
		this.state = state;
		this.rank = rank;
	}

	/**
	 * @return the effortLeft
	 */
	public long getEffortLeft() {
		return effortLeft;
	}

	/**
	 * @param effortLeft the effortLeft to set
	 */
	public void setEffortLeft(final long effortLeft) {
		this.effortLeft = effortLeft;
		setChanged();
		notifyObservers();
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
	public void setEffortSpent(final long effortSpent) {
		this.effortSpent = effortSpent;
		setChanged();
		notifyObservers();
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
	public void setOriginalEstimate(final long originalEstimate) {
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
	public void setName(final String name) {
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
	 * @return the responsibles
	 */
	public List<User> getResponsibles() {
		return responsibles;
	}

	/**
	 * @param responsibles the responsibles to set
	 */
	public void setResponsibles(final List<User> responsibles) {
		this.responsibles = responsibles;
		setChanged();
		notifyObservers();
	}

	/**
	 * @return the state
	 */
	public StateKey getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(final StateKey state) {
		setState(state, false);
	}

	/**
	 * @param state the state to set
	 * @param resetELIfDone whether if effort left should be reset to zero, if {@link StateKey} is done.
	 */
	public void setState(final StateKey state, final boolean resetELIfDone) {
		this.state = state;

		if (state.equals(StateKey.DONE) && resetELIfDone) {
			this.effortLeft = 0;
		}

		setChanged();
		notifyObservers();
	}

	@Override
	public int  getRank() {
		return rank;
	}

	@Override
	public void setRank(final int rank) {
		this.rank = rank;
		setChanged();
		notifyObservers();
	}

	/**
	 * @return the story
	 */
	public Backlog getStory() {
		return story;
	}

	/**
	 * @param story the story to set
	 */
	public void setStory(final Backlog story) {
		this.story = story;
	}

	/**
	 * @return the iteration
	 */
	public Iteration getIteration() {
		return iteration;
	}

	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(final Iteration iteration) {
		this.iteration = iteration;
	}

	/**
	 * This is a convenience to update multiple values at once and to notify changes only once, to avoid
	 * views to render multiple times.
	 *
	 * @param task the updated task.
	 */
	public void updateValues(final Task task) {
		this.effortLeft = task.getEffortLeft();
		this.effortSpent = task.getEffortSpent();
		this.originalEstimate = task.getOriginalEstimate();
		this.state = task.getState();
		this.responsibles = task.getResponsibles();
		setChanged();
		notifyObservers();
	}

	@Override
	public Task clone() {
		final Task clonedTask = new Task();
		clonedTask.setEffortLeft(effortLeft);
		clonedTask.setEffortSpent(effortSpent);
		clonedTask.setOriginalEstimate(originalEstimate);
		clonedTask.setName(name);
		clonedTask.setId(id);
		clonedTask.setResponsibles(responsibles);
		clonedTask.setState(state);
		clonedTask.setRank(rank);
		clonedTask.setIteration(iteration);
		clonedTask.setStory(story);
		return clonedTask;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Task)) {
			return false;
		}
		final Task other = (Task) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}
}
