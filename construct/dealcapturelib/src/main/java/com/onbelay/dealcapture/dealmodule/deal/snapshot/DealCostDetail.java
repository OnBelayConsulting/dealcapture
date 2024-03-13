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
import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealCostDetail  {

	private BigDecimal costValue;
	private String currencyCodeValue;
	private String unitOfMeasureCodeValue;
	private String costNameCodeValue;
	private String costTypeCodeValue;

	@Transient
	public Price getCostPerUnit() {
		return new Price(
				costValue,
				CurrencyCode.lookUp(currencyCodeValue),
				UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue));
	}
	
	public void setCostPerUnit(Price price) {
		this.costValue = price.getValue();
		this.currencyCodeValue = price.getCurrency().getCode();
		this.unitOfMeasureCodeValue = price.getUnitOfMeasure().getCode();
	}

    @Column(name="COST_VALUE")
    public BigDecimal getCostValue() {
		return costValue;
	}


	public void setCostValue(BigDecimal costValue) {
		this.costValue = costValue;
	}

	@Transient
	@JsonIgnore
	public CurrencyCode getCurrencyCode() {
		return CurrencyCode.lookUp(currencyCodeValue);
	}

	public void setCurrencyCode(CurrencyCode code) {
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
	public UnitOfMeasureCode getUnitOfMeasureCode() {
		return UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
	}

	public void setUnitOfMeasureCode(UnitOfMeasureCode code) {
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

	@Transient
	@JsonIgnore
	public CostNameCode getCostName() {
		return CostNameCode.lookUp(costNameCodeValue);
	}

	public void setCostName(CostNameCode code) {
		this.costNameCodeValue = code.getCode();
	}

	@Column(name = "COST_NAME_CODE")
	public String getCostNameCodeValue() {
		return costNameCodeValue;
	}

	public void setCostNameCodeValue(String name) {
		this.costNameCodeValue = name;
	}


	@Transient
	@JsonIgnore
	public CostTypeCode getCostType() {
		return CostTypeCode.lookUp(costTypeCodeValue);
	}

	public void setCostType(CostTypeCode code) {
		this.costTypeCodeValue = code.getCode();
	}

	@Column(name = "COST_TYPE_CODE")
	public String getCostTypeCodeValue() {
		return costTypeCodeValue;
	}

	public void setCostTypeCodeValue(String type) {
		this.costTypeCodeValue = type;
	}

	public void validate() throws OBValidationException {
		if (costTypeCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_COST_TYPE.getCode());
		if (costNameCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_COST_NAME.getCode());
		if (costValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_COST_VALUE.getCode());
		if (currencyCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_COST_CURRENCY.getCode());

		if (getCostType() == CostTypeCode.PER_UNIT) {
			if (this.unitOfMeasureCodeValue == null)
				throw new OBValidationException(DealErrorCode.MISSING_DEAL_COST_UOM.getCode());
		}

	}


    public void copyFrom(DealCostDetail copy) {
		if (copy.costValue != null)
    		this.costValue = copy.costValue;

		if (copy.currencyCodeValue != null)
    		this.currencyCodeValue = copy.currencyCodeValue;

		if (copy.unitOfMeasureCodeValue != null)
    		this.unitOfMeasureCodeValue = copy.unitOfMeasureCodeValue;

		if (copy.costNameCodeValue != null)
    		this.costNameCodeValue = copy.costNameCodeValue;

		if (copy.costTypeCodeValue != null)
    		this.costTypeCodeValue = copy.costTypeCodeValue;
    }
	
}
