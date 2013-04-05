package com.monits.agilefant.util;

import java.util.Comparator;

import com.monits.agilefant.model.Task;

public class TaskRankComparator implements Comparator<Task>{

	@Override
	public int compare(Task task1, Task task2) {
		if (task1.getRank() < task2.getRank()) {
			return -1;
		} else if (task1.getRank() > task2.getRank()) {
			return 1;
		} else {
			return 0;
		}
	}
}
