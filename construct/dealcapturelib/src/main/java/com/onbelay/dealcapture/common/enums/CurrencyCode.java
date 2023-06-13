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

/**
 * 
 * @author lefeuvrem
 *
 */
public enum CurrencyCode {
	US  ("USD", "US Dollar"),
    CDN ("CDN", "Canadian Dollar"),
	EUR ("EUR", "EURO");

	private final String code;
	private final String name;

    private static final Map<String,CurrencyCode> lookup 
    	= new HashMap<String,CurrencyCode>();

    private static final Map<String,CurrencyCode> lookupByName 
        = new HashMap<String,CurrencyCode>();

    static {
    	for(CurrencyCode c : EnumSet.allOf(CurrencyCode.class))
         lookup.put(c.code, c);
        for(CurrencyCode c : EnumSet.allOf(CurrencyCode.class))
         lookupByName.put(c.name, c);
    }
    
	private CurrencyCode(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
    public String getName() {
        return name;
    }
    
	public static CurrencyCode lookUp(String code) {
		return lookup.get(code);
	}

    public static CurrencyCode lookUpByName(String name) {
        return lookupByName.get(name);
    }
    
    public static CurrencyCode lookUpByNameOrCode(String name) {
		CurrencyCode rule = CurrencyCode.lookUp(name);
		if (rule != null) {
			return rule; 
		} else {
			return lookupByName.get(name);
		}
    }

}
