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

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CurrencyCode;
import com.onbelay.dealcapture.common.enums.UnitOfMeasureCode;

public class DealCostDetail  {

	private BigDecimal costValue;
	private String currencyCodeValue;
	private String unitOfMeasureCodeValue;
	private String name;
	private String description; 

	@Transient
	public Price getPerUnitCost() {
		return new Price(
				CurrencyCode.lookUp(currencyCodeValue),
				UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue),
				costValue);
	}
	
	public void setPerUnitCost(Price price) {
		this.costValue = price.getValue();
		this.currencyCodeValue = price.getCurrency().getCode();
		this.unitOfMeasureCodeValue = price.getUnitOfMeasure().getCode();
	}

    @Column(name="COST_PER_UNIT")
    private BigDecimal getCostPerUnitValue() {
		return costValue;
	}


	private void setCostPerUnitValue(BigDecimal costValue) {
		this.costValue = costValue;
	}


    @Column(name="COST_CURRENCY_CODE")
	private String getCurrencyCodeValue() {
		return currencyCodeValue;
	}


	private void setCurrencyCodeValue(String currencyCodeValue) {
		this.currencyCodeValue = currencyCodeValue;
	}


    @Column(name="COST_UOM_CODE")
    private String getUnitOfMeasureCodeValue() {
		return unitOfMeasureCodeValue;
	}


    private void setUnitOfMeasureCodeValue(String costUoMValue) {
		this.unitOfMeasureCodeValue = costUoMValue;
	}

	@Column(name = "NAME_TXT")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION_TXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



    public void copyFrom(DealCostDetail copy) {
    	this.costValue = copy.costValue;
    	this.currencyCodeValue = copy.currencyCodeValue;
    	this.unitOfMeasureCodeValue = copy.unitOfMeasureCodeValue;
    	this.name = copy.name;
    	this.description = copy.description;
    }
	
}
