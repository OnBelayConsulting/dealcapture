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
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.shared.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FxIndexDetail {

	private String name;
	private String description;
	private String fromCurrencyCodeValue;
	private String toCurrencyCodeValue;
	private String frequencyValue;
	private Integer daysOffsetForExpiry;

	public FxIndexDetail() {

	}

	@JsonIgnore
	public void setDefaults() {
		frequencyValue = FrequencyCode.MONTHLY.getCode();
	}

	public void validate() throws OBValidationException {

		if (name == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_INDEX_NAME.getCode());

		if (fromCurrencyCodeValue == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_INDEX_TYPE.getCode());

		if (toCurrencyCodeValue == null)
			throw new OBValidationException(PricingErrorCode.MISSING_INDEX_DAYS_EXPIRY.getCode());

	}

	@Transient
	@JsonIgnore
	public CurrencyCode getFromCurrencyCode() {
		return CurrencyCode.lookUp(fromCurrencyCodeValue);
	}
	
	public void setFromCurrencyCode(CurrencyCode code) {
		if (code != null)
			this.fromCurrencyCodeValue = code.getCode();
		else 
			this.fromCurrencyCodeValue = null;
	}

	@Column(name = "FROM_CURRENCY_CODE")
	public String getFromCurrencyCodeValue() {
		return fromCurrencyCodeValue;
	}

	public void setFromCurrencyCodeValue(String fromCurrencyCodeValue) {
		this.fromCurrencyCodeValue = fromCurrencyCodeValue;
	}


	@Transient
	@JsonIgnore
	public CurrencyCode getToCurrencyCode() {
		return CurrencyCode.lookUp(toCurrencyCodeValue);
	}

	public void setToCurrencyCode(CurrencyCode code) {
		if (code != null)
			this.toCurrencyCodeValue = code.getCode();
		else
			this.toCurrencyCodeValue = null;
	}
	@Column(name = "TO_CURRENCY_CODE")
	public String getToCurrencyCodeValue() {
		return toCurrencyCodeValue;
	}

	public void setToCurrencyCodeValue(String toCurrencyCodeValue) {
		this.toCurrencyCodeValue = toCurrencyCodeValue;
	}


	@Transient
	@JsonIgnore
	public FrequencyCode getFrequencyCode() {
		return FrequencyCode.lookUp(frequencyValue);
	}

	public void setFrequencyCode(FrequencyCode code) {
		if (code != null)
			this.frequencyValue = code.getCode();
		else
			this.frequencyValue = null;
	}

	@Column(name = "FREQUENCY_CODE")
	public String getFrequencyValue() {
		return frequencyValue;
	}

	public void setFrequencyValue(String frequencyValue) {
		this.frequencyValue = frequencyValue;
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


	@Column(name = "OFFSET_EXPIRY_DAYS")
	public Integer getDaysOffsetForExpiry() {
		return daysOffsetForExpiry;
	}


	public void setDaysOffsetForExpiry(Integer daysOffsetForExpiry) {
		this.daysOffsetForExpiry = daysOffsetForExpiry;
	}


	public void copyFrom(FxIndexDetail copy) {
		
		if (copy.name != null)
			this.name = copy.name;
		
		if (copy.frequencyValue != null)
			this.frequencyValue = copy.frequencyValue;

		if (copy.toCurrencyCodeValue != null)
			this.toCurrencyCodeValue = copy.toCurrencyCodeValue;

		if (copy.fromCurrencyCodeValue != null)
			this.fromCurrencyCodeValue = copy.fromCurrencyCodeValue;
		
		if (copy.description != null)
			this.description = copy.description;
		
		if (copy.daysOffsetForExpiry != null)
			this.daysOffsetForExpiry = copy.daysOffsetForExpiry;
	}
	
}
