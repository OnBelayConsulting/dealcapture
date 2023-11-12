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
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.shared.enums.CurrencyCode;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealCostDetail  {

	private BigDecimal costPerUnitValue;
	private String currencyCodeValue;
	private String unitOfMeasureCodeValue;
	private String name;
	private String description; 

	@Transient
	public Price getCostPerUnit() {
		return new Price(
				costPerUnitValue,
				CurrencyCode.lookUp(currencyCodeValue),
				UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue));
	}
	
	public void setCostPerUnit(Price price) {
		this.costPerUnitValue = price.getValue();
		this.currencyCodeValue = price.getCurrency().getCode();
		this.unitOfMeasureCodeValue = price.getUnitOfMeasure().getCode();
	}

    @Column(name="COST_PER_UNIT")
    public BigDecimal getCostPerUnitValue() {
		return costPerUnitValue;
	}


	public void setCostPerUnitValue(BigDecimal costValue) {
		this.costPerUnitValue = costValue;
	}

	@Transient
	@JsonIgnore
	public CurrencyCode getCurrency() {
		return CurrencyCode.lookUp(currencyCodeValue);
	}

	public void setCurrency(CurrencyCode code) {
		this.currencyCodeValue = code.getCode();
	}

    @Column(name="COST_CURRENCY_CODE")
	@InjectCodeLabel(codeFamily = "currencyCode", injectedPropertyName = "currencyCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
	public String getCurrencyCodeValue() {
		return currencyCodeValue;
	}


	public void setCurrencyCodeValue(String currencyCodeValue) {
		this.currencyCodeValue = currencyCodeValue;
	}


	@Transient
	@JsonIgnore
	public UnitOfMeasureCode getUnitOfMeasure() {
		return UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
	}

	public void setUnitOfMeasure(UnitOfMeasureCode code) {
		this.unitOfMeasureCodeValue = code.getCode();
	}

    @Column(name="COST_UOM_CODE")
	@InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "unitOfMeasureCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
    public String getUnitOfMeasureCodeValue() {
		return unitOfMeasureCodeValue;
	}


    public void setUnitOfMeasureCodeValue(String costUoMValue) {
		this.unitOfMeasureCodeValue = costUoMValue;
	}

	@Column(name = "COST_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "COST_DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



    public void copyFrom(DealCostDetail copy) {
    	this.costPerUnitValue = copy.costPerUnitValue;
    	this.currencyCodeValue = copy.currencyCodeValue;
    	this.unitOfMeasureCodeValue = copy.unitOfMeasureCodeValue;
    	this.name = copy.name;
    	this.description = copy.description;
    }
	
}
