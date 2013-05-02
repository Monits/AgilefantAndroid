package com.monits.agilefant.util;

public class InputUtils {

	/**
	 * Parses given string into double.
	 * 
	 * @param string
	 * @return the double.
	 */
	public static double parseStringToDouble(String string) {
		double el = 0;
		if (!string.trim().equals("")) {
			el = Double.valueOf(string.trim());
		}

		return el;
	}
}
