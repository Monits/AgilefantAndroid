package com.monits.agilefant.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.monits.agilefant.model.backlog.BacklogType;

/**
 * Model of iteration
 * @author Ivan Corbalan
 *
 */
public class Iteration extends Backlog implements Serializable {

	private static final long serialVersionUID = 4014161739613202291L;

	private static final int PRIME = 31;
	private static final int SHIFT = 32;
	private static final int HUNDRED_PERCENT = 100;

	@SerializedName("title")
	private String title;

	@SerializedName("rankedStories")
	private List<Story> stories;

	@SerializedName("startDate")
	private long startDate;

	@SerializedName("endDate")
	private long endDate;

	@SerializedName("tasks")
	private List<Task> tasksWithoutStory;

	@SerializedName("root")
	private RootIteration rootIteration;

	@SerializedName("parent")
	private Backlog parent;

	/**
	 * Default constructor
	 */
	public Iteration() {
		// Default constructor.
	}

	/**
	 * Constructor
	 * @param id The iteration id
	 * @param title The iteration title
	 */
	public Iteration(final long id, final String title) {
		super(id, title);
		this.title = title;
	}

	/**
	 * @return The iteration title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The iteration title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}


	/**
	 * @return the stories
	 */
	public List<Story> getStories() {
		return stories;
	}

	/**
	 * @param stories the stories to set
	 */
	public void setStories(final List<Story> stories) {
		this.stories = stories;
	}

	/**
	 * @return the startDate
	 */
	public long getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(final long startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public long getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(final long endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the tasksWithoutStory
	 */
	public List<Task> getTasksWithoutStory() {
		return tasksWithoutStory;
	}

	/**
	 * @param tasksWithoutStory the tasksWithoutStory to set
	 */
	public void setTasksWithoutStory(final List<Task> tasksWithoutStory) {
		this.tasksWithoutStory = tasksWithoutStory;
	}

	/**
	 * @return the rootIteration
	 */
	public RootIteration getRootIteration() {
		return rootIteration;
	}

	/**
	 * @param rootIteration the rootIteration to set
	 */
	public void setRootIteration(final RootIteration rootIteration) {
		this.rootIteration = rootIteration;
	}

	/**
	 * @return The parent backlog
	 */
	public Backlog getParent() {
		return parent;
	}

	/**
	 * Set the parent backlog
	 * @param parent The backlog to set as a parent
	 */
	public void setParent(final Backlog parent) {
		this.parent = parent;
	}

	/**
	 * @return The effort left in minutes
	 */
	public long getEffortLeft() {
		long effortLeft = 0;
		for (final Story story : stories) {
			effortLeft += story.getMetrics().getEffortLeft();
		}

		return effortLeft;
	}

	/**
	 * @return The original estimate in minutes.
	 */
	public long getOriginalEstimate() {
		long originalEstimate = 0;
		for (final Story story : stories) {
			originalEstimate += story.getMetrics().getOriginalEstimate();
		}

		return originalEstimate;
	}

	/**
	 * @return The effort spent in minutes.
	 */
	public long getEffortSpent() {
		long effortSpent = 0;
		for (final Story story : stories) {
			effortSpent += story.getMetrics().getEffortSpent();
		}

		return effortSpent;
	}

	/**
	 * @return The percentage of completed stories
	 */
	public int getCompletedStoriesPercentage() {
		int completedStories = 0;

		if (!stories.isEmpty()) {
			for (final Story story : stories) {
				if (story.getState() == StateKey.DONE) {
					completedStories++;
				}
			}
		}

		return stories.isEmpty() ? 0 : completedStories * HUNDRED_PERCENT / stories.size();
	}

	/**
	 * @return The percentage of completed tasks
	 */
	public int getCompletedTaskPercentage() {
		int completedTasks = 0;

		int totalTasks = 0;
		if (!stories.isEmpty()) {
			for (final Story story : stories) {
				final List<Task> tasks = story.getTasks();
				completedTasks += getCompletedTaskAmount(story.getTasks());
				totalTasks += tasks.size();
			}
		}

		if (!tasksWithoutStory.isEmpty()) {
			totalTasks += tasksWithoutStory.size();
			for (final Task two : tasksWithoutStory) {
				if (two.getState() == StateKey.DONE) {
					completedTasks++;
				}
			}
		}

		return totalTasks == 0 ? 0 : completedTasks * HUNDRED_PERCENT / totalTasks;
	}

	private int getCompletedTaskAmount(final List<Task> tasks) {
		int completedTasks = 0;
		if (!tasks.isEmpty()) {
			for (final Task two : tasks) {
				if (two.getState() == StateKey.DONE) {
					completedTasks++;
				}
			}
		}

		return completedTasks;
	}

	@Override
	public int hashCode() {
		return PRIME + (int) (getId() ^ (getId() >>> SHIFT));
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final Iteration other = (Iteration) obj;
		return getId() == other.getId();
	}

	@Override
	public BacklogType getType() {
		return BacklogType.ITERATION;
	}

	@Override
	public String toString() {
		final StringBuilder storiesToStringBuilder = new StringBuilder("[");
		if (stories != null && !stories.isEmpty()) {
			for (final Story story : stories) {
				storiesToStringBuilder.append(story).append(", ");
			}
		}
		storiesToStringBuilder.append(']');

		return new StringBuilder("Iteration [title: ").append(title)
			.append(", stories: ").append(storiesToStringBuilder.toString())
			.append(", startDate: ").append(startDate)
			.append(", endDate: ").append(endDate)
			.append(", tasksWithoutStory: ").append(tasksWithoutStory)
			.append(", rootIteration: ").append(rootIteration)
			.append(", parent: ").append(parent)
			.append("] ").append(super.toString())
			.toString();
	}

}
