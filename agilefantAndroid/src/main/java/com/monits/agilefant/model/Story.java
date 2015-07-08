package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.annotations.SerializedName;

public class Story extends Observable implements Serializable, Observer, Rankable<Story> {

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

	/**
	 * Default constructor.
	 */
	public Story() {
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
			story.tasks != null ? new LinkedList<>(story.tasks) : null, story.rank, story.backlog, story.iteration);
	}

	@Override
	public Story getCopy() {
		return new Story(this);
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
		this.state = state;

		setChanged();
		notifyObservers();
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
		return tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(final List<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public int  getRank() {
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
		setChanged();
		notifyObservers();
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

	/**
	 * Attach Observers story's tasks.
	 */
	public void attachTasksObservers() {
		if (tasks != null) {
			for (final Task task : tasks) {
				task.addObserver(this);
			}
		}
	}

	/**
	 * This is a convenience to update multiple values at once and to notify changes only once, to avoid
	 * views to render multiple times.
	 *
	 * @param task the updated task.
	 */
	public void updateValues(final Story task) {
		this.name = task.getName();
		this.rank = task.getRank();
		this.backlog = task.getBacklog();
		this.iteration = task.getIteration();
		this.tasks = task.getTasks();
		this.metrics = task.getMetrics();
		this.state = task.getState();
		this.responsibles = task.getResponsibles();

		setChanged();
		notifyObservers();
	}

	@Override
	public void update(final Observable observable, final Object data) {
		if (observable instanceof Task) {
			long el = 0;
			long es = 0;
			long oe = 0;

			for (final Task task : tasks) {
				// Agilefant's stories don't consider this states on it's metrics.
				if (task.getState() != StateKey.DEFERRED) {
					oe += task.getOriginalEstimate();
					el += task.getEffortLeft();
				}

				es += task.getEffortSpent();
			}

			this.metrics = new Metrics(el, es, oe);
		}
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
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder responsiblesToStringBuilder = new StringBuilder('[');
		for (final User responsible : responsibles) {
			responsiblesToStringBuilder.append(responsible).append(", ");
		}
		responsiblesToStringBuilder.append(']');

		final StringBuilder tasksToStringBuilder = new StringBuilder('[');
		for (final Task task : tasks) {
			tasksToStringBuilder.append(task).append(", ");
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
			.append(']')
			.toString();
	}
}
