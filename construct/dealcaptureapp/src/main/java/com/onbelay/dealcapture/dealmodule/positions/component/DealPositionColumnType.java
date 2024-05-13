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
package com.onbelay.dealcapture.dealmodule.positions.component;


import com.onbelay.dealcapture.dealmodule.deal.component.ColumnType;

import java.util.*;

/**
 * Defines the columns for a deal position
 * @author lefeuvrem
 *
 */
public enum DealPositionColumnType {
	TICKET_NO ("TICKET_NO", ColumnType.STRING),
	DEAL_TYPE ("DEAL_TYPE", ColumnType.STRING),
	BUY_SELL ("BUY_SELL", ColumnType.STRING),
	START_DATE ("START_DATE", ColumnType.DATE),
	END_DATE ("END_DATE", ColumnType.DATE),
	FREQUENCY ("FREQUENCY", ColumnType.STRING),
	CURRENCY ("CURRENCY", ColumnType.STRING),
	VOL_QUANTITY ("QUANTITY", ColumnType.BIG_DECIMAL),
	VOL_UNIT_OF_MEASURE ("VOL_UOM", ColumnType.STRING),
	POWER_FLOW_CODE ("POWER_FLOW_CODE", ColumnType.STRING),
	CREATED_DATE_TIME ("CREATED_DATE_TIME", ColumnType.DATE_TIME),
	VALUED_DATE_TIME ("VALUED_DATE_TIME", ColumnType.DATE_TIME),
	DEAL_PRICE_VALUATION_CODE ("DEAL_PRICE_VAL_CODE", ColumnType.STRING),
	DEAL_PRICE ("DEAL_PRICE", ColumnType.BIG_DECIMAL),
	DEAL_INDEX_NAME ("DEAL_INDEX_NAME", ColumnType.STRING),
	DEAL_INDEX_PRICE ("DEAL_INDEX_PRICE", ColumnType.BIG_DECIMAL),
	TOTAL_DEAL_PRICE ("TOTAL_DEAL_PRICE", ColumnType.BIG_DECIMAL),
	MARKET_VALUATION_CODE ("MARKET_VAL_CODE", ColumnType.STRING),
	MARKET_INDEX_NAME ("MARKET_INDEX_NAME", ColumnType.STRING),
	MARKET_PRICE ("MARKET_PRICE", ColumnType.BIG_DECIMAL),
	MTM_AMT ("MTM_AMT", ColumnType.BIG_DECIMAL),
	SETTLEMENT_CURRENCY ("SETTLEMENT_CURRENCY", ColumnType.STRING),
	COST_SETTLEMENT_AMT ("COST_SETTLEMENT_AMT", ColumnType.BIG_DECIMAL),
	SETTLEMENT_AMT ("SETTLEMENT_AMT", ColumnType.BIG_DECIMAL),
	TOTAL_SETTLEMENT_AMT ("TOTAL_SETTLEMENT_AMT", ColumnType.BIG_DECIMAL),
	ERROR_CODE ("ERROR_CODE", ColumnType.STRING),
	ERROR_MSG ("ERROR_MSG", ColumnType.STRING),

	;

	private final String code;
	private ColumnType columnType;

	private static final Map<String, DealPositionColumnType> codeMap
			= new HashMap<>();

	static {
		for(DealPositionColumnType c : EnumSet.allOf(DealPositionColumnType.class))
			codeMap.put(c.code, c);
	}


	public static String[] getAsArray() {
		String[] headers = new String[DealPositionColumnType.values().length];
		for (int i = 0; i < headers.length; i++) {
			headers[i] = DealPositionColumnType.values()[i].code;
		}
		return headers;
	}

	private DealPositionColumnType(String code, ColumnType type) {
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
	public static DealPositionColumnType lookUp(String code) {
		return codeMap.get(code);
	}

	/**
	 * Returns an enum constant by its string representation
	 * of the enum constant's name.
	 * If the name does not exist in the enum, null is returned.
	 */
	public static DealPositionColumnType lookUpByName(String name) {

		if (name == null) {
			return null;
		}

		try {
			return DealPositionColumnType.valueOf(name);
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
	public static DealPositionColumnType lookUpByCodeOrName(String value) {

		DealPositionColumnType enumConstant = lookUp(value);
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

		DealPositionColumnType enumConstant = lookUp(value);
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
