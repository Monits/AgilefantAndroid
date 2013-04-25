package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Story implements Serializable, Parcelable, Observer {

	private static final long serialVersionUID = -3498965706027533482L;

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
	 * @return the state
	 */
	public StateKey getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(StateKey state) {
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
	public void setResponsibles(List<User> responsibles) {
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
	public void setMetrics(Metrics metrics) {
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
	public void setTasks(List<Task> tasks) {
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
	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(this);
	}

	/**
	 * Attach Observers story's tasks.
	 */
	public void attachTasksObservers() {
		if (tasks != null) {
			for (Task task : tasks) {
				task.addObserver(this);
			}
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (observable instanceof Task) {
			long el = 0;
			long es = 0;
			long oe = 0;

			for (Task task : tasks) {
				el += task.getEffortLeft();
				es += task.getEffortSpent();
				oe += task.getOriginalEstimate();
			}

			this.metrics = new Metrics(el, es, oe);
		}
	}
}