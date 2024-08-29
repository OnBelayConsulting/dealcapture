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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.onbelay.core.codes.annotations.CodeLabelSerializer;
import com.onbelay.core.codes.annotations.InjectCodeLabel;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialSwapDealDetail {

	private String paysValuationCodeValue;
	private String receivesValuationCodeValue;

	public FinancialSwapDealDetail() {
	}

	public void setDefaults() {
	}

	public void validate() throws OBValidationException {

		if (paysValuationCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUATION.getCode());

		if  (! (getPaysValuationCode() == ValuationCode.FIXED || getPaysValuationCode() == ValuationCode.INDEX ||
				getPaysValuationCode() == ValuationCode.INDEX_PLUS) )
			throw new OBValidationException(DealErrorCode.INVALID_PAYS_VALUATION.getCode());


		if (receivesValuationCodeValue == null) {
			throw new OBValidationException(DealErrorCode.MISSING_MARKET_PRICE_VALUATION.getCode());
		}

		if  (! (getReceivesValuationCode() == ValuationCode.POWER_PROFILE || getReceivesValuationCode() == ValuationCode.INDEX) )
			throw new OBValidationException(DealErrorCode.INVALID_RECEIVES_VALUATION.getCode());


	}

	@Transient
	@JsonIgnore
	public ValuationCode getPaysValuationCode() {
		return ValuationCode.lookUp(paysValuationCodeValue);
	}

	public void setPaysValuationCode(ValuationCode code) {
		this.paysValuationCodeValue = code.getCode();
	}

	@Column(name="PAYS_VALUATION_CODE")
	public String getPaysValuationCodeValue() {
		return paysValuationCodeValue;
	}

	public void setPaysValuationCodeValue(String paysPriceValuationCodeValue) {
		this.paysValuationCodeValue = paysPriceValuationCodeValue;
	}

	@Transient
	@JsonIgnore
	public ValuationCode getReceivesValuationCode() {
		return ValuationCode.lookUp(receivesValuationCodeValue);
	}

	public void setReceivesValuationCode(ValuationCode code) {
		this.receivesValuationCodeValue = code.getCode();
	}

	@Column(name="RECEIVES_VALUATION_CODE")
	public String getReceivesValuationCodeValue() {
		return receivesValuationCodeValue;
	}

	public void setReceivesValuationCodeValue(String receivesValuationCodeValue) {
		this.receivesValuationCodeValue = receivesValuationCodeValue;
	}

    public void copyFrom(FinancialSwapDealDetail copy) {
		if (copy.paysValuationCodeValue != null)
			this.paysValuationCodeValue = copy.paysValuationCodeValue;

		if (copy.receivesValuationCodeValue != null)
			this.receivesValuationCodeValue = copy.receivesValuationCodeValue;
    }
}
