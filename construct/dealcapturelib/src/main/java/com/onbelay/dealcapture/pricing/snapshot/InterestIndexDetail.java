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
package com.onbelay.dealcapture.pricing.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.BenchSettlementRuleCode;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Transient;
import org.hibernate.type.YesNoConverter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InterestIndexDetail {

	private String name;
	private String description;
	private String frequencyCodeValue;
	private Boolean isRiskFreeRate;

	public InterestIndexDetail() {

	}

	@JsonIgnore
	public void setDefaults() {
		isRiskFreeRate = false;

	}

	public void validate() throws OBValidationException {

		if (name == null)
			throw new OBValidationException(PricingErrorCode.MISSING_INTEREST_INDEX_NAME.getCode());

		if (frequencyCodeValue == null)
			throw new OBValidationException(PricingErrorCode.MISSING_INTEREST_INDEX_FREQ.getCode());

		if (isRiskFreeRate == null)
			throw new OBValidationException(PricingErrorCode.MISSING_INTEREST_INDEX_IS_RISK_FREE_RATE.getCode());

	}


	@Transient
	@JsonIgnore
	public FrequencyCode getFrequencyCode() {
		return FrequencyCode.lookUp(frequencyCodeValue);
	}

	public void setFrequencyCode(FrequencyCode code) {
		if (code != null)
			this.frequencyCodeValue = code.getCode();
		else
			this.frequencyCodeValue = null;
	}

	@Column(name = "FREQUENCY_CODE")
	public String getFrequencyCodeValue() {
		return frequencyCodeValue;
	}

	public void setFrequencyCodeValue(String frequencyCodeValue) {
		this.frequencyCodeValue = frequencyCodeValue;
	}

	@Column(name = "INDEX_NAME")
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Column(name = "INDEX_DESCRIPTION")
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "IS_RISK_FREE_RATE")
	@Convert(converter = YesNoConverter.class)
	public Boolean getIsRiskFreeRate() {
		return isRiskFreeRate;
	}

	public void setIsRiskFreeRate(Boolean riskFreeRate) {
		isRiskFreeRate = riskFreeRate;
	}

	public void copyFrom(InterestIndexDetail copy) {
		
		if (copy.name != null)
			this.name = copy.name;
		
		if (copy.frequencyCodeValue != null)
			this.frequencyCodeValue = copy.frequencyCodeValue;

		if (copy.isRiskFreeRate != null)
			this.isRiskFreeRate = copy.isRiskFreeRate;

		if (copy.description != null)
			this.description = copy.description;
		
	}
	
}
