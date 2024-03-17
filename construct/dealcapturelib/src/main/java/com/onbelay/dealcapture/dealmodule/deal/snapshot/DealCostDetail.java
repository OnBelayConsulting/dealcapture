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
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealCostDetail  {

	private BigDecimal costValue;
	private String costNameCodeValue;

    @Column(name="COST_VALUE")
    public BigDecimal getCostValue() {
		return costValue;
	}


	public void setCostValue(BigDecimal costValue) {
		this.costValue = costValue;
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
		return getCostName().getCostTypeCode();
	}

	public void validate() throws OBValidationException {
		if (costNameCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_COST_NAME.getCode());
		if (costValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_COST_VALUE.getCode());

	}


    public void copyFrom(DealCostDetail copy) {
		if (copy.costValue != null)
    		this.costValue = copy.costValue;

		if (copy.costNameCodeValue != null)
    		this.costNameCodeValue = copy.costNameCodeValue;
    }
	
}
