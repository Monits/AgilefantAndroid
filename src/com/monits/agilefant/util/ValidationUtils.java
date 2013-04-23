package com.monits.agilefant.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.util.Log;

public class ValidationUtils {

	private ValidationUtils() {
	}

	public static boolean validDate(String pattern, String date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern, Locale.US);

		try {
			dateFormatter.parse(date);

			return true;
		} catch (ParseException e) {
			Log.d("ValidationUtils", "Date validation error", e);

			return false;
		}
	}
}
