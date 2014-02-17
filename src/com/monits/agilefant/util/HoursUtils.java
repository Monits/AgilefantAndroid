package com.monits.agilefant.util;

public class HoursUtils {

	private static final int MINUTES_OF_A_HOUR = 60;

	/**
	 * Convert minutes to hours with Agilefant format
	 * @param minutes
	 * @return hours
	 */
	public static String convertMinutesToHours(final long minutes) {
		if (minutes == 0) {
			return "—";
		} else if (minutes % MINUTES_OF_A_HOUR  == 0) {
			return String.valueOf(minutes / MINUTES_OF_A_HOUR) + "h";
		} else {
			return String.valueOf((float)minutes / MINUTES_OF_A_HOUR) + "h";
		}
	}

	/**
	 * Converts the string that represents the hours to it's equivalent value in minutes.
	 *
	 * @param hours the hours to be converted
	 *
	 * @return the minutes.
	 */
	public static long convertHoursStringToMinutes(final String hours) {
		Double ret = 0.0;
		if (hours != null && !hours.equals("")) {
			if (hours.endsWith("h")) {
				ret = Double.valueOf(hours.split("h")[0]);
			} else {
				ret = Double.valueOf(hours);
			}
		}

		ret *= 60;

		return ret.longValue();
	}
}
