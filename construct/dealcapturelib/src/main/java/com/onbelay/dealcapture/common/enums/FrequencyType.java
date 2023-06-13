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
package com.onbelay.dealcapture.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum FrequencyType {
	HOURLY  ("HOURLY", "HOURLY"),
    DAILY ("DAILY", "DAILY"),
	MONTHLY  ("MONTHLY", "MONTHLY");

	private final String code;
	private final String name;

    private static final Map<String,FrequencyType> lookup 
    	= new HashMap<String,FrequencyType>();

    private static final Map<String,FrequencyType> lookupByName 
        = new HashMap<String,FrequencyType>();

    static {
    	for(FrequencyType c : EnumSet.allOf(FrequencyType.class))
         lookup.put(c.code, c);
        for(FrequencyType c : EnumSet.allOf(FrequencyType.class))
         lookupByName.put(c.name, c);
    }
    
	private FrequencyType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
    public String getName() {
        return name;
    }
    
	public static FrequencyType lookUp(String code) {
		return lookup.get(code);
	}

    public static FrequencyType lookUpByName(String name) {
        return lookupByName.get(name);
    }

}
