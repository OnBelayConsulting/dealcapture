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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Buy or a sell
 * 
 * @author lefeuvrem
 *
 */
public enum BuySellType {
	BUY 	("B", "BUY"),
    SELL	("S", "SELL");

	private final String code;
	private final String name;

    private static final Map<String,BuySellType> lookup 
    	= new HashMap<String,BuySellType>();

    private static final Map<String,BuySellType> lookupByName 
        = new HashMap<String,BuySellType>();

    static {
    	for(BuySellType c : EnumSet.allOf(BuySellType.class))
         lookup.put(c.code, c);
        for(BuySellType c : EnumSet.allOf(BuySellType.class))
         lookupByName.put(c.name, c);
    }
    
	private BuySellType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
    public String getName() {
        return name;
    }
    
	public static BuySellType lookUp(String code) {
		return lookup.get(code);
	}

    public static BuySellType lookUpByName(String name) {
        return lookupByName.get(name);
    }
    
    public static BuySellType lookUpByNameOrCode(String name) {
		BuySellType rule = BuySellType.lookUp(name);
		if (rule != null) {
			return rule; 
		} else {
			return lookupByName.get(name);
		}
    }

}
