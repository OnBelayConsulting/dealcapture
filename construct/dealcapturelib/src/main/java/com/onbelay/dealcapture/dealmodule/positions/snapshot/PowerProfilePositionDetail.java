package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.onbelay.core.codes.annotations.CodeLabelSerializer;
import com.onbelay.core.codes.annotations.InjectCodeLabel;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PowerProfilePositionDetail extends AbstractDetail {

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdDateTime;
    private LocalDateTime valuedDateTime;
    private String powerFlowCodeValue;
    private Integer numberOfHours;
    
    private String currencyCodeValue;
    private String unitOfMeasureValue;

    private String errorCode;
    private String errorMessage;


    public void setDefaults() {
        errorCode = "0";
    }

    public void validate() throws OBValidationException {

    }

    public void copyFrom(PowerProfilePositionDetail copy) {

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

        if (copy.unitOfMeasureValue != null)
            this.unitOfMeasureValue = copy.unitOfMeasureValue;

        if (copy.numberOfHours != null)
            this.numberOfHours = copy.numberOfHours;

        if (copy.currencyCodeValue != null)
            this.currencyCodeValue = copy.currencyCodeValue;

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
    @InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "unitOfMeasureCodeItem")
    @JsonSerialize(using = CodeLabelSerializer.class)
    public String getUnitOfMeasureValue() {
        return unitOfMeasureValue;
    }

    public void setUnitOfMeasureValue(String volumeUnitOfMeasureValue) {
        this.unitOfMeasureValue = volumeUnitOfMeasureValue;
    }

    @Column(name = "NUMBER_OF_HOURS")
    public Integer getNumberOfHours() {
        return numberOfHours;
    }

    public void setNumberOfHours(Integer numberOfHours) {
        this.numberOfHours = numberOfHours;
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
