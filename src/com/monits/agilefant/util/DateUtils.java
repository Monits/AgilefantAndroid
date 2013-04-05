package com.monits.agilefant.util;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class DateUtils {

	/**
	 * Format Date 
	 * @param date Date to format
	 * @param pattern Pattern of date
	 * @return String with the formatted date
	 */
	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
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
}
