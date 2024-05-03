package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.pricing.enums.IndexType;
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
    private Integer basisNo;
    private String indexTypeCodeValue;

    private String errorCode;
    private String errorMessage;


    public void setDefaults() {
        errorCode = "0";
        basisNo = 0;
        indexTypeCodeValue = IndexType.HUB.getCode();
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

        if (copy.numberOfHours != null)
            this.numberOfHours = copy.numberOfHours;

        if (copy.basisNo != null)
            this.basisNo = copy.basisNo;

        if (copy.indexTypeCodeValue != null)
            this.indexTypeCodeValue = copy.indexTypeCodeValue;

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


    @Column(name = "NUMBER_OF_HOURS")
    public Integer getNumberOfHours() {
        return numberOfHours;
    }

    public void setNumberOfHours(Integer numberOfHours) {
        this.numberOfHours = numberOfHours;
    }

    @Column(name = "BASIS_NO")
    public Integer getBasisNo() {
        return basisNo;
    }

    public void setBasisNo(Integer basisNo) {
        this.basisNo = basisNo;
    }

    @Transient
    @JsonIgnore
    public IndexType getIndexTypeCode() {
        return IndexType.lookUp(indexTypeCodeValue);
    }

    public void setIndexTypeCode(IndexType indexTypeCode) {
        this.indexTypeCodeValue = indexTypeCode.getCode();
    }

    @Column(name = "INDEX_TYPE_CODE")
    public String getIndexTypeCodeValue() {
        return indexTypeCodeValue;
    }

    public void setIndexTypeCodeValue(String indexTypeCodeValue) {
        this.indexTypeCodeValue = indexTypeCodeValue;
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
