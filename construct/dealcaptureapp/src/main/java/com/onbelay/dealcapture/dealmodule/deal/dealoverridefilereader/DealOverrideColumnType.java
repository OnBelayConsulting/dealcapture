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
package com.onbelay.dealcapture.dealmodule.deal.dealoverridefilereader;


import com.onbelay.dealcapture.dealmodule.deal.dealfilereader.ColumnType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the columns for a fixed price physical deal
 * @author canmxf
 *
 */
public enum DealOverrideColumnType {
	TICKET_NO ("TICKET_NO", ColumnType.STRING),
	OVERRIDE_DATE ("OVERRIDE_DATE", ColumnType.DATE),
	HOUR_ENDING ("HOUR_ENDING", ColumnType.INTEGER),
	VALUE ("VALUE", ColumnType.BIG_DECIMAL),
	;

	private final String code;
	private ColumnType columnType;

	private static final Map<String, DealOverrideColumnType> codeMap
			= new HashMap<>();

	static {
		for(DealOverrideColumnType c : EnumSet.allOf(DealOverrideColumnType.class))
			codeMap.put(c.code, c);
	}

	private static DealOverrideColumnType[] list = DealOverrideColumnType.values();

	public static DealOverrideColumnType[] getAsArray() {
		return list;
	}

	private DealOverrideColumnType(String code, ColumnType type) {
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
	public static DealOverrideColumnType lookUp(String code) {
		return codeMap.get(code);
	}

	/**
	 * Returns an enum constant by its string representation
	 * of the enum constant's name.
	 * If the name does not exist in the enum, null is returned.
	 */
	public static DealOverrideColumnType lookUpByName(String name) {

		if (name == null) {
			return null;
		}

		try {
			return DealOverrideColumnType.valueOf(name);
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
	public static DealOverrideColumnType lookUpByCodeOrName(String value) {

		DealOverrideColumnType enumConstant = lookUp(value);
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

		DealOverrideColumnType enumConstant = lookUp(value);
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
