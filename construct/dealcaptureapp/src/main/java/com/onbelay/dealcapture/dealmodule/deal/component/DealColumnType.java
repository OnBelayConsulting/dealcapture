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
package com.onbelay.dealcapture.dealmodule.deal.component;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the columns for a fixed price physical deal
 * @author canmxf
 *
 */
public enum DealColumnType {
	COMPANY_NAME ("COMPANY", ColumnType.STRING),
	COUNTERPARTY_NAME ("COUNTERPARTY", ColumnType.STRING),
	COMMODITY ("Commodity", ColumnType.STRING),
	DEAL_STATUS ("DEAL_STATUS", ColumnType.STRING),
	BUY_SELL ("BUY_SELL", ColumnType.STRING),
	TICKET_NO ("TICKET_NO", ColumnType.STRING),
	START_DATE ("START_DATE", ColumnType.DATE),
	END_DATE ("END_DATE", ColumnType.DATE),
	VOL_QUANTITY ("QUANTITY", ColumnType.BIG_DECIMAL),
	VOL_UNIT_OF_MEASURE ("VOL_UOM", ColumnType.STRING),
	VOL_FREQUENCY ("VOL_FREQUENCY", ColumnType.STRING),
	REP_CURRENCY ("REP_CURRENCY", ColumnType.STRING),
	SETTLE_CURRENCY ("SETTLE_CURRENCY", ColumnType.STRING),
	DEAL_PRICE ("DEAL_PRICE", ColumnType.BIG_DECIMAL),
	DEAL_PRICE_UOM ("DEAL_PRICE_UOM", ColumnType.STRING),
	DEAL_PRICE_CURRENCY ("DEAL_PRICE_CURRENCY", ColumnType.STRING),
	MARKET_INDEX_NAME ("MARKET_INDEX", ColumnType.STRING),
	
	;

	private final String code;
	private ColumnType columnType;

	private static final Map<String, DealColumnType> codeMap
			= new HashMap<>();

	static {
		for(DealColumnType c : EnumSet.allOf(DealColumnType.class))
			codeMap.put(c.code, c);
	}

	private static DealColumnType[] list = DealColumnType.values();

	public static DealColumnType[] getAsArray() {
		return list;
	}

	private DealColumnType(String code, ColumnType type) {
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
	public static DealColumnType lookUp(String code) {
		return codeMap.get(code);
	}

	/**
	 * Returns an enum constant by its string representation
	 * of the enum constant's name.
	 * If the name does not exist in the enum, null is returned.
	 */
	public static DealColumnType lookUpByName(String name) {

		if (name == null) {
			return null;
		}

		try {
			return DealColumnType.valueOf(name);
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
	public static DealColumnType lookUpByCodeOrName(String value) {

		DealColumnType enumConstant = lookUp(value);
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

		DealColumnType enumConstant = lookUp(value);
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
