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
package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicalDealDetail  {

	private String dealPriceValuationCodeValue;
	private String marketValuationCodeValue;

	public PhysicalDealDetail() {
	}

	public void setDefaults() {
		dealPriceValuationCodeValue = ValuationCode.FIXED.getCode();
		marketValuationCodeValue = ValuationCode.INDEX.getCode();
	}

	public void validate() throws OBValidationException {

		if (dealPriceValuationCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUATION.getCode());

		if (marketValuationCodeValue == null) {
			throw new OBValidationException(DealErrorCode.MISSING_MARKET_PRICE_VALUATION.getCode());
		}


	}

	@Transient
	@JsonIgnore
	public ValuationCode getDealPriceValuationCode() {
		return ValuationCode.lookUp(dealPriceValuationCodeValue);
	}

	public void setDealPriceValuationCode(ValuationCode code) {
		this.dealPriceValuationCodeValue = code.getCode();
	}

	@Column(name="DEAL_PRICE_VALUATION_CODE")
	public String getDealPriceValuationCodeValue() {
		return dealPriceValuationCodeValue;
	}

	@Transient
	@JsonIgnore
	public ValuationCode getMarketValuationCode() {
		return ValuationCode.lookUp(marketValuationCodeValue);
	}

	public void setMarketValuationCode(ValuationCode code) {
		this.marketValuationCodeValue = code.getCode();
	}

	@Column(name="MARKET_VALUATION_CODE")
	public String getMarketValuationCodeValue() {
		return marketValuationCodeValue;
	}

	public void setMarketValuationCodeValue(String marketValuationCodeValue) {
		this.marketValuationCodeValue = marketValuationCodeValue;
	}

	public void setDealPriceValuationCodeValue(String dealPriceValuationCodeValue) {
		this.dealPriceValuationCodeValue = dealPriceValuationCodeValue;
	}

    public void copyFrom(PhysicalDealDetail copy) {
		if (copy.dealPriceValuationCodeValue != null)
			this.dealPriceValuationCodeValue = copy.dealPriceValuationCodeValue;

		if (copy.marketValuationCodeValue != null)
			this.marketValuationCodeValue = copy.marketValuationCodeValue;
    }
	
}
