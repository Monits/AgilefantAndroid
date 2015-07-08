package com.monits.agilefant.util;

public final class HoursUtils {

	private static final int MINUTES_OF_A_HOUR = 60;
	public static final char HOURS = 'h';
	public static final int MINUTES_IN_HOUR = 60;

	private HoursUtils() {
		throw new AssertionError("Utility classes should not been instantiated");
	}

	/**
	 * Convert minutes to hours with Agilefant format
	 * @param minutes The minutes to be converted
	 * @return The hours
	 */
	public static String convertMinutesToHours(final long minutes) {
		if (minutes == 0) {
			return "—";
		} else if (minutes % MINUTES_OF_A_HOUR  == 0) {
			return String.valueOf(minutes / MINUTES_OF_A_HOUR) + HOURS;
		} else {
			return String.valueOf((float) minutes / MINUTES_OF_A_HOUR) + HOURS;
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
		if (hours != null && !"".equals(hours)) {
			if (hours.endsWith(String.valueOf(HOURS))) {
				ret = Double.valueOf(hours.split(String.valueOf(HOURS))[0]);
			} else {
				ret = Double.valueOf(hours);
			}
		}

		ret *= MINUTES_IN_HOUR;

		return ret.longValue();
	}
}
