package com.monits.agilefant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable, Rankable<Task>, WorkItem {

	private static final long serialVersionUID = 2576001407807164868L;

	private static final int SHIFT = 32;
	public static final int PRIME = 31;

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
	private Story story;

	/**
	 * Default constructor
	 */
	public Task() {
		// Default constructor.
	}

	/**
	 * @param effortLeft The effort left
	 * @param effortSpent The effort spent
	 * @param originalEstimate The original statimate
	 * @param name The name
	 * @param id The id
	 * @param responsibles The responsibles
	 * @param state The state
	 * @param rank The rank
	 */
	public Task(final long effortLeft, final long effortSpent, final long originalEstimate, final String name,
			final long id, final List<User> responsibles, final StateKey state, final int rank) {
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
	 * Constructor.
	 * Copy the data of the given object, and creates a new object with the same internal state.
	 *
	 *  @param task The task to copy
	 */
	public Task(final Task task) {
		this.effortLeft = task.effortLeft;
		this.effortSpent = task.effortSpent;
		this.originalEstimate = task.originalEstimate;
		this.name = task.name;
		this.id = task.id;
		this.responsibles = task.responsibles;
		this.state = task.state;
		this.rank = task.rank;
		this.iteration = task.iteration;
		this.story = task.story;
	}

	@Override
	public Task getCopy() {
		return new Task(this);
	}

	/**
	 * @return the effortLeft
	 */
	@Override
	public long getEffortLeft() {
		return effortLeft;
	}

	/**
	 * @param effortLeft the effortLeft to set
	 */
	public void setEffortLeft(final long effortLeft) {
		this.effortLeft = effortLeft;
	}

	/**
	 * @return the effortSpent
	 */
	@Override
	public long getEffortSpent() {
		return effortSpent;
	}

	/**
	 * @param effortSpent the effortSpent to set
	 */
	public void setEffortSpent(final long effortSpent) {
		this.effortSpent = effortSpent;
	}

	/**
	 * @return the originalEstimate
	 */
	@Override
	public long getOriginalEstimate() {
		return originalEstimate;
	}

	@Override
	public WorkItemType getType() {
		return WorkItemType.TASK;
	}

	@Override
	public void updateValues(final WorkItem task) {

		if (!(task instanceof Task)) {
			throw new IllegalArgumentException("The parameter sent is not a Task!");
		}

		final Task innerTask = (Task) task;

		this.effortLeft = innerTask.getEffortLeft();
		this.effortSpent = innerTask.getEffortSpent();
		this.originalEstimate = innerTask.getOriginalEstimate();
		this.state = innerTask.getState();
		this.responsibles = innerTask.getResponsibles();
		this.name = innerTask.getName();
		this.rank = innerTask.getRank();
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
	@Override
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
	@Override
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
	@Override
	public List<User> getResponsibles() {
		return responsibles;
	}

	/**
	 * @param responsibles the responsibles to set
	 */
	public void setResponsibles(final List<User> responsibles) {
		this.responsibles = responsibles;
	}

	/**
	 * @return the state
	 */
	@Override
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

		if (state == StateKey.DONE && resetELIfDone) {
			this.effortLeft = 0;
		}
	}

	@Override
	public int getRank() {
		return rank;
	}

	@Override
	public void setRank(final int rank) {
		this.rank = rank;
	}

	/**
	 * @return the story
	 */
	public Story getStory() {
		return story;
	}

	/**
	 * @param story the story to set
	 */
	public void setStory(final Story story) {
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

	@Override
	public int hashCode() {
		return PRIME + (int) (id ^ (id >>> SHIFT));
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
		return id == other.id;
	}

	@Override
	public String toString() {
		final StringBuilder responsiblesToStringBuilder = new StringBuilder("[");
		for (final User responsible : responsibles) {
			responsiblesToStringBuilder.append(responsible.getInitials()).append(", ");
		}
		responsiblesToStringBuilder.append(']');

		return new StringBuilder("Task [id: ").append(id)
			.append(", name: ").append(name)
			.append(", effortLeft: ").append(effortLeft)
			.append(", effortSpent: ").append(effortSpent)
			.append(", originalEstimate: ").append(originalEstimate)
			.append(", responsibles: ").append(responsiblesToStringBuilder.toString())
			.append(", state: ").append(state)
			.append(", rank: ").append(rank)
			.append(", iteration: ").append(iteration == null ? null : iteration.getId())
			.append(", story: ").append(story == null ? null : story.getId())
			.append(']')
				.toString();
	}
}
