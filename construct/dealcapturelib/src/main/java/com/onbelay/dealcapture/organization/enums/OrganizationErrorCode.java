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
package com.onbelay.dealcapture.organization.enums;

import java.util.*;

public enum OrganizationErrorCode {

    SUCCESS                       	 ("0", "Success: transaction was successful"),
    MISSING_ORGANIZATION_ID          ("DC_OR-E0001", "Error: Missing organization id."),
    MISSING_ORGANIZATION_NAME        ("DC_OR-E0002", "Error: Missing organization name."),

    MISSING_CO_IS_HOLDING_PARENT     ("DC_OR-E0200", "Error: Missing CompanyRole: isHoldingParent."),

    MISSING_CP_CURRENCY_AVAIL_CREDIT ("DC_OR-E0300", "Error: Missing either currency or credit value requires both or none"),

    SYSTEM_FAILURE                   ("E99999", "Error: Application error has occurred");

    private String code;
    private String description;

    private static final Map<String, OrganizationErrorCode> lookup
            = new HashMap<String, OrganizationErrorCode>();

    static {
        for (OrganizationErrorCode c : EnumSet.allOf(OrganizationErrorCode.class))
            lookup.put(c.code, c);
    }


    private OrganizationErrorCode(String code, String description) {
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
        for (OrganizationErrorCode c : EnumSet.allOf(OrganizationErrorCode.class))
            list.add(c.getCode() + " : " + c.getDescription());
        return list;
    }

    public String getDescription() {
        return description;
    }

    public static OrganizationErrorCode lookUp(String code) {
        return lookup.get(code);
    }

}
