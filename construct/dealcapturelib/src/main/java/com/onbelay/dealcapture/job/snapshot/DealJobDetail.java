package com.onbelay.dealcapture.job.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.job.enums.JobErrorCode;
import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.enums.JobTypeCode;
import com.onbelay.shared.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DealJobDetail extends AbstractDetail {

    private String jobTypeCodeValue;
    private String jobStatusCodeValue;
    private String queryText;
    private Integer domainId;
    private LocalDateTime createdDateTime;
    private LocalDateTime valuationDateTime;
    private String currencyCodeValue;
    private String positionGenerationId;
    private String volumeUnitOfMeasure;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDateTime queuedDateTime;
    private LocalDateTime executionStartDateTime;
    private LocalDateTime executionEndDateTime;
    private String errorCode;
    private String errorMessage;
    
    public void setDefaults() {
        errorCode = "0";
    }

    public void validate() throws OBValidationException {
        if (jobTypeCodeValue == null) {
            throw new OBValidationException(JobErrorCode.MISSING_JOB_STATUS_CODE.getCode());
        }
        if (jobStatusCodeValue == null) {
            throw new OBValidationException(JobErrorCode.MISSING_JOB_STATUS_CODE.getCode());
        }
        if (queryText == null && domainId == null) {
            throw new OBValidationException(JobErrorCode.MISSING_DEAL_QUERY_TEXT.getCode());
        }
        if (createdDateTime == null) {
            throw new OBValidationException(JobErrorCode.MISSING_CREATED_DATETIME.getCode());
        }
        if (currencyCodeValue == null) {
            throw new OBValidationException(JobErrorCode.MISSING_CURRENCY_CODE.getCode());
        }
        if (fromDate == null) {
            throw new OBValidationException(JobErrorCode.MISSING_FROM_DATE.getCode());
        }
        if (toDate == null) {
            throw new OBValidationException(JobErrorCode.MISSING_TO_DATE.getCode());
        }

    }


    public void copyFrom(DealJobDetail copy) {
        if (copy.jobTypeCodeValue != null) {
            this.jobTypeCodeValue = copy.jobTypeCodeValue;
        }
        if (copy.getJobStatusCodeValue() != null) {
            this.jobStatusCodeValue = copy.getJobStatusCodeValue();
        }
        if (copy.queryText != null) {
            this.queryText = copy.queryText;
        }
        if (copy.domainId != null) {
            this.domainId = copy.domainId;
        }
        if (copy.getCreatedDateTime() != null) {
            this.createdDateTime = copy.getCreatedDateTime();
        }
        if (copy.valuationDateTime != null) {
            valuationDateTime = copy.valuationDateTime;
        }
        if (copy.getCurrencyCodeValue() != null) {
            this.currencyCodeValue = copy.getCurrencyCodeValue();
        }
        if (copy.getFromDate() != null) {
            this.fromDate = copy.getFromDate();
        }
        if (copy.getToDate() != null) {
            this.toDate = copy.getToDate();
        }
        if (copy.getQueuedDateTime() != null) {
            this.queuedDateTime = copy.getQueuedDateTime();
        }
        if (copy.getExecutionStartDateTime() != null) {
            this.executionStartDateTime = copy.getExecutionStartDateTime();
        }
        if (copy.getExecutionEndDateTime() != null) {
            this.executionEndDateTime = copy.getExecutionEndDateTime();
        }
        if (copy.getPositionGenerationId() != null) {
            setPositionGenerationId(copy.getPositionGenerationId());
        }
        if (copy.getVolumeUnitOfMeasure() != null) {
            setVolumeUnitOfMeasure(copy.getVolumeUnitOfMeasure());
        }
        if (copy.errorCode != null) {
            this.errorCode = copy.errorCode;
        }
        if (copy.errorMessage != null) {
            this.errorMessage = copy.errorMessage;
        }

    }

    @Transient
    @JsonIgnore
    public JobTypeCode getJobTypeCode() {
        return JobTypeCode.lookUp(jobTypeCodeValue);
    }

    public void setJobTypeCode(JobTypeCode jobTypeCode) {
        this.jobTypeCodeValue = jobTypeCode.getCode();
    }

    @Column(name="JOB_TYPE_CODE")
    public String getJobTypeCodeValue() {
        return jobTypeCodeValue;
    }

    public void setJobTypeCodeValue(String jobTypeCodeValue) {
        this.jobTypeCodeValue = jobTypeCodeValue;
    }

    @JsonIgnore
    @Transient
    public JobStatusCode getJobStatusCode() {
        return JobStatusCode.lookUp(jobStatusCodeValue);
    }

    public void setJobStatusCode(JobStatusCode jobStatusCode) {
        this.jobStatusCodeValue = jobStatusCode.getCode();
    }

    @Column(name="JOB_STATUS_CODE")
    public String getJobStatusCodeValue() {
        return jobStatusCodeValue;
    }

    public void setJobStatusCodeValue(String jobStatusCodeValue) {
        this.jobStatusCodeValue = jobStatusCodeValue;
    }

    @Column(name="DEAL_ID")
    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    @Column(name="DEAL_QUERY_TEXT")
    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    @Column(name="CREATED_DATETIME")
    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @Column(name="VALUATION_DATETIME")
    public LocalDateTime getValuationDateTime() {
        return valuationDateTime;
    }

    public void setValuationDateTime(LocalDateTime valuationDateTime) {
        this.valuationDateTime = valuationDateTime;
    }


    @JsonIgnore
    @Transient
    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.lookUp(currencyCodeValue);
    }

    public void setCurrencyCode(CurrencyCode currencyCode) {
        this.currencyCodeValue = currencyCode.getCode();
    }

    @Column(name="CURRENCY_CODE")
    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public void setCurrencyCodeValue(String currencyCodeValue) {
        this.currencyCodeValue = currencyCodeValue;
    }

    @Column(name="FROM_DATE")
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    @Column(name="TO_DATE")
    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    @Column(name="QUEUED_DATETIME")
    public LocalDateTime getQueuedDateTime() {
        return queuedDateTime;
    }

    public void setQueuedDateTime(LocalDateTime queuedDateTime) {
        this.queuedDateTime = queuedDateTime;
    }

    @Column(name="EXECUTION_START_DATETIME")
    public LocalDateTime getExecutionStartDateTime() {
        return executionStartDateTime;
    }

    public void setExecutionStartDateTime(LocalDateTime executionStartDateTime) {
        this.executionStartDateTime = executionStartDateTime;
    }

    @Column(name="EXECUTION_END_DATETIME")
    public LocalDateTime getExecutionEndDateTime() {
        return executionEndDateTime;
    }

    public void setExecutionEndDateTime(LocalDateTime executionEndDateTime) {
        this.executionEndDateTime = executionEndDateTime;
    }
    @Column(name="POSITION_GENERATION_ID")
    public String getPositionGenerationId() {
        return positionGenerationId;
    }

    public void setPositionGenerationId(String positionGenerationId) {
        this.positionGenerationId = positionGenerationId;
    }

    @Column(name="VOLUME_UOM_CODE")
    public String getVolumeUnitOfMeasure() {
        return volumeUnitOfMeasure;
    }

    public void setVolumeUnitOfMeasure(String volumeUnitOfMeasure) {
        this.volumeUnitOfMeasure = volumeUnitOfMeasure;
    }

    @Column(name="ERROR_CODE")
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Column(name="ERROR_MSG")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
