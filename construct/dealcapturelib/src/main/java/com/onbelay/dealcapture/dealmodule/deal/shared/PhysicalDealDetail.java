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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CurrencyCode;
import com.onbelay.dealcapture.common.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;

public class PhysicalDealDetail  {

	private BigDecimal dealPriceValue;
	private String dealPriceCurrencyCodeValue;
	private String dealPriceUnitOfMeasureValue;
	
	public PhysicalDealDetail() {
	}
	
	public PhysicalDealDetail(
			BigDecimal dealPrice, 
			CurrencyCode currency, 
			UnitOfMeasureCode unitOfMeasure) {
		
		this.dealPriceValue = dealPrice;
		this.dealPriceCurrencyCodeValue = currency.getCode();
		this.dealPriceUnitOfMeasureValue = unitOfMeasure.getCode();
	}
	
	public void validate() throws OBValidationException {
		
		if (dealPriceValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUE.getCode());
		
		if (dealPriceCurrencyCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_CURRENCY.getCode());
		
		if (dealPriceUnitOfMeasureValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_UOM.getCode());
		
		
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
		this.dealPriceValue = price.getValue();
		this.dealPriceCurrencyCodeValue = price.getCurrency().getCode();
		this.dealPriceUnitOfMeasureValue = price.getUnitOfMeasure().getCode();
	}

    @Column(name="DEAL_PRICE")
    public BigDecimal getDealPriceValue() {
		return dealPriceValue;
	}


    public void setDealPriceValue(BigDecimal dealPriceValue) {
		this.dealPriceValue = dealPriceValue;
	}


    @Column(name="DEAL_PRICE_CURRENCY_CODE")
    public String getDealPriceCurrencyCodeValue() {
		return dealPriceCurrencyCodeValue;
	}


    public void setDealPriceCurrencyCodeValue(String dealPriceCurrencyValue) {
		this.dealPriceCurrencyCodeValue = dealPriceCurrencyValue;
	}


    @Column(name="DEAL_PRICE_UOM_CODE")
    public String getDealPriceUnitOfMeasureValue() {
		return dealPriceUnitOfMeasureValue;
	}


    public void setDealPriceUnitOfMeasureValue(String dealPriceUoMValue) {
		this.dealPriceUnitOfMeasureValue = dealPriceUoMValue;
	}


    public void copyFrom(PhysicalDealDetail copy) {
    	if (copy.dealPriceValue != null)
    		this.dealPriceValue = copy.dealPriceValue;
    	
    	if (copy.dealPriceCurrencyCodeValue != null)
    		this.dealPriceCurrencyCodeValue = copy.dealPriceCurrencyCodeValue;
    	
    	if (copy.dealPriceUnitOfMeasureValue != null)
    		this.dealPriceUnitOfMeasureValue = copy.dealPriceUnitOfMeasureValue;
    }
	
}
