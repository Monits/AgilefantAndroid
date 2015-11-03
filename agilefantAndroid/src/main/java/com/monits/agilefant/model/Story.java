package com.monits.agilefant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Story implements Serializable, Rankable<Story>, WorkItem {

	private static final long serialVersionUID = 5178157997788833446L;

	private static final int SHIFT = 32;
	private static final int PRIME = 31;

	@SerializedName("id")
	private long id;

	@SerializedName("name")
	private String name;

	@SerializedName("state")
	private StateKey state;

	@SerializedName("responsibles")
	private List<User> responsibles;

	@SerializedName("metrics")
	private Metrics metrics;

	@SerializedName("tasks")
	private List<Task> tasks;

	@SerializedName("rank")
	private int rank;

	@SerializedName("backlog")
	private Backlog backlog;

	@SerializedName("iteration")
	private Iteration iteration;

	private transient boolean expanded;

	/**
	 * Default constructor.
	 */
	public Story() {
		// Default constructor.
	}

	/**
	 * @param id The id
	 * @param name The name
	 * @param state The state
	 * @param responsibles The responsibles
	 * @param metrics The metrics
	 * @param tasks The tasks
	 * @param rank The rank
	 * @param backlog The backlog
	 * @param iteration The iteration
	 */
	public Story(final long id, final String name, final StateKey state, final List<User> responsibles,
			final Metrics metrics, final List<Task> tasks, final int rank, final Backlog backlog,
			final Iteration iteration) {
		super();
		this.id = id;
		this.name = name;
		this.state = state;
		this.responsibles = responsibles;
		this.metrics = metrics;
		this.tasks = tasks;
		this.rank = rank;
		this.backlog = backlog;
		this.iteration = iteration;
	}

	/**
	 * Constructor.
	 * Copy the data of the given object, and creates a new object with the same internal state.
	 *
	 *  @param story The story to copy
	 */
	public Story(final Story story) {
		this(story.id, story.name, story.state, story.responsibles, story.metrics,
			story.tasks == null ? null : new LinkedList<>(story.tasks), story.rank, story.backlog, story.iteration);
	}

	@Override
	public Story getCopy() {
		return new Story(this);
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
		this.state = state;
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
	}

	/**
	 * @return the metrics
	 */
	public Metrics getMetrics() {
		if (metrics == null) {
			metrics = new Metrics();
		}
		return metrics;
	}

	/**
	 * @param metrics the metrics to set
	 */
	public void setMetrics(final Metrics metrics) {
		this.metrics = metrics;
	}

	/**
	 * @return the tasks
	 */
	public List<Task> getTasks() {
		return tasks == null ? Collections.EMPTY_LIST : tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(final List<Task> tasks) {
		this.tasks = tasks;
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
	 * @return the context
	 */
	public Backlog getBacklog() {
		return backlog;
	}

	/**
	 * @param backlog the context to set
	 */
	public void setBacklog(final Backlog backlog) {
		this.backlog = backlog;
	}

	@Override
	public void updateValues(final WorkItem story) {

		if (!(story instanceof Story)) {
			throw new IllegalArgumentException("The parameter sent is not a Story!");
		}

		final Story innerStory = (Story) story;

		this.name = innerStory.getName();
		this.rank = innerStory.getRank();
		this.backlog = innerStory.getBacklog();
		this.iteration = innerStory.getIteration();
		this.tasks = innerStory.getTasks();
		this.metrics = innerStory.getMetrics();
		this.state = innerStory.getState();
		this.responsibles = innerStory.getResponsibles();

	}

	@Override
	public long getEffortLeft() {
		return getMetrics().getEffortLeft();
	}

	@Override
	public long getEffortSpent() {
		return getMetrics().getEffortSpent();
	}

	@Override
	public long getOriginalEstimate() {
		return getMetrics().getOriginalEstimate();
	}

	@Override
	public WorkItemType getType() {
		return WorkItemType.STORY;
	}

	/**
	 * @return True if work item is expanded
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * @param expanded expanded to set
	 */
	public void setExpanded(final boolean expanded) {
		this.expanded = expanded;
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
		if (!(obj instanceof Story)) {
			return false;
		}
		final Story other = (Story) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		final StringBuilder responsiblesToStringBuilder = new StringBuilder("[");
		for (final User responsible : responsibles) {
			responsiblesToStringBuilder.append(responsible.getInitials()).append(", ");
		}
		responsiblesToStringBuilder.append(']');

		final StringBuilder tasksToStringBuilder = new StringBuilder("[");
		for (final Task task : tasks) {
			tasksToStringBuilder.append(task.getId()).append(", ");
		}
		tasksToStringBuilder.append(']');

		return new StringBuilder("Story [id: ").append(id)
			.append(", name: ").append(name)
			.append(", state: ").append(state)
			.append(", responsibles: ").append(responsiblesToStringBuilder.toString())
			.append(", metrics: ").append(metrics)
			.append(", tasks: ").append(tasksToStringBuilder.toString())
			.append(", rank: ").append(rank)
			.append(", backlog: ").append(backlog)
			.append(", iteration: ").append(iteration)
			.append(", expanded: ").append(expanded)
			.append(']')
			.toString();
	}
}
