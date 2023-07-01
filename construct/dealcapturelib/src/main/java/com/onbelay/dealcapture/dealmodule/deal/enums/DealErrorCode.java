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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DealErrorCode {

    SUCCESS                       	 ("0", "Success: transaction was successful"),
    INVALID_DEAL_ID                  ("DC_DL-E0001", "Error: Invalid deal id."),
    MISSING_COMPANY_ROLE             ("DC_DL-E0101", "Error: Missing company."),
    MISSING_COUNTERPARTY_ROLE        ("DC_DL-E0102", "Error: Missing counterparty."),
    MISSING_DEAL_STATUS              ("DC_DL-E0150", "Error: Missing deal status."),
    MISSING_BUY_SELL                 ("DC_DL-E0155", "Error: Missing BUY SELL."),
    MISSING_TICKET_NO                ("DC_DL-E0156", "Error: Missing ticket no."),
    MISSING_START_DATE               ("DC_DL-E0157", "Error: Missing start date."),
    MISSING_END_DATE                 ("DC_DL-E0158", "Error: Missing end  date."),
    MISSING_VOL_QUANTITY             ("DC_DL-E0159", "Error: Missing volume quantity."),
    MISSING_VOL_UNIT_OF_MEASURE      ("DC_DL-E0160", "Error: Missing volume unit of measure."),

    MISSING_MARKET_INDEX      		 ("DC_DL-E0501", "Error: Missing market index"),
    MISSING_DEAL_PRICE_VALUE   		 ("DC_DL-E0502", "Error: Missing deal price value"),
    MISSING_DEAL_PRICE_CURRENCY		 ("DC_DL-E0503", "Error: Missing deal price currency."),
    MISSING_DEAL_PRICE_UOM		     ("DC_DL-E0504", "Error: Missing deal price unit of measure."),

    SYSTEM_FAILURE                   ("E99999", "Error: Application error has occurred");

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