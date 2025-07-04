package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Transient;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DealPositionViewDetail {


    private LocalDate startDate;
    private LocalDate endDate;
    private String dealTypeCodeValue;
    private String currencyCodeValue;
    private String frequencyCodeValue;
    private LocalDateTime createdDateTime;
    private LocalDateTime valuedDateTime;
    private String volumeUnitOfMeasureValue;
    private BigDecimal volumeQuantityValue;
    private String powerFlowCodeValue;

    private BigDecimal fixedPriceValue;
    private String  fixedPriceCurrencyCodeValue;
    private String  fixedPriceUnitOfMeasureCodeValue;

    private BigDecimal mtmAmountValue;
    private BigDecimal costSettlementAmountValue;
    private BigDecimal settlementAmountValue;
    private BigDecimal totalSettlementAmountValue;

    private String ticketNo;
    private String buySellCodeValue;

    private Boolean isSettlementPosition;
    private String settlementCurrencyCodeValue;
    private String dealUnitOfMeasureCodeValue;


    private String errorCode;
    private String errorMessage;


    @Transient
    @JsonIgnore
    public DealTypeCode getDealTypeCode() {
        return DealTypeCode.lookUp(dealTypeCodeValue);
    }

    public void setDealTypeCode(DealTypeCode code) {
        this.dealTypeCodeValue = code.getCode();
    }

    @Column(name = "DEAL_TYPE_CODE", insertable = false, updatable = false)
    private String getDealTypeCodeValue() {
        return dealTypeCodeValue;
    }

    private void setDealTypeCodeValue(String dealTypeCodeValue) {
        this.dealTypeCodeValue = dealTypeCodeValue;
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

    @Transient
    @JsonIgnore
    public FrequencyCode getFrequencyCode() {
        return FrequencyCode.lookUp(frequencyCodeValue);
    }

    public void setFrequencyCode(FrequencyCode frequencyCode) {
        this.frequencyCodeValue = frequencyCode.getCode();
    }

    @Column(name = "FREQUENCY_CODE")
    public String getFrequencyCodeValue() {
        return frequencyCodeValue;
    }

    public void setFrequencyCodeValue(String frequencyCodeValue) {
        this.frequencyCodeValue = frequencyCodeValue;
    }

    @Transient
    @JsonIgnore
    public PowerFlowCode getPowerFlowCode() {
        return PowerFlowCode.lookUp(powerFlowCodeValue);
    }

    public void setPowerFlowCode(PowerFlowCode powerFlowCode) {
        this.powerFlowCodeValue = powerFlowCode.getCode();
    }

    @Column(name = "POWER_FLOW_CODE")
    public String getPowerFlowCodeValue() {
        return powerFlowCodeValue;
    }

    public void setPowerFlowCodeValue(String powerFlowCodeValue) {
        this.powerFlowCodeValue = powerFlowCodeValue;
    }


    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getDealUnitOfMeasureCode() {
        return UnitOfMeasureCode.lookUp(dealUnitOfMeasureCodeValue);
    }

    @Column(name = "DEAL_UNIT_OF_MEASURE")
    public String getDealUnitOfMeasureCodeValue() {
        return dealUnitOfMeasureCodeValue;
    }

    public void setDealUnitOfMeasureCodeValue(String dealUnitOfMeasureCodeValue) {
        this.dealUnitOfMeasureCodeValue = dealUnitOfMeasureCodeValue;
    }

    @Transient
    public Quantity getQuantity() {
        return new Quantity(
                volumeQuantityValue,
                getVolumeUnitOfMeasure());
    }

    @Column(name = "VOLUME_QUANTITY")
    public BigDecimal getVolumeQuantityValue() {
        return volumeQuantityValue;
    }

    public void setVolumeQuantityValue(BigDecimal volumeQuantityValue) {
        this.volumeQuantityValue = volumeQuantityValue;
    }

    @Column(name = "CREATE_UPDATE_DATETIME")
    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @Column(name = "VALUED_DATETIME")
    public LocalDateTime getValuedDateTime() {
        return valuedDateTime;
    }

    public void setValuedDateTime(LocalDateTime valuedDateTime) {
        this.valuedDateTime = valuedDateTime;
    }

    @Transient
    @JsonIgnore
    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.lookUp(currencyCodeValue);
    }

    public void setCurrencyCode(CurrencyCode code) {
        this.currencyCodeValue = code.getCode();
    }

    @Column(name = "CURRENCY_CODE")
    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public void setCurrencyCodeValue(String currencyCodeValue) {
        this.currencyCodeValue = currencyCodeValue;
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
    public String getVolumeUnitOfMeasureValue() {
        return volumeUnitOfMeasureValue;
    }

    public void setVolumeUnitOfMeasureValue(String volumeUnitOfMeasureValue) {
        this.volumeUnitOfMeasureValue = volumeUnitOfMeasureValue;
    }

    @Column(name = "FIXED_PRICE")
    public BigDecimal getFixedPriceValue() {
        return fixedPriceValue;
    }

    public void setFixedPriceValue(BigDecimal fixedPriceValue) {
        this.fixedPriceValue = fixedPriceValue;
    }


    @Column(name = "FIXED_PRICE_CURRENCY_CODE")
    public String getFixedPriceCurrencyCodeValue() {
        return fixedPriceCurrencyCodeValue;
    }

    public void setFixedPriceCurrencyCodeValue(String fixedPriceCurrencyCodeValue) {
        this.fixedPriceCurrencyCodeValue = fixedPriceCurrencyCodeValue;
    }

    @Column(name = "FIXED_PRICE_UOM_CODE")
    public String getFixedPriceUnitOfMeasureCodeValue() {
        return fixedPriceUnitOfMeasureCodeValue;
    }

    public void setFixedPriceUnitOfMeasureCodeValue(String fixedPriceUnitOfMeasureCodeValue) {
        this.fixedPriceUnitOfMeasureCodeValue = fixedPriceUnitOfMeasureCodeValue;
    }

    @Transient
    @JsonIgnore
    public CurrencyCode getFixedPriceCurrencyCode() {
        return CurrencyCode.lookUp(fixedPriceCurrencyCodeValue);
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getFixedPriceUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(fixedPriceUnitOfMeasureCodeValue);
    }

    @Column(name = "TICKET_NO")
    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    @Transient
    @JsonIgnore
    public BuySellCode getBuySellCode() {
        return BuySellCode.lookUp(buySellCodeValue);
    }

    @Column(name = "BUY_SELL_CODE")
    public String getBuySellCodeValue() {
        return buySellCodeValue;
    }

    public void setBuySellCodeValue(String buySellCodeValue) {
        this.buySellCodeValue = buySellCodeValue;
    }

    @Transient
    @JsonIgnore
    public CurrencyCode getSettlementCurrencyCode() {
        return CurrencyCode.lookUp(settlementCurrencyCodeValue);
    }

    @Column(name = "SETTLEMENT_CURRENCY_CODE")
    public String getSettlementCurrencyCodeValue() {
        return settlementCurrencyCodeValue;
    }

    public void setSettlementCurrencyCodeValue(String settlementCurrencyCodeValue) {
        this.settlementCurrencyCodeValue = settlementCurrencyCodeValue;
    }

    @Column(name = "IS_SETTLEMENT_POSITION")
    @Convert(
            converter = YesNoConverter.class
    )
    public Boolean getIsSettlementPosition() {
        return isSettlementPosition;
    }

    public void setIsSettlementPosition(Boolean settlementPosition) {
        isSettlementPosition = settlementPosition;
    }

    @Column(name = "MTM_VALUATION")
    public BigDecimal getMtmAmountValue() {
        return mtmAmountValue;
    }

    public void setMtmAmountValue(BigDecimal mtmAmountValue) {
        this.mtmAmountValue = mtmAmountValue;
    }

    @Column(name = "COST_SETTLEMENT_AMOUNT")
    public BigDecimal getCostSettlementAmountValue() {
        return costSettlementAmountValue;
    }

    public void setCostSettlementAmountValue(BigDecimal costSettlementAmountValue) {
        this.costSettlementAmountValue = costSettlementAmountValue;
    }

    @Column(name = "SETTLEMENT_AMOUNT")
    public BigDecimal getSettlementAmountValue() {
        return settlementAmountValue;
    }

    public void setSettlementAmountValue(BigDecimal settlementAmountValue) {
        this.settlementAmountValue = settlementAmountValue;
    }

    @Column(name = "TOTAL_SETTLEMENT_AMOUNT")
    public BigDecimal getTotalSettlementAmountValue() {
        return totalSettlementAmountValue;
    }

    public void setTotalSettlementAmountValue(BigDecimal totalSettlementAmountValue) {
        this.totalSettlementAmountValue = totalSettlementAmountValue;
    }

    @Column(name = "ERROR_CODE")
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Column(name = "ERROR_MSG")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
