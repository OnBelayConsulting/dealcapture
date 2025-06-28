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
package com.onbelay.dealcapture.businesscontact.enums;

import java.util.*;

public enum BusinessContactErrorCode {

    SUCCESS                       	 ("0", "Success: transaction was successful"),
    MISSING_BUSINESS_CONTACT         ("BUSC_OR-E0001", "Error: Missing Business Contact."),
    MISSING_FIRST_NAME               ("BUSC_OR-E0002", "Error: Missing first name."),
    MISSING_LAST_NAME                ("BUSC_OR-E0003", "Error: Missing last name."),
    MISSING_EMAIL                    ("BUSC_OR-E0004", "Error: Missing email name."),
    NON_UNIQUE_EMAIL                 ("BUSC_OR-E0005", "Error: Non-unique email."),

    MISSING_IS_COMPANY_TRADER        ("BUSC_OR-E0006", "Error: Missing is company trader yes or no."),
    MISSING_IS_COUNTERPARTY_TRADER   ("BUSC_OR-E0007", "Error: Missing is counterparty trader yes or no."),
    MISSING_IS_ADMINISTRATOR         ("BUSC_OR-E0008", "Error: Missing is administrator yes or no."),


    SYSTEM_FAILURE                   ("E99999", "Error: Application error has occurred");

    private String code;
    private String description;

    private static final Map<String, BusinessContactErrorCode> lookup
            = new HashMap<String, BusinessContactErrorCode>();

    static {
        for (BusinessContactErrorCode c : EnumSet.allOf(BusinessContactErrorCode.class))
            lookup.put(c.code, c);
    }


    private BusinessContactErrorCode(String code, String description) {
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
        for (BusinessContactErrorCode c : EnumSet.allOf(BusinessContactErrorCode.class))
            list.add(c.getCode() + " : " + c.getDescription());
        return list;
    }

    public String getDescription() {
        return description;
    }

    public static BusinessContactErrorCode lookUp(String code) {
        return lookup.get(code);
    }

}
