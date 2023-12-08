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
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.shared.enums.BuySellCode;

import com.onbelay.shared.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealDetail {

	private String dealStatusValue;
	private String buySellCodeValue;
	private String ticketNo;
	private LocalDate startDate;
	private LocalDate endDate;
	private BigDecimal volumeQuantity;
	private String volumeUnitOfMeasureValue;
	private String reportingCurrencyValue;
	
	public DealDetail() {
		
	}
	
	public void validate() throws OBValidationException {
		
		if (dealStatusValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_STATUS.getCode());
		
		if (buySellCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_BUY_SELL.getCode());
		
		if (ticketNo == null)
			throw new OBValidationException(DealErrorCode.MISSING_TICKET_NO.getCode());
		
		if (startDate == null)
			throw new OBValidationException(DealErrorCode.MISSING_START_DATE.getCode());
		
		if (endDate == null)
			throw new OBValidationException(DealErrorCode.MISSING_END_DATE.getCode());
		
		if (volumeQuantity == null)
			throw new OBValidationException(DealErrorCode.MISSING_VOL_QUANTITY.getCode());
		
		if (volumeUnitOfMeasureValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_VOL_UNIT_OF_MEASURE.getCode());

		if (reportingCurrencyValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_REPORTING_CURRENCY.getCode());

	}
	
	@Transient
	@JsonIgnore
	public Quantity getVolume() {
		return new Quantity(
				volumeQuantity,
				UnitOfMeasureCode.lookUp(volumeUnitOfMeasureValue));
	}
	
	public void setVolume(Quantity quantity) {
		this.volumeQuantity = quantity.getValue();
		this.volumeUnitOfMeasureValue = quantity.getUnitOfMeasureCode().getCode();
	}
	
	
	@Column(name = "VOLUME_QUANTITY")
	public BigDecimal getVolumeQuantity() {
		return volumeQuantity;
	}

	public void setVolumeQuantity(BigDecimal volumeQuantity) {
		this.volumeQuantity = volumeQuantity;
	}

	@Transient
	@JsonIgnore
	public UnitOfMeasureCode getVolumeUnitOfMeasure() {
		return UnitOfMeasureCode.lookUp(volumeUnitOfMeasureValue);
	}

	public void setVolumeUnitOfMeasure(UnitOfMeasureCode code) {
		this.volumeUnitOfMeasureValue = code.getCode();
	}

	@Column(name = "VOLUME_UOM_CODE")
	@InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "volumeUnitOfMeasureCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
	public String getVolumeUnitOfMeasureValue() {
		return volumeUnitOfMeasureValue;
	}

	public void setVolumeUnitOfMeasureValue(String volumeUnitOfMeasureValue) {
		this.volumeUnitOfMeasureValue = volumeUnitOfMeasureValue;
	}

	@Transient
	@JsonIgnore
	public CurrencyCode getReportingCurrencyCode() {
		return CurrencyCode.lookUp(reportingCurrencyValue);
	}

	public void setReportingCurrencyCode(CurrencyCode code) {
		this.reportingCurrencyValue = code.getCode();
	}

	@Column(name = "REPORTING_CURRENCY_CODE")
	public String getReportingCurrencyValue() {
		return reportingCurrencyValue;
	}

	public void setReportingCurrencyValue(String reportingCurrencyValue) {
		this.reportingCurrencyValue = reportingCurrencyValue;
	}

	@Transient
	@JsonIgnore
	public DealStatusCode getDealStatus() {
		return DealStatusCode.lookUp(dealStatusValue);
	}
	
	public void setDealStatus(DealStatusCode status) {
		this.dealStatusValue = status.getCode();
	}
	
	@Column(name = "DEAL_STATUS_CODE")
	@InjectCodeLabel(codeFamily = "dealStatusCode", injectedPropertyName = "dealStatusCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
	public String getDealStatusValue() {
		return dealStatusValue;
	}

	public void setDealStatusValue(String dealStatusValue) {
		this.dealStatusValue = dealStatusValue;
	}

	@Transient
	@JsonIgnore
	public BuySellCode getBuySell() {
		return BuySellCode.lookUp(buySellCodeValue);
	}
	
	public void setBuySell(BuySellCode type) {
		buySellCodeValue = type.getCode();
	}
	
	@Column(name = "BUY_SELL_CODE")
	@InjectCodeLabel(codeFamily = "buySellCode", injectedPropertyName = "buySellCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
	public String getBuySellCodeValue() {
		return buySellCodeValue;
	}
	public void setBuySellCodeValue(String buySellCodeValue) {
		this.buySellCodeValue = buySellCodeValue;
	}
	
	@Column(name = "START_DATE")
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	
	@Column(name = "END_DATE")
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	@Column(name = "TICKET_NO")
	public String getTicketNo() {
		return ticketNo;
	}
	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}
	
	public void copyFrom(DealDetail copy) {
		
		if (copy.dealStatusValue != null)
			this.dealStatusValue = copy.dealStatusValue;
		
		if (copy.endDate != null)
			this.endDate = copy.endDate;
		
		if (copy.startDate != null)
			this.startDate = copy.startDate;
		
		if (copy.ticketNo != null)
			this.ticketNo = copy.ticketNo;
		
		if (copy.buySellCodeValue != null)
			this.buySellCodeValue = copy.buySellCodeValue;
		
		if (copy.volumeQuantity != null)
			this.volumeQuantity = copy.volumeQuantity;
		
		if (copy.volumeUnitOfMeasureValue != null)
			this.volumeUnitOfMeasureValue = copy.volumeUnitOfMeasureValue;

		if (copy.reportingCurrencyValue != null)
			this.reportingCurrencyValue = copy.reportingCurrencyValue;
	}
	
}
