package com.monits.agilefant.util;

import java.util.Comparator;

import com.monits.agilefant.model.Rankable;

public final class RankComparator implements Comparator<Rankable> {

	public static final RankComparator INSTANCE = new RankComparator();

	/**
	 * Hidding constructor.
	 */
	private RankComparator() {
	}

	@Override
	public int compare(final Rankable lhs, final Rankable rhs) {
		if (lhs.getRank() < rhs.getRank()) {
			return -1;
		} else if (lhs.getRank() > rhs.getRank()) {
			return 1;
		}

		return 0;
	}
}
