package com.monits.agilefant.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class DateUtils {

	/**
	 * Format Date
	 * @param date Date to format
	 * @param pattern Pattern of date
	 * @return String with the formatted date
	 */
	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
		return simpleDateFormat.format(date);
	}

	/**
	 * Format Date
	 * @param date Date to format
	 * @param pattern Pattern of date
	 * @return String with the formatted date
	 */
	public static String formatDate(long date, String pattern) {
		return formatDate(new Date(date), pattern);
	}

	/**
	 * Parses the date string to the given pattern.
	 * 
	 * @param dateString the string that represents the date in the given pattern.
	 * @param pattern the pattern to be applied to the resulting date.
	 * 
	 * @return the date.
	 */
	public static Date parseDate(String dateString, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);

		try {
			return simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			Log.e("DateUtils", "Couldn't parse the date.", e);
		}

		return null;
	}
}
