/*
 Copyright 2019, OnBelay Consulting Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  
 */
package com.onbelay.dealcapture.dealmodule.deal.enums;

import java.util.*;

public enum DealErrorCode {

    INVALID_DEAL_ID                  ("DC_DL-E0001", "Error: Invalid deal id."),
    MISSING_COMPANY_ROLE             ("DC_DL-E0101", "Error: Missing company."),
    MISSING_COUNTERPARTY_ROLE        ("DC_DL-E0102", "Error: Missing counterparty."),
    MISSING_COMMODITY_CODE           ("DC_DL-E0103", "Error: Missing Commodity."),
    MISSING_COMPANY_TRADER           ("DC_DL-E0110", "Error: Missing Commodity."),
    MISSING_COUNTERPARTY_TRADER      ("DC_DL-E0111", "Error: Missing Commodity."),
    MISSING_DEAL_STATUS              ("DC_DL-E0150", "Error: Missing deal status."),
    MISSING_POSITION_GEN_STATUS      ("DC_DL-E0151", "Error: Missing position generation status."),
    MISSING_BUY_SELL                 ("DC_DL-E0155", "Error: Missing BUY SELL."),
    MISSING_TICKET_NO                ("DC_DL-E0156", "Error: Missing ticket no."),
    MISSING_START_DATE               ("DC_DL-E0157", "Error: Missing start date."),
    MISSING_END_DATE                 ("DC_DL-E0158", "Error: Missing end  date."),
    MISSING_VOL_QUANTITY             ("DC_DL-E0159", "Error: Missing volume quantity."),
    MISSING_VOL_UNIT_OF_MEASURE      ("DC_DL-E0160", "Error: Missing volume unit of measure."),
    MISSING_VOL_FREQUENCY            ("DC_DL-E0161", "Error: Missing volume frequency."),
    MISSING_REPORTING_CURRENCY		 ("DC_DL-E0180", "Error: Missing reporting currency."),
    MISSING_SETTLEMENT_CURRENCY		 ("DC_DL-E0181", "Error: Missing settlement currency."),

    MISSING_DEAL_PRICE_VALUATION     ("DC_DL-E0200", "Error: Missing Deal Price Valuation Code."),
    MISSING_MARKET_PRICE_VALUATION   ("DC_DL-E0201", "Error: Missing Market Price Valuation Code."),
    INVALID_DEAL_PRICE_VALUATION     ("DC_DL-E0202", "Error: Invalid Deal Price Valuation Code(Fixed, Index or Index Plus only)."),
    INVALID_MARKET_PRICE_VALUATION   ("DC_DL-E0203", "Error: Invalid Market Price Valuation Code(Index or Power Profile only."),

    INVALID_DEAL_PRICE_INDEX         ("DC_DL-E0300", "Error: Invalid deal price index.Deal is fixed."),
    MISSING_DEAL_PRICE_INDEX         ("DC_DL-E0301", "Error: Missing deal price index.Deal is Indexed."),
    INVALID_DEAL_PRICE_VALUE   		 ("DC_DL-E0302", "Error: Invalid deal price value"),
    MISSING_DEAL_PRICE_VALUE   		 ("DC_DL-E0303", "Error: Missing deal price value"),
    INVALID_DEAL_PRICE_POWER_PROFILE ("DC_DL-E0304", "Error: Power Profile not supported for deal price."),

    INVALID_FIXED_PRICE_VALUE  		 ("DC_DL-E0320", "Error: Invalid fixed price value. Valuation is Index."),
    MISSING_FIXED_PRICE_VALUE  		 ("DC_DL-E0321", "Error: Missing fixed price value, Valuation is Index plus"),

    MISSING_PAYS_PRICE       		 ("DC_DL-E0400", "Error: Missing pays price leg."),
    MISSING_RECEIVES_PRICE     		 ("DC_DL-E0401", "Error: Missing receives price leg."),
    INVALID_RECEIVES_VALUATION 		 ("DC_DL-E0402", "Error: Invalid receives valuation: FLOAT OR POWER PROFILE."),
    INVALID_PAYS_VALUATION   		 ("DC_DL-E0403", "Error: Invalid pays valuation: FLOAT, FLOAT_PLUS OR FIXED."),


    MISSING_MARKET_INDEX      		 ("DC_DL-E0501", "Error: Missing market index"),
    MISSING_MARKET_POWER_PROFILE	 ("DC_DL-E0502", "Error: Missing market Power Profile."),

