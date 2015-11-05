package com.monits.agilefant.util;

import com.monits.agilefant.model.Rankable;

import java.util.List;

/**
 * Created by edipasquale on 05/11/15.
 */
public final class RankUtils {

	// fixme : Those methods need revision. It might not be the best solution

	/**
	 * Hidding constructor
	 */
	private RankUtils() {
		throw new AssertionError("Utility classes should not been instantiated");
	}


	/**
	 * Updates the ranks of the items in the source list with the ones in the fallbacklist.
	 *
	 * @param <T> the class to rank
	 * @param source the list to be updated.
	 * @param fallbackList the list containing the values to be updated with.
	 */
	public static <T extends Rankable<T>> void rollbackRanks(final List<T> source, final List<T> fallbackList) {

		for (final T currentTaskAt : source) {

			final int indexOfFallbackTask = fallbackList.indexOf(currentTaskAt);
			final T fallbackTask = fallbackList.get(indexOfFallbackTask);

			currentTaskAt.setRank(fallbackTask.getRank());
		}
	}

	/**
	 * This method clones the source items into the fallback list, and updates the ranks of the source list.
	 *
	 * @param <T> the class to rank
	 * @param source the original list.
	 * @param copy the list where cloned items will be added.
	 */
	public static <T extends Rankable<T>> void copyAndSetRank(final List<T> source, final List<T> copy) {

		for (int i = 0; i < source.size(); i++) {
			final T itemAt = source.get(i);

			copy.add(itemAt.getCopy());

			// Rank is equal to the index in the list.
			itemAt.setRank(i);
		}
	}
}
