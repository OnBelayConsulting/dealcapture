/*
 * Copyright (c) 1995-2008 by SAS Institute Inc., Cary, NC, USA.
 * All Rights Reserved
 * This software is protected by copyright laws and international treaties.
 * U.S. GOVERNMENT RESTRICTED RIGHTS
 * Use, duplication, or disclosure of this software and related
 * documentation by the U.S. government is subject to the
 * Agreement with SAS Institute and the restrictions set forth
 * in FAR 52.227-19, Commercial Computer Software
 * Restricted Rights (June 1987).
 */
package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Defines column data files in uploaded files.
 */
public enum ColumnType {
	BIG_DECIMAL ("BigDecimal", ColumnType::convertFromCSVToBigDecimal, ColumnType::convertToCSVFromBigDecimal),
	BOOLEAN 	("Boolean", ColumnType::convertFromCSVToBoolean, ColumnType::convertToCSVFromBoolean),
	PRICE 		("BigDecimal", ColumnType::convertFromCSVToPrice, ColumnType::convertToCSVFromPrice),
	DATE 		("Date", ColumnType::convertFromCSVToDate, ColumnType::convertToCSVFromDate),
	DATE_TIME 	("DateTime", ColumnType::convertFromCSVToDateTime, ColumnType::convertToCSVFromDateTime),
	DATE_TIME_MS ("DateTime(ms)", ColumnType::convertFromCSVToDateTime, ColumnType::convertToCSVFromDateTime),
	INTEGER 	("Integer", ColumnType::convertFromCSVToInteger, ColumnType::convertToCSVFromInteger),
	STRING		("String", ColumnType::convertFromCSVToString, ColumnType::convertToCSVFromString),;

	private final String code;
	private final Function<String, Object> fromCSVConverter;
	private final Function<Object, String> toCSVConverter;

	private static final Map<String, ColumnType> codeMap
			= new HashMap<>();

	static {
		for(ColumnType c : EnumSet.allOf(ColumnType.class))
			codeMap.put(c.code, c);
	}

	private ColumnType(
			String code,
			Function<String, Object> fromCSVConverter,
			Function<Object, String> toCSVConverter) {
		this.code = code;
		this.fromCSVConverter = fromCSVConverter;
		this.toCSVConverter = toCSVConverter;
	}

	public String getCode() {
		return code;
	}

	public Function<String, Object> getFromCSVConverter() {
		return fromCSVConverter;
	}

	public Function<Object, String> getToCSVConverter() {
		return toCSVConverter;
	}

	private static Object convertFromCSVToDate(String s) {
		if (s == null || s.isEmpty())
			return null;
		DateTimeFormatter sdf =  DateTimeFormatter.ISO_DATE;
		try {
			return LocalDate.parse(s, sdf);
		} catch (DateTimeParseException e) {
			throw new RuntimeException(e);
		}
	}


	private static String convertToCSVFromDate(Object in) {
		LocalDate date = (LocalDate) in;
		if (date == null)
			return null;

		return date.toString();
	}


	private static Object convertFromCSVToDateTime(String s) {
		if (s == null || s.isEmpty())
			return null;
		DateTimeFormatter sdf =  DateTimeFormatter.ISO_DATE;
		try {
			return LocalDateTime.parse(s, sdf);
		} catch (DateTimeParseException e) {
			throw new RuntimeException(e);
		}
	}

	private static String convertToCSVFromDateTime(Object in) {
		LocalDateTime dateTime = (LocalDateTime) in;
		if (dateTime == null)
			return null;

		return dateTime.toString();
	}


	private static Object convertFromCSVToInteger(String s) {
		if (s == null || s.isEmpty())
			return null;
		return Integer.parseInt(s);
	}

	private static String convertToCSVFromInteger(Object in) {
		Integer i = (Integer) in;
		return i == null ? null : i.toString();
	}

	private static Object convertFromCSVToString(String s) {
		return s;
	}

	private static String convertToCSVFromString(Object in) {
		String s = (String) in;
		return s == null ? null : s.trim();
	}


	private static Object convertFromCSVToBoolean(String s) {
		return Boolean.getBoolean(s);
	}

	private static String convertToCSVFromBoolean(Object in) {
		boolean b = (Boolean) in;
		return b ? "true" : "false";
	}


	private static Object convertFromCSVToBigDecimal(String s) {
		if (s == null || s.isEmpty())
			return null;
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		DecimalFormat decimalFormat = new DecimalFormat("#,##0", symbols);
		decimalFormat.setParseBigDecimal(true);
		try {
			return (BigDecimal) decimalFormat.parse(s);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private static String convertToCSVFromBigDecimal(Object in) {
		BigDecimal b = (BigDecimal) in;
		b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b == null ? null : b.toPlainString();
	}

	private static Object convertFromCSVToPrice(String s) {
		if (s == null || s.isEmpty())
			return null;
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setCurrencySymbol("$");
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		DecimalFormat decimalFormat = new DecimalFormat("$#,##0", symbols);
		decimalFormat.setParseBigDecimal(true);
		try {
			return (BigDecimal) decimalFormat.parse(s);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}


	private static String convertToCSVFromPrice(Object in) {
		BigDecimal b = (BigDecimal) in;
		b = b.setScale(3, BigDecimal.ROUND_HALF_UP);
		return b == null ? null : b.toPlainString();
	}


	/**
	 * Returns an enum constant by its code representation.
	 * If the code does not exist as a property value of one
	 * of the enum constants, null is returned.
	 */
	public static ColumnType lookUp(String code) {
		return codeMap.get(code);
	}

	/**
	 * Returns an enum constant by its string representation
	 * of the enum constant's name.
	 * If the name does not exist in the enum, null is returned.
	 */
	public static ColumnType lookUpByName(String name) {

		if (name == null) {
			return null;
		}

		try {
			return ColumnType.valueOf(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Returns an enum constant by code or by name. It first
	 * searches by code and if nothing is found, by name.
	 * If neither findByCode or findByName return a value,
	 * then null is returned.
	 */
	public static ColumnType lookUpByCodeOrName(String value) {

		ColumnType enumConstant = lookUp(value);
		if (enumConstant != null) {
			return enumConstant;
		}

		enumConstant = lookUpByName(value);

		if (enumConstant != null) {
			return enumConstant;
		} else {
			return null;
		}
	}

}
