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

public enum PowerProfileErrorCode {

    MISSING_NAME                   ("DC_PP-E0001", "Error: Missing name."),
    INVALID_POWER_PROFILE_CODE     ("DC_PP-E0002", "Error: Invalid power profile code. Not marked as supporting hourly profile."),
    MISSING_POWER_PROFILE_MAPPING  ("DC_PP-E0003", "Error: Profile uses power profile code without price index mapping."),
    INVALID_JOB                 ("DC_PP-E0020", "Error: Job is not a power profile job."),

    ;

    private String code;
    private String description;

    private static final Map<String, PowerProfileErrorCode> lookup
            = new HashMap<String, PowerProfileErrorCode>();

    static {
        for (PowerProfileErrorCode c : EnumSet.allOf(PowerProfileErrorCode.class))
            lookup.put(c.code, c);
    }


    private PowerProfileErrorCode(String code, String description) {
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
        for (PowerProfileErrorCode c : EnumSet.allOf(PowerProfileErrorCode.class))
            list.add(c.getCode() + " : " + c.getDescription());
        return list;
    }

    public String getDescription() {
        return description;
    }

    public static PowerProfileErrorCode lookUp(String code) {
        return lookup.get(code);
    }

}
