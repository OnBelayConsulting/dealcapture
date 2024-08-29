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
package com.onbelay.dealcapture.dealmodule.positions.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the type of position
 * 
 * @author lefeuvrem
 *
 */
public enum PriceTypeCode {
	FIXED_PRICE 		("FIXED_PRICE"),
	FIXED_QUANTITY 		("FIXED_QUANTITY"),
	PAYS_PRICE 			("PAYS_PRICE"),
	RECEIVES_PRICE 		("RECEIVES_PRICE"),
	DEAL_PRICE 			("DEAL_PRICE"),
	MARKET_PRICE 		("MARKET_PRICE");

	private final String code;

    private static final Map<String, PriceTypeCode> lookup
    	= new HashMap<String, PriceTypeCode>();

    static {
    	for(PriceTypeCode c : EnumSet.allOf(PriceTypeCode.class))
         lookup.put(c.code, c);
    }

	private PriceTypeCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static PriceTypeCode lookUp(String code) {
		return lookup.get(code);
	}

}
