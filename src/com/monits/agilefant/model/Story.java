package com.monits.agilefant.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Story extends Observable implements Parcelable, Observer {

	public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
		@Override
		public Story createFromParcel(final Parcel in) {
			return new Story(in);
		}

		@Override
		public Story[] newArray(final int size) {
			return new Story[size];
		}
	};

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

	public Story() {
	}

	/**
	 * @param id
	 * @param name
	 * @param state
	 * @param responsibles
	 * @param metrics
	 * @param tasks
	 * @param rank
	 * @param backlog
	 * @param iteration
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

	private Story(final Parcel in) {
		this.id = in.readLong();
		this.name = in.readString();
		this.state = (StateKey) in.readSerializable();
		this.responsibles = new LinkedList<User>();
		in.readList(responsibles, User.class.getClassLoader());
		this.metrics = in.readParcelable(Metrics.class.getClassLoader());
		this.tasks = new LinkedList<Task>();
		in.readList(tasks, Task.class.getClassLoader());
		this.rank = in.readInt();
		this.backlog = in.readParcelable(Backlog.class.getClassLoader());
		this.iteration = in.readParcelable(Iteration.class.getClassLoader());
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

	/**
	 * @return the rank
	 */
	public int  getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeSerializable(state);
		dest.writeList(responsibles);
		dest.writeParcelable(metrics, flags);
		dest.writeList(tasks);
		dest.writeInt(rank);
		dest.writeParcelable(backlog, flags);
		dest.writeParcelable(iteration, flags);
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
	public Story clone() {
		return new Story(id, name, state, responsibles, metrics, tasks != null ? new LinkedList<Task>(tasks) : null, rank, backlog, iteration);
	}

	@Override
	public void update(final Observable observable, final Object data) {
		if (observable instanceof Task) {
			long el = 0;
			long es = 0;
			long oe = 0;

			for (final Task task : tasks) {
				// Agilefant's stories don't consider this states on it's metrics.
				if (!task.getState().equals(StateKey.DEFERRED)) {
					oe += task.getOriginalEstimate();
					el += task.getEffortLeft();
				}

				es += task.getEffortSpent();
			}

			this.metrics = new Metrics(el, es, oe);
		}
	}
}