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
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.common.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.BuySellType;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatus;

public class DealDetail {

	private String dealStatusValue;
	private String buySellTypeValue;
	private String ticketNo;
	private LocalDate startDate;
	private LocalDate endDate;
	private BigDecimal volumeQuantity;
	private String volumeUnitOfMeasureValue;
	
	public DealDetail() {
		
	}
	
	public void validate() throws OBValidationException {
		
		if (dealStatusValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_STATUS.getCode());
		
		if (buySellTypeValue == null)
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
		
	}
	
	public DealDetail(
			DealStatus status,
			BuySellType buySellType,
			String ticketNo,
			LocalDate startDate,
			LocalDate endDate,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode unitOfMeasure) {
		
		this.dealStatusValue = status.getCode();
		this.buySellTypeValue = buySellType.getCode();
		this.ticketNo = ticketNo;
		this.startDate = startDate;
		this.endDate = endDate;
		this.volumeQuantity = volumeQuantity;
		this.volumeUnitOfMeasureValue = unitOfMeasure.getCode();
		
	}
	
	public void setDealAttributes(
			DealStatus dealStatus,
			BuySellType buySell,
			LocalDate startDate,
			LocalDate endDate,
			Quantity volume) {

		setDealStatus(dealStatus);
		setBuySellType(buySell);
		setStartDate(startDate);
		setEndDate(endDate);
		setVolume(volume);
	}
	
	@Transient
	@JsonIgnore
	public Quantity getVolume() {
		return new Quantity(
				UnitOfMeasureCode.lookUp(volumeUnitOfMeasureValue),
				volumeQuantity);
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

	@Column(name = "VOLUME_UOM_CODE")
	public String getVolumeUnitOfMeasureValue() {
		return volumeUnitOfMeasureValue;
	}

	public void setVolumeUnitOfMeasureValue(String volumeUnitOfMeasureValue) {
		this.volumeUnitOfMeasureValue = volumeUnitOfMeasureValue;
	}

	@Transient
	@JsonIgnore
	public DealStatus getDealStatus() {
		return DealStatus.lookUp(dealStatusValue);
	}
	
	public void setDealStatus(DealStatus status) {
		this.dealStatusValue = status.getCode();
	}
	
	@Column(name = "DEAL_STATUS_CODE")
	public String getDealStatusValue() {
		return dealStatusValue;
	}

	public void setDealStatusValue(String dealStatusValue) {
		this.dealStatusValue = dealStatusValue;
	}

	@Transient
	@JsonIgnore
	public BuySellType getBuySellType() {
		return BuySellType.lookUp(buySellTypeValue);
	}
	
	public void setBuySellType(BuySellType type) {
		buySellTypeValue = type.getCode();
	}
	
	@Column(name = "BUY_SELL_CODE")
	public String getBuySellTypeValue() {
		return buySellTypeValue;
	}
	public void setBuySellTypeValue(String buySellTypeValue) {
		this.buySellTypeValue = buySellTypeValue;
	}
	
	@Column(name = "START_DATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)	
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	
	@Column(name = "END_DATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)	
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
		
		if (copy.buySellTypeValue != null)
			this.buySellTypeValue = copy.buySellTypeValue;
		
		if (copy.volumeQuantity != null)
			this.volumeQuantity = copy.volumeQuantity;
		
		if (copy.volumeUnitOfMeasureValue != null)
			this.volumeUnitOfMeasureValue = copy.volumeUnitOfMeasureValue;
	}
	
}
