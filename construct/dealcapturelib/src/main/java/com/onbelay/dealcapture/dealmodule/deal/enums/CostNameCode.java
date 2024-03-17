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
 * Defines the name of a COST.
 * 
 * @author lefeuvrem
 *
 */
public enum CostNameCode {
	FACILITY_PER_UNIT_FEE	 	("Facility Fee", CostTypeCode.PER_UNIT),
	FACILITY_FLAT_FEE	 	 	("Facility Flat Fee", CostTypeCode.FIXED),
	TRANSPORTATION_PER_UNIT_FEE	("Transportation Fee", CostTypeCode.PER_UNIT),
	TRANSPORTATION_FLAT_FEE		("Trans Flat Fee", CostTypeCode.FIXED),
	BROKERAGE_PER_UNIT_FEE	 	("Brokerage Fee", CostTypeCode.PER_UNIT),
	BROKERAGE_DAILY_FEE		 	("Brokerage Flat Fee", CostTypeCode.FIXED),
	TOTAL_PER_UNIT_FEE		 	("Total Per Unit Cost", CostTypeCode.PER_UNIT),
	TOTAL_FIXED_FEE		 		("Total Fixed Cost", CostTypeCode.FIXED),
	;

	private final String code;
	private final CostTypeCode costTypeCode;

    private static final Map<String, CostNameCode> lookup
    	= new HashMap<String, CostNameCode>();

    static {
    	for(CostNameCode c : EnumSet.allOf(CostNameCode.class))
         lookup.put(c.code, c);
    }

	private CostNameCode(String code, CostTypeCode costTypeCode) {
		this.code = code;
		this.costTypeCode = costTypeCode;
	}
	
	public String getCode() {
		return code;
	}

	public CostTypeCode getCostTypeCode() {
		return costTypeCode;
	}

	public static CostNameCode lookUp(String code) {
		return lookup.get(code);
	}

}
