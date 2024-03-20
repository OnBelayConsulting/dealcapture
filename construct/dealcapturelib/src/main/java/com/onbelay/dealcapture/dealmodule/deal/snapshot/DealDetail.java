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
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealDetail {

	private String commodityCodeValue;
	private String dealStatusCodeValue;
	private String positionGenerationStatusValue;
	private String positionGenerationIdentifier;
	private LocalDateTime positionGenerationDateTime;
	private String buySellCodeValue;
	private String ticketNo;
	private LocalDate startDate;
	private LocalDate endDate;
	private BigDecimal volumeQuantity;
	private String volumeUnitOfMeasureCodeValue;
	private String reportingCurrencyCodeValue;
	private String settlementCurrencyCodeValue;
	private String costCurrencyCodeValue;

	public DealDetail() {
		
	}


	public void setDefaults() {
		this.setPositionGenerationStatusCode(PositionGenerationStatusCode.NONE);
	}


	public void validate() throws OBValidationException {

		if (commodityCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_COMMODITY_CODE.getCode());

		if (dealStatusCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_DEAL_STATUS.getCode());

		if (positionGenerationStatusValue == null)
			throw new OBValidationException((DealErrorCode.MISSING_POSITION_GEN_STATUS.getCode()));

		
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
		
		if (volumeUnitOfMeasureCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_VOL_UNIT_OF_MEASURE.getCode());

		if (reportingCurrencyCodeValue == null)
			throw new OBValidationException(DealErrorCode.MISSING_REPORTING_CURRENCY.getCode());

	}

	@Transient
	@JsonIgnore
	public CommodityCode getCommodityCode() {
		return CommodityCode.lookUp(commodityCodeValue);
	}

	public void setCommodityCode(CommodityCode code) {
		this.commodityCodeValue = code.getCode();
	}

	@Column(name = "COMMODITY_CODE")
	public String getCommodityCodeValue() {
		return commodityCodeValue;
	}

	public void setCommodityCodeValue(String commodityCodeValue) {
		this.commodityCodeValue = commodityCodeValue;
	}

	@Transient
	@JsonIgnore
	public Quantity getVolume() {
		return new Quantity(
				volumeQuantity,
				UnitOfMeasureCode.lookUp(volumeUnitOfMeasureCodeValue));
	}
	
	public void setVolume(Quantity quantity) {
		this.volumeQuantity = quantity.getValue();
		this.volumeUnitOfMeasureCodeValue = quantity.getUnitOfMeasureCode().getCode();
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
		return UnitOfMeasureCode.lookUp(volumeUnitOfMeasureCodeValue);
	}

	public void setVolumeUnitOfMeasure(UnitOfMeasureCode code) {
		this.volumeUnitOfMeasureCodeValue = code.getCode();
	}

	@Column(name = "VOLUME_UOM_CODE")
	@InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "volumeUnitOfMeasureCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
	public String getVolumeUnitOfMeasureCodeValue() {
		return volumeUnitOfMeasureCodeValue;
	}

	public void setVolumeUnitOfMeasureCodeValue(String volumeUnitOfMeasureCodeValue) {
		this.volumeUnitOfMeasureCodeValue = volumeUnitOfMeasureCodeValue;
	}

	@Transient
	@JsonIgnore
	public CurrencyCode getReportingCurrencyCode() {
		return CurrencyCode.lookUp(reportingCurrencyCodeValue);
	}

	public void setReportingCurrencyCode(CurrencyCode code) {
		this.reportingCurrencyCodeValue = code.getCode();
	}

	@Column(name = "REPORTING_CURRENCY_CODE")
	public String getReportingCurrencyCodeValue() {
		return reportingCurrencyCodeValue;
	}

	public void setReportingCurrencyCodeValue(String reportingCurrencyCodeValue) {
		this.reportingCurrencyCodeValue = reportingCurrencyCodeValue;
	}
	@Transient
	@JsonIgnore
	public CurrencyCode getSettlementCurrencyCode() {
		return CurrencyCode.lookUp(settlementCurrencyCodeValue);
	}

	public void setSettlementCurrencyCode(CurrencyCode code) {
		this.settlementCurrencyCodeValue = code.getCode();
	}

	@Column(name = "SETTLEMENT_CURRENCY_CODE")
	public String getSettlementCurrencyCodeValue() {
		return settlementCurrencyCodeValue;
	}

	public void setSettlementCurrencyCodeValue(String settlementCurrencyCodeValue) {
		this.settlementCurrencyCodeValue = settlementCurrencyCodeValue;
	}

	@Transient
	@JsonIgnore
	public CurrencyCode getCostCurrencyCode() {
		return CurrencyCode.lookUp(costCurrencyCodeValue);
	}

	public void setCostCurrencyCode(CurrencyCode code) {
		this.costCurrencyCodeValue = code.getCode();
	}

	@Column(name = "COST_CURRENCY_CODE")
	public String getCostCurrencyCodeValue() {
		return costCurrencyCodeValue;
	}

	public void setCostCurrencyCodeValue(String costCurrencyCodeValue) {
		this.costCurrencyCodeValue = costCurrencyCodeValue;
	}

	@Transient
	@JsonIgnore
	public DealStatusCode getDealStatus() {
		return DealStatusCode.lookUp(dealStatusCodeValue);
	}
	
	public void setDealStatus(DealStatusCode status) {
		this.dealStatusCodeValue = status.getCode();
	}
	
	@Column(name = "DEAL_STATUS_CODE")
	@InjectCodeLabel(codeFamily = "dealStatusCode", injectedPropertyName = "dealStatusCodeItem")
	@JsonSerialize(using = CodeLabelSerializer.class)
	public String getDealStatusCodeValue() {
		return dealStatusCodeValue;
	}

	public void setDealStatusCodeValue(String dealStatusCodeValue) {
		this.dealStatusCodeValue = dealStatusCodeValue;
	}

	@Transient
	@JsonIgnore
	public PositionGenerationStatusCode getPositionGenerationStatusCode() {
		return PositionGenerationStatusCode.lookUp(positionGenerationStatusValue);
	}

	public void setPositionGenerationStatusCode(PositionGenerationStatusCode code) {
		if (code != null)
			this.positionGenerationStatusValue = code.getCode();
		else
			this.positionGenerationStatusValue = null;
	}

	@Column(name = "POSITION_GENERATION_STATUS_CODE")
	public String getPositionGenerationStatusValue() {
		return positionGenerationStatusValue;
	}

	public void setPositionGenerationStatusValue(String positionGenerationStatusValue) {
		this.positionGenerationStatusValue = positionGenerationStatusValue;
	}

	@Column(name = "POSITION_GENERATION_IDENTIFIER")
	public String getPositionGenerationIdentifier() {
		return positionGenerationIdentifier;
	}

	public void setPositionGenerationIdentifier(String positionGenerationIdentifier) {
		this.positionGenerationIdentifier = positionGenerationIdentifier;
	}

	@Column(name = "POSITION_GENERATION_DATE_TIME")
	public LocalDateTime getPositionGenerationDateTime() {
		return positionGenerationDateTime;
	}

	public void setPositionGenerationDateTime(LocalDateTime positionGenerationDateTime) {
		this.positionGenerationDateTime = positionGenerationDateTime;
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

		if (copy.commodityCodeValue != null)
			this.commodityCodeValue = copy.commodityCodeValue;

		if (copy.dealStatusCodeValue != null)
			this.dealStatusCodeValue = copy.dealStatusCodeValue;

		if (copy.positionGenerationStatusValue != null)
			this.positionGenerationStatusValue = copy.positionGenerationStatusValue;

		if (copy.positionGenerationIdentifier != null)
			this.positionGenerationIdentifier = copy.positionGenerationIdentifier;

		if (copy.positionGenerationDateTime != null)
			this.positionGenerationDateTime = copy.positionGenerationDateTime;

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
		
		if (copy.volumeUnitOfMeasureCodeValue != null)
			this.volumeUnitOfMeasureCodeValue = copy.volumeUnitOfMeasureCodeValue;

		if (copy.reportingCurrencyCodeValue != null)
			this.reportingCurrencyCodeValue = copy.reportingCurrencyCodeValue;

		if (copy.settlementCurrencyCodeValue != null)
			this.settlementCurrencyCodeValue = copy.settlementCurrencyCodeValue;

		if (copy.costCurrencyCodeValue != null)
			this.costCurrencyCodeValue = copy.costCurrencyCodeValue;
	}
}
