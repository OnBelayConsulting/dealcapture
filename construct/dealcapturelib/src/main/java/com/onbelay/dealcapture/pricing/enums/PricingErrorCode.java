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
package com.onbelay.dealcapture.pricing.enums;

import java.util.*;

public enum PricingErrorCode {

    MISSING_PRICING_LOCATION        ("DC_PR-E0001", "Error: Missing pricing location."),
    MISSING_PRICING_LOCATION_NAME   ("DC_PR-E0002", "Error: Missing pricing location name."),

    MISSING_PRICE_INDEX_NAME        ("DC_PR-E0005", "Error: Missing pricing index name."),
    MISSING_PRICE_INDEX_TYPE        ("DC_PR-E0006", "Error: Missing pricing index type."),
    MISSING_BENCH_SETTLE_RULE       ("DC_PR-E0007", "Error: Missing Bench settlement rule."),
    MISSING_BASE_INDEX              ("DC_PR-E0010", "Error: Missing base index and Index type is BASIS"),
    MISSING_BENCH_INDEX             ("DC_PR-E0011", "Error: Missing bench index and bench settlement rule is not NEVER."),

    MISSING_FX_INDEX                ("DC_FX-E0001", "Error: Missing FX index."),
    MISSING_FX_INDEX_NAME           ("DC_FX-E0002", "Error: Missing FX index name."),
    MISSING_FX_INDEX_TO_CURRENCY    ("DC_FX-E0003", "Error: Missing FX index to currency."),
    MISSING_FX_INDEX_FROM_CURRENCY  ("DC_FX-E0004", "Error: Missing FX index from currency."),
    MISSING_FX_INDEX_FREQUENCY      ("DC_FX-E0005", "Error: Missing FX index frequency."),
    MISSING_FX_INDEX_DAYS_EXPIRY    ("DC_FX-E0006", "Error: Missing FX index days of expiry."),

    MISSING_CURVE_DATE              ("DC_PR-E0220", "Error: Missing Curve date."),
    MISSING_CURVE_OBS_DATE_TIME     ("DC_PR-E0221", "Error: Missing Curve observed date/time."),
    MISSING_CURVE_VALUE             ("DC_PR-E0222", "Error: Missing Curve value."),
    MISSING_CURVE_FREQUENCY         ("DC_PR-E0223", "Error: Missing Curve Frequency."),

    INVALID_CURVE_FILE_FORMAT       ("DC_PR-E0225", "Error: Invalid Curve file format."),


    MISSING_INTEREST_INDEX          ("DC_PR-E0500", "Error: Missing Interest Index."),
    MISSING_INTEREST_INDEX_NAME     ("DC_PR-E0501", "Error: Missing Interest Index Name."),
    DUPLICATE_INTEREST_INDEX_NAME   ("DC_PR-E0502", "Error: Missing Interest Index Name."),
    MISSING_INTEREST_INDEX_FREQ     ("DC_PR-E0503", "Error: Missing Interest Index frequency code."),
    MISSING_INTEREST_INDEX_IS_RISK_FREE_RATE     ("DC_PR-E0504", "Error: Missing  is risk free rate."),
    INVALID_INTEREST_INDEX_IS_RISK_FREE_RATE     ("DC_PR-E0505", "Error: Only one interest index may be the risk free rate."),

    ;

    private String code;
    private String description;

    private static final Map<String, PricingErrorCode> lookup
            = new HashMap<String, PricingErrorCode>();

    static {
        for (PricingErrorCode c : EnumSet.allOf(PricingErrorCode.class))
            lookup.put(c.code, c);
    }


    private PricingErrorCode(String code, String description) {
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
        for (PricingErrorCode c : EnumSet.allOf(PricingErrorCode.class))
            list.add(c.getCode() + " : " + c.getDescription());
        return list;
    }

    public String getDescription() {
        return description;
    }

    public static PricingErrorCode lookUp(String code) {
        return lookup.get(code);
    }

}
