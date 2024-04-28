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
package com.onbelay.dealcapture.dealmodule.positions.enums;

import java.util.*;

public enum PositionErrorCode {
    SUCCESS                                 ("0", "Success"),
    ERROR_VALUE_POSITION_MISSING_PRICES     ("DC_PS-E0001", "Error: Position valuation failed. Missing/Invalid prices."),
    ERROR_VALUE_MISSING_MARKET_PRICE        ("DC_PS-E0002", "Error: Position valuation failed. Missing/Invalid market price."),
    ERROR_VALUE_MTM_DEAL_PRICE              ("DC_PS-E0003", "Error: Position valuation failed. Missing/Invalid deal price for MTM."),
    ERROR_VALUE_MTM_CALCULATION             ("DC_PS-E0005", "Error: Position valuation failed to calculate MtM."),
    ERROR_VALUE_SET_DEAL_PRICE              ("DC_PS-E0020", "Error: Position valuation failed. Missing/Invalid deal price for settlement."),
    ERROR_VALUE_SET_CALCULATION             ("DC_PS-E0021", "Error: Position valuation failed to calculate settlement amount"),
    ERROR_MISSING_FX_RATE_CONVERSION        ("DC_PS-E0022", "Error: Position valuation for risk factor - missing fx rate conversion."),
    ERROR_MISSING_COST_FX_RATE_CONVERSION   ("DC_PS-E0030", "Error: Position valuation missing cost fx rate conversion."),
    ERROR_INVALID_MTM_MISSING_COST          ("DC_PS-E0031", "Error: Position valuation invalid MtM - missing cost."),
    ERROR_INVALID_SETTLE_AMOUNT             ("DC_PS-E0032", "Error: Position valuation invalid settlement amount."),
    ERROR_INVALID_TOTAL_SETTLE_MISSING_COST ("DC_PS-E0033", "Error: Position valuation invalid total settlement amount - missing cost."),
    ERROR_INVALID_POSITION_VALUATION        ("DC_PS-E0040", "Error: Position valuation failed. See Error Messages."),
    MISSING_REQUIRED_EVAL_CONTEXT_FIELDS    ("DC_PS-E0100", "Error: Missing required Evaluation Context fields: CurrencyCode, CreatedDateTime, start date."),
    MISSING_POWER_PROFILE_POSITIONS         ("DC_PS-E0200", "Error: Missing power profile positions for deal position generation."),
    MISSING_RISK_FACTOR_ID_ASSIGNMENT       ("DC_PS-E0300", "Error: RiskFactor is missing or id is not assigned."),
    MISSING_BASIS_CONTAINER                 ("DC_PS-E0600", "Error: Missing basis container in position generation.");

    private String code;
    private String description;

    private static final Map<String, PositionErrorCode> lookup
            = new HashMap<String, PositionErrorCode>();

    static {
        for (PositionErrorCode c : EnumSet.allOf(PositionErrorCode.class))
            lookup.put(c.code, c);
    }


    private PositionErrorCode(String code, String description) {
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
        for (PositionErrorCode c : EnumSet.allOf(PositionErrorCode.class))
            list.add(c.getCode() + " : " + c.getDescription());
        return list;
    }

    public String getDescription() {
        return description;
    }

    public static PositionErrorCode lookUp(String code) {
        return lookup.get(code);
    }

}
