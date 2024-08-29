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
package com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter;


import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.ColumnType;

import java.util.*;

/**
 * Defines the columns for a deal position
 * @author lefeuvrem
 *
 */
public enum PhysicalPositionColumnType {
	DEAL_PRICE_VALUATION_CODE ("DEAL_PRICE_VAL_CODE", ColumnType.STRING),
	DEAL_PRICE ("DEAL_PRICE", ColumnType.BIG_DECIMAL),
	DEAL_INDEX_PRICE ("DEAL_INDEX_PRICE", ColumnType.BIG_DECIMAL),
	TOTAL_DEAL_PRICE ("TOTAL_DEAL_PRICE", ColumnType.BIG_DECIMAL),
	MARKET_VALUATION_CODE ("MARKET_VAL_CODE", ColumnType.STRING),
	MARKET_PRICE ("MARKET_PRICE", ColumnType.BIG_DECIMAL)

	;

	private final String code;
	private ColumnType columnType;

	private static final Map<String, PhysicalPositionColumnType> codeMap
			= new HashMap<>();

	static {
		for(PhysicalPositionColumnType c : EnumSet.allOf(PhysicalPositionColumnType.class))
			codeMap.put(c.code, c);
	}


	public static List<String> getAsList() {
		return Arrays.stream(values()).map(v-> v.code).toList();
	}

	private PhysicalPositionColumnType(String code, ColumnType type) {
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
	public static PhysicalPositionColumnType lookUp(String code) {
		return codeMap.get(code);
	}

	/**
	 * Returns an enum constant by its string representation
	 * of the enum constant's name.
	 * If the name does not exist in the enum, null is returned.
	 */
	public static PhysicalPositionColumnType lookUpByName(String name) {

		if (name == null) {
			return null;
		}

		try {
			return PhysicalPositionColumnType.valueOf(name);
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
	public static PhysicalPositionColumnType lookUpByCodeOrName(String value) {

		PhysicalPositionColumnType enumConstant = lookUp(value);
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

		PhysicalPositionColumnType enumConstant = lookUp(value);
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
