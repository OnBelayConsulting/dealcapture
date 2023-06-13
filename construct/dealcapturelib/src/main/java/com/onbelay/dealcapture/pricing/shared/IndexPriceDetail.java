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
package com.onbelay.dealcapture.pricing.shared;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;

import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;

public class IndexPriceDetail extends AbstractDetail {
	
	private LocalDate priceDate;
	private LocalDateTime observedDateTime;
	private BigDecimal priceValue;
	
	
	public void copyFrom(IndexPriceDetail copy) {
		
		if (copy.priceDate != null)
			this.priceDate = copy.priceDate;
		
		if (copy.observedDateTime != null)
			this.observedDateTime = copy.observedDateTime;
		
		if (copy.priceValue != null)
			this.priceValue = copy.priceValue;
		
	}
	
	public void validate() throws OBValidationException {
		if (priceDate == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_DATE.getCode());
		
		if (observedDateTime == null)
			throw new OBValidationException(PricingErrorCode.MISSING_OBSERVED_DATE_TIME.getCode());
		
		if (priceValue == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICE_VALUE.getCode());
	}
	
	@Column (name = "PRICE_DATE")
	public LocalDate getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(LocalDate priceDate) {
		this.priceDate = priceDate;
	}
	
	@Column (name = "OBSERVED_DATE_TIME")
	public LocalDateTime getObservedDateTime() {
		return observedDateTime;
	}
	public void setObservedDateTime(LocalDateTime observedDateTime) {
		this.observedDateTime = observedDateTime;
	}
	
	@Column (name = "PRICE_VALUE")
	public BigDecimal getPriceValue() {
		return priceValue;
	}
	public void setPriceValue(BigDecimal priceValue) {
		this.priceValue = priceValue;
	}

	

}
