package com.monits.agilefant.util;

import java.util.Comparator;

import com.monits.agilefant.model.Story;

public class StoryRankComparator implements Comparator<Story>{

	@Override
	public int compare(Story story1, Story story2) {
		if (story1.getRank() < story2.getRank()) {
			return -1;
		} else if (story1.getRank() > story2.getRank()) {
			return 1;
		} else {
			return 0;
		}
	}
}
