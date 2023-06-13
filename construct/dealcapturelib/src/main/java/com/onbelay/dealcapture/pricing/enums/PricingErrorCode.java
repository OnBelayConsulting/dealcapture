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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PricingErrorCode {

    SUCCESS                       	 ("0", "Success: transaction was successful"),
    MISSING_PRICING_LOCATION         ("DC_OR-E0001", "Error: Missing pricing location."),
    MISSING_PRICING_INDEX_NAME       ("DC_OR-E0002", "Error: Missing pricing index name."),
    MISSING_PRICING_INDEX_TYPE       ("DC_OR-E0003", "Error: Missing pricing index type."),
    MISSING_INDEX_DAYS_EXPIRY        ("DC_OR-E0004", "Error: Missing pricing index days offset for expiry."),
    MISSING_BASE_INDEX               ("DC_OR-E0010", "Error: Missing base index and Index type is BASIS"),

    MISSING_PRICE_DATE               ("DC_OR-E0020", "Error: Missing Price date."),
    MISSING_OBSERVED_DATE_TIME       ("DC_OR-E0021", "Error: Missing Price observed date/time."),
    MISSING_PRICE_VALUE              ("DC_OR-E0022", "Error: Missing Price value."),

    
    MISSING_PRICING_LOCATION_NAME    ("DC_OR-E0030", "Error: Missing pricing location name."),
    SYSTEM_FAILURE                   ("E99999", "Error: Application error has occurred");

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
