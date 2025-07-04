package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealPositionDetail extends AbstractDetail {

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdDateTime;
    private LocalDateTime valuedDateTime;
    private BigDecimal volumeQuantityValue;
    private String volumeUnitOfMeasureValue;
    private String frequencyCodeValue;
    private String powerFlowCodeValue;

    private String currencyCodeValue;


    private BigDecimal fixedPriceValue;


    private String errorCode;
    private String errorMessage;


    public void setDefaults() {
        errorCode = "0";
    }

    public void validate() throws OBValidationException {

    }

    public void copyFrom(DealPositionDetail copy) {

        if (copy.startDate != null)
            this.startDate = copy.startDate;

        if (copy.endDate != null)
            this.endDate = copy.endDate;

        if (copy.powerFlowCodeValue != null)
            this.powerFlowCodeValue = copy.powerFlowCodeValue;

        if (copy.createdDateTime != null)
            this.createdDateTime = copy.createdDateTime;

        if (copy.valuedDateTime != null)
            this.valuedDateTime = copy.valuedDateTime;

        if (copy.volumeQuantityValue != null)
            this.volumeQuantityValue = copy.volumeQuantityValue;

        if (copy.volumeUnitOfMeasureValue != null)
            this.volumeUnitOfMeasureValue = copy.volumeUnitOfMeasureValue;

        if (copy.frequencyCodeValue != null)
            this.frequencyCodeValue = copy.frequencyCodeValue;

        if (copy.currencyCodeValue != null)
            this.currencyCodeValue = copy.currencyCodeValue;

        if (copy.fixedPriceValue != null)
            this.fixedPriceValue = copy.fixedPriceValue;

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

    @Column(name = "CREATE_UPDATE_DATETIME")
    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createUpdateDate) {
        this.createdDateTime = createUpdateDate;
    }

    @Column(name = "VALUED_DATETIME")
    public LocalDateTime getValuedDateTime() {
        return valuedDateTime;
    }

    public void setValuedDateTime(LocalDateTime valuedDateTime) {
        this.valuedDateTime = valuedDateTime;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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


    @Column(name = "FIXED_PRICE")
    public BigDecimal getFixedPriceValue() {
        return fixedPriceValue;
    }

    public void setFixedPriceValue(BigDecimal fixedPriceValue) {
        this.fixedPriceValue = fixedPriceValue;
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
