package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.onbelay.core.codes.annotations.CodeLabelSerializer;
import com.onbelay.core.codes.annotations.InjectCodeLabel;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Transient;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CostPositionViewDetail {


    private LocalDate   startDate;
    private LocalDate   endDate;
    private String      currencyCodeValue;
    private LocalDateTime createdDateTime;
    private String      unitOfMeasureValue;
    private String      costCurrencyCodeValue;
    private String      costUnitOfMeasureValue;
    private BigDecimal  volumeQuantityValue;
    private String      costNameCodeValue;
    private BigDecimal  costValue;

    private BigDecimal costFxRateValue;
    private Integer    costFxIndexId;

    private Boolean    isSettlementPosition;
    private Boolean    isFixedValued;

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

    @Column(name = "CREATE_UPDATE_DATETIME")
    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @Transient
    public Quantity getQuantity() {
        return new Quantity(
                volumeQuantityValue,
                getUnitOfMeasure());
    }

    @Column(name = "VOLUME_QUANTITY")
    public BigDecimal getVolumeQuantityValue() {
        return volumeQuantityValue;
    }

    public void setVolumeQuantityValue(BigDecimal volumeQuantityValue) {
        this.volumeQuantityValue = volumeQuantityValue;
    }

    @Transient
    @JsonIgnore
    public CostNameCode getCostNameCode() {
        return CostNameCode.lookUp(costNameCodeValue);
    }

    @Column(name = "COST_NAME_CODE")
    public String getCostNameCodeValue() {
        return costNameCodeValue;
    }

    public void setCostNameCodeValue(String costNameCodeValue) {
        this.costNameCodeValue = costNameCodeValue;
    }

    @Column(name = "COST_VALUE")
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

    @Column(name = "CURRENCY_CODE")
    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public void setCurrencyCodeValue(String currencyCodeValue) {
        this.currencyCodeValue = currencyCodeValue;
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(unitOfMeasureValue);
    }

    public void setUnitOfMeasure(UnitOfMeasureCode code) {
        this.unitOfMeasureValue = code.getCode();
    }

    @Column(name = "UNIT_OF_MEASURE_CODE")
    public String getUnitOfMeasureValue() {
        return unitOfMeasureValue;
    }

    public void setUnitOfMeasureValue(String volumeUnitOfMeasureValue) {
        this.unitOfMeasureValue = volumeUnitOfMeasureValue;
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
    public UnitOfMeasureCode getCostUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(costUnitOfMeasureValue);
    }

    public void setCostUnitOfMeasure(UnitOfMeasureCode code) {
        this.costUnitOfMeasureValue = code.getCode();
    }

    @Column(name = "COST_UNIT_OF_MEASURE")
    public String getCostUnitOfMeasureValue() {
        return costUnitOfMeasureValue;
    }

    public void setCostUnitOfMeasureValue(String costUnitOfMeasureValue) {
        this.costUnitOfMeasureValue = costUnitOfMeasureValue;
    }

    @Column(name = "COST_FX_VALUE")
    public BigDecimal getCostFxRateValue() {
        return costFxRateValue;
    }

    public void setCostFxRateValue(BigDecimal costFxRateValue) {
        this.costFxRateValue = costFxRateValue;
    }

    @Column(name = "COST_FX_INDEX_ID")
    public Integer getCostFxIndexId() {
        return costFxIndexId;
    }

    public void setCostFxIndexId(Integer costFxIndexId) {
        this.costFxIndexId = costFxIndexId;
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

    @Column(name = "IS_FIXED_VALUED")
    @Convert(
            converter = YesNoConverter.class
    )
    public Boolean getIsFixedValued() {
        return isFixedValued;
    }

    public void setIsFixedValued(Boolean fixedValued) {
        isFixedValued = fixedValued;
    }
}
