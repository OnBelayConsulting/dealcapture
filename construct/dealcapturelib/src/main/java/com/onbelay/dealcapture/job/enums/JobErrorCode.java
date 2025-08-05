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
package com.onbelay.dealcapture.job.enums;

import java.util.*;

public enum JobErrorCode {
    SUCCESS                         ("0", "Success"),
    MISSING_DEAL_QUERY_TEXT         ("DC_JB-E0001", "Error: Missing deal query text."),
    MISSING_POSITION_ID             ("DC_JB-E0002", "Error: Missing position generation id."),
    MISSING_FROM_DATE               ("DC_JB-E0003", "Error: Missing from date."),
    MISSING_TO_DATE                 ("DC_JB-E0004", "Error: Missing to date."),
    MISSING_CURRENCY_CODE           ("DC_JB-E0005", "Error: Missing currency code."),
    MISSING_CREATED_DATETIME        ("DC_JB-E0006", "Error: Missing created date/time."),
    MISSING_JOB_STATUS_CODE         ("DC_JB-E0007", "Error: Missing job status code."),
    MISSING_JOB_TYPE_CODE           ("DC_JB-E0008", "Error: Missing job type code."),
    MISSING_VALUATION_DATE_TIME     ("DC_JB-E0100", "Error: Missing valuation date/time."),
    QUEUE_FAILED                    ("DC_JB-E0200", "Error: Request to queue failed. Must be in status pending."),
    COMPLETE_FAILED                 ("DC_JB-E0201", "Error: Request to complete failed. Must be in status of executing."),
    FAIL_FAILED                     ("DC_JB-E0202", "Error: Request to fail - failed. Must be in status of pending, queued or executing."),
    DELETE_FAILED                   ("DC_JB-E0203", "Error: Request to delete failed. Must be in status of failed or cancelled."),
    CANCEL_FAILED                   ("DC_JB-E0204", "Error: Request to cancel failed. Must be in status of pending, queued or executing."),

    ;

    private String code;
    private String description;

    private static final Map<String, JobErrorCode> lookup
            = new HashMap<String, JobErrorCode>();

    static {
        for (JobErrorCode c : EnumSet.allOf(JobErrorCode.class))
            lookup.put(c.code, c);
    }


    private JobErrorCode(String code, String description) {
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
        for (JobErrorCode c : EnumSet.allOf(JobErrorCode.class))
            list.add(c.getCode() + " : " + c.getDescription());
        return list;
    }

    public String getDescription() {
        return description;
    }

    public static JobErrorCode lookUp(String code) {
        return lookup.get(code);
    }

}