    MISSED_FIXED_PRICE_CURRENCY      ("DC_DL-E0550", "Error: Fixed price is set and currency is missing."),
    MISSING_FIXED_PRICE_UOM          ("DC_DL-E0551", "Error: Fixed price is set and unit of measure is missing."),

    MISSING_TRADE_TYPE_CODE          ("DC_DL-E0560", "Error: Missing Trade Type Code: OTC or Exchange Traded."),
    MISSING_OPTION_TYPE_CODE         ("DC_DL-E0561", "Error: Missing Option Type Code: Call or Put."),
    MISSING_OPTION_STYLE_CODE        ("DC_DL-E0562", "Error: Missing Trade Type Code: American or European."),
    MISSING_OPTION_EXPIRY_DATE_RULE  ("DC_DL-E0563", "Error: Missing Option Expiry Date Rule."),

    INVALID_STRIKE_PRICE_VALUE   	 ("DC_DL-E0563", "Error: Invalid strike price value. Can not be negative."),
    MISSING_STRIKE_PRICE_VALUE  	 ("DC_DL-E0564", "Error: Missing strike price value."),
    MISSING_STRIKE_PRICE_CURRENCY     ("DC_DL-E0565", "Error: Strike price is set and currency is missing."),
    MISSING_STRIKE_PRICE_UOM         ("DC_DL-E0567", "Error: Strike price is set and unit of measure is missing."),

    INVALID_PREMIUM_PRICE_VALUE   	 ("DC_DL-E0568", "Error: Invalid premium price value. Can not be negative."),
    MISSING_PREMIUM_PRICE_VALUE  	 ("DC_DL-E0569", "Error: Missing premium price value."),
    MISSING_PREMIUM_PRICE_CURRENCY    ("DC_DL-E0570", "Error: Premium price is set and currency is missing."),
    MISSING_PREMIUM_PRICE_UOM        ("DC_DL-E0571", "Error: Premium price is set and unit of measure is missing."),

    MISSING_UNDERLYING_PRICE_IDX     ("DC_DL-E0580", "Error: Missing underlying price index."),


    MISSING_DEAL_COST_TYPE		     ("DC_DL-E0600", "Error: Missing deal cost type."),
    MISSING_DEAL_COST_NAME		     ("DC_DL-E0601", "Error: Missing deal cost name."),
    MISSING_DEAL_COST_CURRENCY	     ("DC_DL-E0602", "Error: Missing deal cost currency."),
    MISSING_DEAL_COST_UOM		     ("DC_DL-E0603", "Error: Missing deal cost unitOfMeasure."),
    MISSING_DEAL_COST_VALUE		     ("DC_DL-E0604", "Error: Missing deal cost value."),
    DUPLICATE_DEAL_COST	    	     ("DC_DL-E0605", "Error: Deal Cost with same dealid, cost name, cost type already exists."),

    MISSING_DEAL_DAY_TYPE		     ("DC_DL-E0700", "Error: Missing deal day type."),
    MISSING_DEAL_DAY_YEAR		     ("DC_DL-E0701", "Error: Missing deal day year."),
    MISSING_DEAL_DAY_MONTH		     ("DC_DL-E0702", "Error: Missing deal day month."),
    DUPLICATE_DEAL_DAY	    	     ("DC_DL-E0703", "Error: Deal Day with same dealid, type, year, month already exists."),
    INVALID_COST_OVERRIDE            ("DC_DL-E0750", "Error: Deal Cost not found for cost override"),
    ;

    private String code;
    private String description;

    private static final Map<String, DealErrorCode> lookup
            = new HashMap<String, DealErrorCode>();

    static {
        for (DealErrorCode c : EnumSet.allOf(DealErrorCode.class))
            lookup.put(c.code, c);
    }


    private DealErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String toString() {
        return code + ":" + description;
    }

    public String getCode() {
        return code;
    }

    public static List<String> getTransactionCodes() {
        ArrayList<String> list = new ArrayList<String>();
        for (DealErrorCode c : EnumSet.allOf(DealErrorCode.class))
            list.add(c.getCode() + " : " + c.getDescription());
        return list;
    }

    public String getDescription() {
        return description;
    }

    public static DealErrorCode lookUp(String code) {
        return lookup.get(code);
    }

}
