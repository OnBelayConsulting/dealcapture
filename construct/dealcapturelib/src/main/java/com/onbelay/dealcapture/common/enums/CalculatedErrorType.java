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
public enum CalculatedErrorType {
	NO_ERROR  ("NO_ERROR"),
    ERROR ("ERROR"),
	ERROR_INCOMPAT_CURRENCY ("ERR_CURRENCY"),
	ERROR_INCOMPAT_UOM ("ERR_UOM");

	private final String code;

    private static final Map<String,CalculatedErrorType> lookup 
    	= new HashMap<String,CalculatedErrorType>();

    static {
    	for(CalculatedErrorType c : EnumSet.allOf(CalculatedErrorType.class))
         lookup.put(c.code, c);
    }
    
	private CalculatedErrorType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static CalculatedErrorType lookUp(String code) {
		return lookup.get(code);
	}
    

}
