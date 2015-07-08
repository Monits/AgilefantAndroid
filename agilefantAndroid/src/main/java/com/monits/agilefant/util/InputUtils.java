package com.monits.agilefant.util;

public final class InputUtils {

	private InputUtils() {
		throw new AssertionError("Utility classes should not been instantiated");
	}

	/**
	 * Parses given string into double.
	 * 
	 * @param string The string to be parsed
	 * @return the parsed double.
	 */
	public static double parseStringToDouble(final String string) {
		final String trim = string.trim();
		return trim.isEmpty() ? 0 : Double.parseDouble(trim);
	}
}
