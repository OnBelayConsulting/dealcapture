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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines the type of position
 * 
 * @author lefeuvrem
 *
 */
public enum JobTypeCode {
	DEAL_POS_GENERATION	 		("DealPositionGeneration"),
	DEAL_POS_VALUATION 			("DealPositionValuation"),
	PWR_PROFILE_POS_GENERATION	("PwrProfilePositionGeneration"),
	PWR_PROFILE_POS_VALUATION 	("PwrProfilePositionValuation"),
	PRICE_RF_VALUATION 			("PriceRiskFactorValuation"),
	FX_RF_VALUATION 			("FxRiskFactorValuation"),
	ERROR	 				    ("Error");

	private final String code;

    private static final Map<String, JobTypeCode> lookup
    	= new HashMap<String, JobTypeCode>();

    static {
    	for(JobTypeCode c : EnumSet.allOf(JobTypeCode.class))
         lookup.put(c.code, c);
    }

	private JobTypeCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static JobTypeCode lookUp(String code) {
		return lookup.get(code);
	}

}
