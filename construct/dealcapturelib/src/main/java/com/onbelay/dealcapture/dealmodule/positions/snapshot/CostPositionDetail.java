package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
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

public class CostPositionDetail {

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdDateTime;
    private LocalDateTime valuedDateTime;

    private String costNameCodeValue;
    private Boolean isFixedValued;
    private BigDecimal volumeQuantityValue;
    private BigDecimal costValue;
    private BigDecimal costAmount;
    private String currencyCodeValue;
    private String unitOfMeasureValue;
    private String frequencyCodeValue;
    private Boolean isSettlementPosition;

    private String settlementReference;

    private String errorCode;
    private String errorMessage;


    public void setDefaults() {
        errorCode = "0";
    }

    public void validate() throws OBValidationException {

    }


    public void copyFrom(CostPositionDetail copy) {

        if (copy.startDate != null)
            this.startDate = copy.startDate;

        if (copy.endDate != null)
            this.endDate = copy.endDate;

        if (copy.isFixedValued != null)
            this.isFixedValued = copy.isFixedValued;

        if (copy.volumeQuantityValue != null)
            this.volumeQuantityValue = copy.volumeQuantityValue;

        if (copy.isSettlementPosition != null)
            this.isSettlementPosition = copy.isSettlementPosition;

        if (copy.createdDateTime != null)
            this.createdDateTime = copy.createdDateTime;

        if (copy.valuedDateTime != null)
            this.valuedDateTime = copy.valuedDateTime;

        if (copy.unitOfMeasureValue != null)
            this.unitOfMeasureValue = copy.unitOfMeasureValue;

        if (copy.frequencyCodeValue != null)
            this.frequencyCodeValue = copy.frequencyCodeValue;

        if (copy.currencyCodeValue != null)
            this.currencyCodeValue = copy.currencyCodeValue;

        if (copy.costNameCodeValue != null)
            this.costNameCodeValue = copy.costNameCodeValue;

        if (copy.costAmount != null)
            this.costAmount = copy.costAmount;

        if (copy.costValue != null)
            this.costValue = copy.costValue;

        if (copy.settlementReference != null)
            this.settlementReference = copy.settlementReference;

        if (copy.errorCode != null)
            this.errorCode = copy.errorCode;

        if (copy.errorMessage != null)
            this.errorMessage = copy.errorMessage;
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

    @Column(name = "CREATE_UPDATE_DATETIME")
    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createUpdateDate) {
        this.createdDateTime = createUpdateDate;
    }

    @Column(name = "VOLUME_QUANTITY")
    public BigDecimal getVolumeQuantityValue() {
        return volumeQuantityValue;
    }

    public void setVolumeQuantityValue(BigDecimal volumeQuantityValue) {
        this.volumeQuantityValue = volumeQuantityValue;
    }

    @Column(name = "VALUED_DATETIME")
    public LocalDateTime getValuedDateTime() {
        return valuedDateTime;
    }

    public void setValuedDateTime(LocalDateTime valuedDateTime) {
        this.valuedDateTime = valuedDateTime;
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

    @Transient
    @JsonIgnore
    public CostNameCode getCostNameCode() {
        return CostNameCode.lookUp(costNameCodeValue);
    }

    public void setCostNameCode(CostNameCode code) {
        this.costNameCodeValue = code.getCode();
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

    @Column(name = "COST_AMOUNT")
    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal amount) {
        this.costAmount = amount;
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

    public void setUnitOfMeasureValue(String unitOfMeasureValue) {
        this.unitOfMeasureValue = unitOfMeasureValue;
    }

    @Transient
    @JsonIgnore
    public FrequencyCode getFrequencyCode() {
        return FrequencyCode.lookUp(frequencyCodeValue);
    }

    public void setFrequencyCode(FrequencyCode code) {
        this.frequencyCodeValue = code.getCode();
    }

    @Column(name = "FREQUENCY_CODE")
    public String getFrequencyCodeValue() {
        return frequencyCodeValue;
    }

    public void setFrequencyCodeValue(String frequencyCodeValue) {
        this.frequencyCodeValue = frequencyCodeValue;
    }

    @Column(name = "SETTLEMENT_REFERENCE")
    public String getSettlementReference() {
        return settlementReference;
    }

    public void setSettlementReference(String settlementReference) {
        this.settlementReference = settlementReference;
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
