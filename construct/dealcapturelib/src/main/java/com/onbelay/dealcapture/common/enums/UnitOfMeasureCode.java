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
public enum UnitOfMeasureCode {
	GJ  	("GJ", "GigaJoule"),
    MW 		("MW", "MegaWatt"),
	MMBTU 	("MMBTU", "One million British Thermal Units");

	private final String code;
	private final String name;

    private static final Map<String,UnitOfMeasureCode> lookup 
    	= new HashMap<String,UnitOfMeasureCode>();

    private static final Map<String,UnitOfMeasureCode> lookupByName 
        = new HashMap<String,UnitOfMeasureCode>();

    static {
    	for(UnitOfMeasureCode c : EnumSet.allOf(UnitOfMeasureCode.class))
         lookup.put(c.code, c);
        for(UnitOfMeasureCode c : EnumSet.allOf(UnitOfMeasureCode.class))
         lookupByName.put(c.name, c);
    }
    
	private UnitOfMeasureCode(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
    public String getName() {
        return name;
    }
    
	public static UnitOfMeasureCode lookUp(String code) {
		return lookup.get(code);
	}

    public static UnitOfMeasureCode lookUpByName(String name) {
        return lookupByName.get(name);
    }
    
    public static UnitOfMeasureCode lookUpByNameOrCode(String name) {
		UnitOfMeasureCode rule = UnitOfMeasureCode.lookUp(name);
		if (rule != null) {
			return rule; 
		} else {
			return lookupByName.get(name);
		}
    }

}
