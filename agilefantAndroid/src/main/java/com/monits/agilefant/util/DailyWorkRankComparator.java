package com.monits.agilefant.util;

import com.monits.agilefant.model.Story;

import java.util.Comparator;

/**
 * Created by edipasquale on 04/11/15.
 */
public final class DailyWorkRankComparator implements Comparator<Story> {

	public static final DailyWorkRankComparator INSTANCE = new DailyWorkRankComparator();

	/**
	 * Hiding constructor
	 */
	private DailyWorkRankComparator() {

	}

	@Override
	public int compare(final Story lhs, final Story rhs) {

		return lhs.getWorkQueueRank() - rhs.getWorkQueueRank();
	}
}
