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
package com.onbelay.dealcapture.organization.snapshot;

import javax.persistence.Column;

import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.organization.enums.OrganizationErrorCode;

public class CounterpartyDetail extends AbstractDetail {

	private String settlementCurrencyCode;
	private boolean isSettlementCurrencyNull = false;

	
	public CounterpartyDetail() {
	}
	
	
	
	public CounterpartyDetail(String settlementCurrencyCode) {
		this.settlementCurrencyCode = settlementCurrencyCode;
	}


	@Column(name = "SETTLEMENT_CURRENCY_CODE")
	public String getSettlementCurrencyCode() {
		return settlementCurrencyCode;
	}

	public void setSettlementCurrencyCode(String settlementCurrencyCode) {
		this.settlementCurrencyCode = settlementCurrencyCode;
	}

	public void validate() throws OBValidationException {
		
		if (settlementCurrencyCode  == null)
			throw new OBValidationException(OrganizationErrorCode.MISSING_CP_CURRENCY_AVAIL_CREDIT.getCode());
		
	}

	public void copyFrom(CounterpartyDetail copy) {
		if (copy.settlementCurrencyCode != null)
			this.settlementCurrencyCode = copy.settlementCurrencyCode;
	}

}
