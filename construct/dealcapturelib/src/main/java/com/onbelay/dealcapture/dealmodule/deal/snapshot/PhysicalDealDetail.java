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
public class PhysicalDealDetail  {

	private String dealPriceValuationCodeValue;
	private String marketValuationCodeValue;
	private BigDecimal fixedPriceValue;
	private String fixedPriceCurrencyCodeValue;
	private String fixedPriceUnitOfMeasureCodeValue;

	public PhysicalDealDetail() {
	}

	public void setDefaults() {
		dealPriceValuationCodeValue = ValuationCode.FIXED.getCode();
		marketValuationCodeValue = ValuationCode.INDEX.getCode();
	}

	public void validate() throws OBValidationException {

		if (dealPriceValuationCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUATION.getCode());

		if (fixedPriceValue != null) {

			if (fixedPriceCurrencyCodeValue == null)
				throw new OBValidationException(DealErrorCode.MISSED_FIXED_PRICE_CURRENCY.getCode());

			if (fixedPriceUnitOfMeasureCodeValue == null)
				throw new OBValidationException(DealErrorCode.MISSING_FIXED_PRICE_UOM.getCode());
		}

		if (marketValuationCodeValue == null) {
			throw new OBValidationException(DealErrorCode.MISSING_MARKET_PRICE_VALUATION.getCode());
		}


	}
	
	@Transient
	@JsonIgnore
	public Price getFixedPrice() {
		if (fixedPriceValue == null)
			return null;
		else
			return new Price(
					fixedPriceValue,
				CurrencyCode.lookUp(fixedPriceCurrencyCodeValue),
				UnitOfMeasureCode.lookUp(fixedPriceUnitOfMeasureCodeValue));
	}
	
	public void setFixedPrice(Price price) {
		this.fixedPriceValue = price.getValue();
		this.fixedPriceCurrencyCodeValue = price.getCurrency().getCode();
		this.fixedPriceUnitOfMeasureCodeValue = price.getUnitOfMeasure().getCode();
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

	@Column(name="FIXED_PRICE")
    public BigDecimal getFixedPriceValue() {
		return fixedPriceValue;
	}


    public void setFixedPriceValue(BigDecimal fixedPriceValue) {
		this.fixedPriceValue = fixedPriceValue;
	}

	@Transient
	@JsonIgnore
	public CurrencyCode getFixedPriceCurrencyCode() {
		return CurrencyCode.lookUp(fixedPriceCurrencyCodeValue);
	}

	public void setFixedPriceCurrencyCode(CurrencyCode code) {
		this.fixedPriceCurrencyCodeValue = code.getCode();
	}

    @Column(name="FIXED_PRICE_CURRENCY_CODE")
	@InjectCodeLabel(codeFamily = "currencyCode", injectedPropertyName = "dealPriceCurrencyCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
    public String getFixedPriceCurrencyCodeValue() {
		return fixedPriceCurrencyCodeValue;
	}


    public void setFixedPriceCurrencyCodeValue(String dealPriceCurrencyValue) {
		this.fixedPriceCurrencyCodeValue = dealPriceCurrencyValue;
	}

	@Transient
	@JsonIgnore
	public UnitOfMeasureCode getFixedPriceUnitOfMeasure() {
		return UnitOfMeasureCode.lookUp(fixedPriceUnitOfMeasureCodeValue);
	}

	public void setFixedPriceUnitOfMeasure(UnitOfMeasureCode code) {
		this.fixedPriceUnitOfMeasureCodeValue = code.getCode();
	}

    @Column(name="FIXED_PRICE_UOM_CODE")
	@InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "dealPriceUnitOfMeasureCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
    public String getFixedPriceUnitOfMeasureCodeValue() {
		return fixedPriceUnitOfMeasureCodeValue;
	}


    public void setFixedPriceUnitOfMeasureCodeValue(String dealPriceUoMValue) {
		this.fixedPriceUnitOfMeasureCodeValue = dealPriceUoMValue;
	}


    public void copyFrom(PhysicalDealDetail copy) {
		if (copy.dealPriceValuationCodeValue != null)
			this.dealPriceValuationCodeValue = copy.dealPriceValuationCodeValue;

		if (copy.marketValuationCodeValue != null)
			this.marketValuationCodeValue = copy.marketValuationCodeValue;

		if (copy.fixedPriceValue != null)
    		this.fixedPriceValue = copy.fixedPriceValue;
    	
    	if (copy.fixedPriceCurrencyCodeValue != null)
    		this.fixedPriceCurrencyCodeValue = copy.fixedPriceCurrencyCodeValue;
    	
    	if (copy.fixedPriceUnitOfMeasureCodeValue != null)
    		this.fixedPriceUnitOfMeasureCodeValue = copy.fixedPriceUnitOfMeasureCodeValue;
    }
	
}
