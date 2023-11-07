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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurveDetail extends AbstractDetail {
	
	private LocalDate curveDate;
	private LocalDateTime observedDateTime;
	private BigDecimal curveValue;
	private String frequencyCodeValue;
	
	
	public void copyFrom(CurveDetail copy) {
		
		if (copy.curveDate != null)
			this.curveDate = copy.curveDate;
		
		if (copy.observedDateTime != null)
			this.observedDateTime = copy.observedDateTime;
		
		if (copy.curveValue != null)
			this.curveValue = copy.curveValue;

		if (copy.frequencyCodeValue != null)
			this.frequencyCodeValue = copy.frequencyCodeValue;
		
	}
	
	public void validate() throws OBValidationException {
		if (curveDate == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_DATE.getCode());
		
		if (observedDateTime == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_OBS_DATE_TIME.getCode());
		
		if (curveValue == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_VALUE.getCode());
	}
	
	@Column (name = "CURVE_DATE")
	public LocalDate getCurveDate() {
		return curveDate;
	}
	public void setCurveDate(LocalDate curveDate) {
		this.curveDate = curveDate;
	}
	
	@Column (name = "OBSERVED_DATE_TIME")
	public LocalDateTime getObservedDateTime() {
		return observedDateTime;
	}
	public void setObservedDateTime(LocalDateTime observedDateTime) {
		this.observedDateTime = observedDateTime;
	}
	
	@Column (name = "CURVE_VALUE")
	public BigDecimal getCurveValue() {
		return curveValue;
	}
	public void setCurveValue(BigDecimal curveValue) {
		this.curveValue = curveValue;
	}


	@Column (name = "FREQUENCY_CODE")
	public String getFrequencyCodeValue() {
		return frequencyCodeValue;
	}

	public void setFrequencyCodeValue(String frequencyCodeValue) {
		this.frequencyCodeValue = frequencyCodeValue;
	}
}
