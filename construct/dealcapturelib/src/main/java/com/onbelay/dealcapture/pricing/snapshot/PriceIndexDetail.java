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
import jakarta.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceIndexDetail {

	private String name;
	private String description;
	private String indexTypeValue;
	private String currencyCodeValue;
	private String unitOfMeasureCodeValue;
	private String frequencyCodeValue;
	private String benchSettlementRuleCodeValue;
	private Double volatility;

	public PriceIndexDetail() {
		
	}
	
	@JsonIgnore
	public void setDefaults() {
		benchSettlementRuleCodeValue = BenchSettlementRuleCode.NEVER.getCode();

	}
	
	public void validate() throws OBValidationException {
		
		if (name == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_INDEX_NAME.getCode());
		
		if (indexTypeValue == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_INDEX_TYPE.getCode());
		
		if (benchSettlementRuleCodeValue == null)
			throw new OBValidationException(PricingErrorCode.MISSING_BENCH_SETTLE_RULE.getCode());


	}
	
	public PriceIndexDetail(String name, String description) {
		super();
		this.name = name;
		this.description = description;
		indexTypeValue = IndexType.HUB.getCode();
	}
	
	
	@Transient
	@JsonIgnore
	public IndexType getIndexType() {
		return IndexType.lookUp(indexTypeValue);
	}
	
	public void setIndexType(IndexType type) {
		if (type != null)
			this.indexTypeValue = type.getCode();
		else
			this.indexTypeValue = null;
	}

	@Transient
	@JsonIgnore
	public CurrencyCode getCurrencyCode() {
		return CurrencyCode.lookUp(currencyCodeValue);
	}
	
	public void setCurrencyCode(CurrencyCode code) {
		if (code != null)
			this.currencyCodeValue = code.getCode();
		else 
			this.currencyCodeValue = null;
	}

	@Column(name = "CURRENCY_CODE")
	public String getCurrencyCodeValue() {
		return currencyCodeValue;
	}

	public void setCurrencyCodeValue(String currencyCodeValue) {
		this.currencyCodeValue = currencyCodeValue;
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

	@Transient
	@JsonIgnore
	public UnitOfMeasureCode getUnitOfMeasureCode() {
		return UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
	}
	
	public void setUnitOfMeasureCode(UnitOfMeasureCode code) {
		if (code != null)
			this.unitOfMeasureCodeValue = code.getCode();
		else 
			this.unitOfMeasureCodeValue = null;
	}

	@Column(name = "UNIT_OF_MEASURE_CODE")
	public String getUnitOfMeasureCodeValue() {
		return unitOfMeasureCodeValue;
	}

	public void setUnitOfMeasureCodeValue(String unitOfMeasureCodeValue) {
		this.unitOfMeasureCodeValue = unitOfMeasureCodeValue;
	}

	@Column(name = "INDEX_TYPE_CODE")
	public String getIndexTypeValue() {
		return indexTypeValue;
	}

	public void setIndexTypeValue(String indexTypeValue) {
		this.indexTypeValue = indexTypeValue;
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

	@Column(name = "VOLATILITY_VALUE")
	public Double getVolatility() {
		return volatility;
	}

	public void setVolatility(Double volatility) {
		this.volatility = volatility;
	}

	@Transient
	@JsonIgnore
	public BenchSettlementRuleCode getBenchSettlementRuleCode() {
		return BenchSettlementRuleCode.lookUp(benchSettlementRuleCodeValue);
	}

	public void setBenchSettlementRuleCode(BenchSettlementRuleCode code) {
		this.benchSettlementRuleCodeValue = code.getCode();
	}

	@Column(name = "BENCH_SETTLE_RULE_CODE")
	public String getBenchSettlementRuleCodeValue() {
		return benchSettlementRuleCodeValue;
	}

	public void setBenchSettlementRuleCodeValue(String benchSettlementRuleCodeValue) {
		this.benchSettlementRuleCodeValue = benchSettlementRuleCodeValue;
	}

	public void copyFrom(PriceIndexDetail copy) {
		
		if (copy.name != null)
			this.name = copy.name;
		
		if (copy.frequencyCodeValue != null)
			this.frequencyCodeValue = copy.frequencyCodeValue;

		if (copy.currencyCodeValue != null)
			this.currencyCodeValue = copy.currencyCodeValue;

		if (copy.unitOfMeasureCodeValue != null)
			this.unitOfMeasureCodeValue = copy.unitOfMeasureCodeValue;
		
		if (copy.description != null)
			this.description = copy.description;
		
		if (copy.benchSettlementRuleCodeValue != null)
			this.benchSettlementRuleCodeValue = copy.benchSettlementRuleCodeValue;
		
		if (copy.indexTypeValue != null)
			this.indexTypeValue = copy.getIndexTypeValue();

		if (copy.volatility != null)
			this.volatility = copy.volatility;
	}
	
}
