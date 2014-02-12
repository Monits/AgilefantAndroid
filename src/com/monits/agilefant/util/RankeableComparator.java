package com.monits.agilefant.util;

import java.util.Comparator;

import com.monits.agilefant.model.Rankeable;

public class RankeableComparator implements Comparator<Rankeable> {

	public static final RankeableComparator INSTANCE = new RankeableComparator();

	/**
	 * Hidding constructor.
	 */
	private RankeableComparator() {
	}

	@Override
	public int compare(final Rankeable lhs, final Rankeable rhs) {
		if (lhs.getRank() < rhs.getRank()) {
			return -1;
		} else if (lhs.getRank() > rhs.getRank()) {
			return 1;
		}

		return 0;
	}
}
