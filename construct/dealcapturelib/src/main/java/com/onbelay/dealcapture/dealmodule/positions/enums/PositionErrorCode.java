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

    ERROR_VALUE_POSITION_MISSING_PRICES ("DC_PS-E0001", "Error: Position valuation failed. Missing, invalid prices."),
    MISSING_BASIS_CONTAINER             ("DC_PS-E0100", "Error: Missing basis container in position generation.");

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
