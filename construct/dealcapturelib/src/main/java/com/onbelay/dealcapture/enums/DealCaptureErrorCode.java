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
package com.onbelay.dealcapture.enums;

import java.util.*;

public enum DealCaptureErrorCode {

    SUCCESS                       	 ("0", "Success: transaction was successful"),
    SYSTEM_FAILURE                   ("E99999", "Error: Application error has occurred");

    private String code;
    private String description;

    private static final Map<String, DealCaptureErrorCode> lookup
            = new HashMap<String, DealCaptureErrorCode>();

    static {
        for (DealCaptureErrorCode c : EnumSet.allOf(DealCaptureErrorCode.class))
            lookup.put(c.code, c);
    }


    private DealCaptureErrorCode(String code, String description) {
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
        for (DealCaptureErrorCode c : EnumSet.allOf(DealCaptureErrorCode.class))
            list.add(c.getCode() + " : " + c.getDescription());
        return list;
    }

    public String getDescription() {
        return description;
    }

    public static DealCaptureErrorCode lookUp(String code) {
        return lookup.get(code);
    }

}
