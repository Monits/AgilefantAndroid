package com.monits.agilefant.model;

import com.google.gson.annotations.SerializedName;

public class DailyWorkStory extends Story {

	private static final long serialVersionUID = 1429656223672609277L;

	@SerializedName("backlog")
	private Context context;

	@SerializedName("iteration")
	private Iteration iteration;

	/**
	 * @return the iteration
	 */
	public Iteration getIteration() {
		return iteration;
	}

	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(Iteration iteration) {
		this.iteration = iteration;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}
}
