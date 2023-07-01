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
package com.onbelay.dealcapture.dealmodule.deal.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.onbelay.core.codes.annotations.CodeLabelSerializer;
import com.onbelay.core.codes.annotations.InjectCodeLabel;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.CurrencyCode;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicalDealDetail  {

	private String dealPriceValuationCodeValue;
	private BigDecimal dealPriceValue;
	private String dealPriceCurrencyCodeValue;
	private String dealPriceUnitOfMeasureValue;

	public PhysicalDealDetail() {
	}

	public void setDefaults() {
		dealPriceValuationCodeValue = ValuationCode.FIXED.getCode();
	}
	
	public PhysicalDealDetail(
			BigDecimal dealPrice, 
			CurrencyCode currency,
			UnitOfMeasureCode unitOfMeasure) {

		dealPriceValuationCodeValue = ValuationCode.FIXED.getCode();
		this.dealPriceValue = dealPrice;
		this.dealPriceCurrencyCodeValue = currency.getCode();
		this.dealPriceUnitOfMeasureValue = unitOfMeasure.getCode();
	}
	
	public void validate() throws OBValidationException {

		if (dealPriceValuationCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUE.getCode());

		if (getDealPriceValuationCode() == ValuationCode.FIXED) {
			if (dealPriceValue == null)
				throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUE.getCode());

			if (dealPriceCurrencyCodeValue == null)
				throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_CURRENCY.getCode());

			if (dealPriceUnitOfMeasureValue == null)
				throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_UOM.getCode());
		}
		
	}
	
	@Transient
	@JsonIgnore
	public Price getDealPrice() {
		return new Price(
				CurrencyCode.lookUp(dealPriceCurrencyCodeValue),
				UnitOfMeasureCode.lookUp(dealPriceUnitOfMeasureValue),
				dealPriceValue);
	}
	
	public void setDealPrice(Price price) {
		this.dealPriceValuationCodeValue = ValuationCode.FIXED.getCode();
		this.dealPriceValue = price.getValue();
		this.dealPriceCurrencyCodeValue = price.getCurrency().getCode();
		this.dealPriceUnitOfMeasureValue = price.getUnitOfMeasure().getCode();
	}

	@Transient
	@JsonIgnore
	public ValuationCode getDealPriceValuationCode() {
		return ValuationCode.lookUp(dealPriceCurrencyCodeValue);
	}

	public void setDealPriceValuationCode(ValuationCode code) {
		this.dealPriceCurrencyCodeValue = code.getCode();
	}

	@Column(name="DEAL_PRICE_VALUATION_CODE")
	public String getDealPriceValuationCodeValue() {
		return dealPriceValuationCodeValue;
	}

	public void setDealPriceValuationCodeValue(String dealPriceValuationCodeValue) {
		this.dealPriceValuationCodeValue = dealPriceValuationCodeValue;
	}

	@Column(name="DEAL_PRICE")
    public BigDecimal getDealPriceValue() {
		return dealPriceValue;
	}


    public void setDealPriceValue(BigDecimal dealPriceValue) {
		this.dealPriceValue = dealPriceValue;
	}

	@Transient
	@JsonIgnore
	public CurrencyCode getDealPriceCurrency() {
		return CurrencyCode.lookUp(dealPriceCurrencyCodeValue);
	}

	public void setDealPriceCurrency(CurrencyCode code) {
		this.dealPriceCurrencyCodeValue = code.getCode();
	}

    @Column(name="DEAL_PRICE_CURRENCY_CODE")
	@InjectCodeLabel(codeFamily = "currencyCode", injectedPropertyName = "dealPriceCurrencyCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
    public String getDealPriceCurrencyCodeValue() {
		return dealPriceCurrencyCodeValue;
	}


    public void setDealPriceCurrencyCodeValue(String dealPriceCurrencyValue) {
		this.dealPriceCurrencyCodeValue = dealPriceCurrencyValue;
	}

	@Transient
	@JsonIgnore
	public UnitOfMeasureCode getDealPriceUnitOfMeasure() {
		return UnitOfMeasureCode.lookUp(dealPriceUnitOfMeasureValue);
	}

	public void setDealPriceUnitOfMeasure(UnitOfMeasureCode code) {
		this.dealPriceUnitOfMeasureValue = code.getCode();
	}

    @Column(name="DEAL_PRICE_UOM_CODE")
	@InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "dealPriceUnitOfMeasureCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
    public String getDealPriceUnitOfMeasureValue() {
		return dealPriceUnitOfMeasureValue;
	}


    public void setDealPriceUnitOfMeasureValue(String dealPriceUoMValue) {
		this.dealPriceUnitOfMeasureValue = dealPriceUoMValue;
	}


    public void copyFrom(PhysicalDealDetail copy) {
		if (copy.dealPriceValuationCodeValue != null)
			this.dealPriceValuationCodeValue = copy.dealPriceValuationCodeValue;

    	if (copy.dealPriceValue != null)
    		this.dealPriceValue = copy.dealPriceValue;
    	
    	if (copy.dealPriceCurrencyCodeValue != null)
    		this.dealPriceCurrencyCodeValue = copy.dealPriceCurrencyCodeValue;
    	
    	if (copy.dealPriceUnitOfMeasureValue != null)
    		this.dealPriceUnitOfMeasureValue = copy.dealPriceUnitOfMeasureValue;
    }
	
}
