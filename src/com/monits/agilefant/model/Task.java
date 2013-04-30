package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;
import java.util.Observable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Task extends Observable implements Serializable, Parcelable {

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
	private StateKey state;

	@SerializedName("rank")
	private int rank;

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
	public Task(long effortLeft, long effortSpent, long originalEstimate,
			String name, long id, List<User> responsibles, StateKey state,
			int rank) {
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
	public void setEffortLeft(long effortLeft) {
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
	public void setEffortSpent(long effortSpent) {
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
	public void setState(StateKey state) {
		setState(state, false);
	}

	/**
	 * @param state the state to set
	 * @param resetELIfDone whether if effort left should be reset to zero, if {@link StateKey} is done.
	 */
	public void setState(StateKey state, boolean resetELIfDone) {
		this.state = state;

		if (state.equals(StateKey.DONE) && resetELIfDone) {
			this.effortLeft = 0;
		}

		setChanged();
		notifyObservers();
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
		setChanged();
		notifyObservers();
	}

	/**
	 * This is a convenience to update multiple values at once and to notify changes only once, to avoid
	 * views to render multiple times.
	 * 
	 * @param task the updated task.
	 */
	public void updateValues(Task task) {
		this.effortLeft = task.getEffortLeft();
		this.effortSpent = task.getEffortSpent();
		this.originalEstimate = task.getOriginalEstimate();
		this.state = task.getState();
		this.responsibles = task.getResponsibles();
		setChanged();
		notifyObservers();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(this);
	}

	@Override
	public Task clone() {
		return new Task(effortLeft, effortSpent, originalEstimate, name, id, responsibles, state, rank);
	}
}
