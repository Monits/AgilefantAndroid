package com.monits.agilefant.util;

public class HoursUltis {

	private static final int MINUTES_OF_A_HOUR = 60;

	/**
	 * Convert minutes to hours with Agilefant format
	 * @param minutes
	 * @return hours
	 */
	public static String convertMinutesToHours(long minutes) {
		if (minutes == 0) {
			return "—";
		} else if (minutes % MINUTES_OF_A_HOUR  == 0) {
			return String.valueOf(minutes / MINUTES_OF_A_HOUR) + "h";
		} else {
			return String.valueOf((float)minutes / MINUTES_OF_A_HOUR) + "h";
		}
	}
}
