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
package com.onbelay.dealcapture.pricing.curvesfilereader;


import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.ColumnType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the columns for a curve
 * @author canmxf
 *
 */
public enum CurveColumnType {
	INDEX_NAME ("INDEX_NAME", ColumnType.STRING),
	FREQUENCY_CODE ("FREQUENCY_CODE", ColumnType.STRING),
	CURVE_DATE ("CURVE_DATE", ColumnType.DATE),
	CURVE_DATE_HOUR_ENDING ("HOUR_ENDING", ColumnType.INTEGER),
	OBSERVED_DATE_TIME ("OBSERVED_DATE_TIME", ColumnType.DATE_TIME),
	CURVE_VALUE ("CURVE_VALUE", ColumnType.BIG_DECIMAL),
	;

	private final String code;
	private ColumnType columnType;

	private static final Map<String, CurveColumnType> codeMap
			= new HashMap<>();

	static {
		for(CurveColumnType c : EnumSet.allOf(CurveColumnType.class))
			codeMap.put(c.code, c);
	}

	private static CurveColumnType[] list = CurveColumnType.values();

	public static CurveColumnType[] getAsArray() {
		return list;
	}

	private CurveColumnType(String code, ColumnType type) {
		this.code = code;
		this.columnType = type;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name();
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	/**
	 * Returns an enum constant by its code representation.
	 * If the code does not exist as a property value of one
	 * of the enum constants, null is returned.
	 */
	public static CurveColumnType lookUp(String code) {
		return codeMap.get(code);
	}

	/**
	 * Returns an enum constant by its string representation
	 * of the enum constant's name.
	 * If the name does not exist in the enum, null is returned.
	 */
	public static CurveColumnType lookUpByName(String name) {

		if (name == null) {
			return null;
		}

		try {
			return CurveColumnType.valueOf(name);
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
	public static CurveColumnType lookUpByCodeOrName(String value) {

		CurveColumnType enumConstant = lookUp(value);
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

	/**
	 * Returns a code string by code or by name. It first
	 * searches by code and if nothing is found, by name.
	 * If neither findByCode or findByName return a value,
	 * then null is returned.
	 */
	public static String convertToCode(String value) {

		CurveColumnType enumConstant = lookUp(value);
		if (enumConstant != null) {
			return enumConstant.getCode();
		}

		enumConstant = lookUpByName(value);

		if (enumConstant != null) {
			return enumConstant.getCode();
		} else {
			return null;
		}
	}

}
