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
 * Defines the type of deal.
 * 
 * @author lefeuvrem
 *
 */
public enum DealType {
	ERROR 				("E", "DEAL_IN_ERROR"),
	PHYSICAL_DEAL 		("P", "PHYSICAL_DEAL");

	private final String code;
	private final String name;

    private static final Map<String,DealType> lookup 
    	= new HashMap<String,DealType>();

    private static final Map<String,DealType> lookupByName 
        = new HashMap<String,DealType>();

    static {
    	for(DealType c : EnumSet.allOf(DealType.class))
         lookup.put(c.code, c);
        for(DealType c : EnumSet.allOf(DealType.class))
         lookupByName.put(c.name, c);
    }
    
	private DealType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
    public String getName() {
        return name;
    }
    
	public static DealType lookUp(String code) {
		return lookup.get(code);
	}

    public static DealType lookUpByName(String name) {
        return lookupByName.get(name);
    }
    
    public static DealType lookUpByNameOrCode(String name) {
		DealType rule = DealType.lookUp(name);
		if (rule != null) {
			return rule; 
		} else {
			return lookupByName.get(name);
		}
    }

}
