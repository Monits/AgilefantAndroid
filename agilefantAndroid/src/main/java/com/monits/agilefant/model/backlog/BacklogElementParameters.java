package com.monits.agilefant.model.backlog;

import java.util.List;

import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.User;

public final class BacklogElementParameters {
	private final Long backlogId;
	private final StateKey stateKey;
	private final List<User> selectedUsers;
	private final String name;
	private final Long iterationId;

	/**
	 * Constructor
	 * @param builder The builder to get the data to construct the object.
	 */
	private BacklogElementParameters(final BacklogElementParameters.Builder builder) {
		this.backlogId = builder.backlogId;
		this.stateKey = builder.stateKey;
		this.selectedUsers = builder.selectedUsers;
		this.name = builder.name;
		this.iterationId = builder.iterationId;
	}

	/**
	 * Builder for the class BacklogElementParameters
	 */
	public static class Builder {
		private Long backlogId;
		private StateKey stateKey;
		private List<User> selectedUsers;
		private String name;
		private Long iterationId;

		/**
		 * Set the backlog id
		 * @param backlogId The backlog id
		 * @return this
		 */
		public Builder backlogId(final Long backlogId) {
			this.backlogId = backlogId;

			return this;
		}

		/**
		 * Set the state key
		 * @param stateKey The state key
		 * @return this
		 */
		public Builder stateKey(final StateKey stateKey) {
			this.stateKey = stateKey;

			return this;
		}

		/**
		 * Set the selected users
		 * @param selectedUsers The selected users
		 * @return this
		 */
		public Builder selectedUsers(final List<User> selectedUsers) {
			this.selectedUsers = selectedUsers;

			return this;
		}

		/**
		 * Set the iteration id
		 * @param iterationId The iteration id
		 * @return this
		 */
		public Builder iterationId(final Long iterationId) {
			this.iterationId = iterationId;
			return this;
		}

		/**
		 * Set the name
		 * @param name The name
		 * @return this
		 */
		public Builder name(final String name) {
			this.name = name;

			return this;
		}

		/**
		 * @return a new BacklogElementParameters
		 */
		public BacklogElementParameters build() {
			return new BacklogElementParameters(this);
		}
	}

	/**
	 * @return the backlog id
	 */
	public Long getBacklogId() {
		return backlogId;
	}

	/**
	 * @return the state key
	 */
	public StateKey getStateKey() {
		return stateKey;
	}

	/**
	 * @return the selected users
	 */
	public List<User> getSelectedUser() {
		return selectedUsers;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the iteration id
	 */
	public Long getIterationId() {
		return iterationId;
	}
}