package com.monits.agilefant.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.util.Log;

public final class ValidationUtils {

	private ValidationUtils() {
		throw new AssertionError("Utility classes should not been instantiated");
	}

	/**
	 * Validates a date
	 * @param pattern The pattern to validate the date
	 * @param date The date to validate
	 * @return true if the date matched the pattern
	 */
	public static boolean validDate(final String pattern, final String date) {
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern, Locale.US);

		try {
			dateFormatter.parse(date);

			return true;
		} catch (final ParseException e) {
			Log.d("ValidationUtils", "Date validation error", e);

			return false;
		}
	}

	/**
	 * Validates if there's an empty or null string
	 * @param strings The strign to validate
	 * @return True if any of the given string is null or empty, otherwise false.
	 */
	public static boolean isNullOrEmpty(final String...strings) {
		for (final String string : strings) {
			if (string == null || "".equals(string.trim())) {
				return true;
			}
		}

		return false;
	}
}
