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
 * 
 * @author lefeuvrem
 *
 */
public enum OptionTypeCode {
	CALL 	("Call"),
    PUT		("Put");

	private final String code;

    private static final Map<String, OptionTypeCode> lookup
    	= new HashMap<String, OptionTypeCode>();

    static {
    	for(OptionTypeCode c : EnumSet.allOf(OptionTypeCode.class))
         lookup.put(c.code, c);
    }

	private OptionTypeCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static OptionTypeCode lookUp(String code) {
		return lookup.get(code);
	}
}
