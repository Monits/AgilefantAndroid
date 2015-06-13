package com.monits.agilefant.model.backlog;

import java.util.List;

import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.User;

public class BacklogElementParameters {
	private final Long backlogId;
	private final StateKey stateKey;
	private final List<User> selectedUsers;
	private final String name;
	private final Long iterationId;

	private BacklogElementParameters(final BacklogElementParameters.Builder builder) {
		this.backlogId = builder.backlogId;
		this.stateKey = builder.stateKey;
		this.selectedUsers = builder.selectedUsers;
		this.name = builder.name;
		this.iterationId = builder.iterationId;
	}

	public static class Builder {
		private Long backlogId;
		private StateKey stateKey;
		private List<User> selectedUsers;
		private String name;
		private Long iterationId;

		public Builder backlogId(final Long backlogId) {
			this.backlogId = backlogId;

			return this;
		}

		public Builder stateKey(final StateKey stateKey) {
			this.stateKey = stateKey;

			return this;
		}

		public Builder selectedUsers(final List<User> selectedUsers) {
			this.selectedUsers = selectedUsers;

			return this;
		}

		public Builder iterationId(final Long iterationId) {
			this.iterationId = iterationId;
			return this;
		}

		public Builder name(final String name) {
			this.name = name;

			return this;
		}

		public BacklogElementParameters build() {
			return new BacklogElementParameters(this);
		}
	}

	public Long getBacklogId() {
		return backlogId;
	}

	public StateKey getStateKey() {
		return stateKey;
	}

	public List<User> getSelectedUser() {
		return selectedUsers;
	}

	public String getName() {
		return name;
	}

	public Long getIterationId() {
		return iterationId;
	}
}