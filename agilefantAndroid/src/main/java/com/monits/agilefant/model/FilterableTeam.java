package com.monits.agilefant.model;

import java.util.List;

public class FilterableTeam extends UserChooser {

	private String description;

	private List<Long> usersId;

	/**
	 * Default constructor.
	 */
	public FilterableTeam() {
	}

	/**
	 * Constructor.
	 * @param id The id
	 * @param enabled Flag to see if it's enabled
	 * @param matchedString The string that matched the users.
	 * @param name The name
	 * @param usersId The users ids
	 * @param description A description
	 */
	public FilterableTeam(final long id, final boolean enabled, final String matchedString,
			final String name, final List<Long> usersId, final String description) {
		super(id, enabled, matchedString, name);
		this.description = description;
		this.usersId = usersId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}


	/**
	 * @return the usersId
	 */
	public List<Long> getUsersId() {
		return usersId;
	}

	/**
	 * @param usersId the users to set
	 */
	public void setUsersId(final List<Long> usersId) {
		this.usersId = usersId;
	}

	@Override
	public String toString() {
		final StringBuilder usersIdToStringBuilder = new StringBuilder('[');
		for (final Long id : usersId) {
			usersIdToStringBuilder.append(id).append(", ");
		}
		usersIdToStringBuilder.append(']');

		return new StringBuilder("FilterableTeam [description: ").append(description)
				.append(", usersId: ").append(usersIdToStringBuilder.toString()).append(']')
				.toString();
	}
}