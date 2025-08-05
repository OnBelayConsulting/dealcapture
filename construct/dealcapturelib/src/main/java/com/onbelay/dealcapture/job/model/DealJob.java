package com.onbelay.dealcapture.job.model;

import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.job.enums.JobActionCode;
import com.onbelay.dealcapture.job.enums.JobErrorCode;
import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.snapshot.DealJobDetail;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DEAL_JOB")
public class DealJob extends AbstractEntity {

    private Integer id;

    private Integer dependsOnJobId;

    private DealJobDetail detail = new DealJobDetail();

    public DealJob() {
    }

    @Id
    @Column(name="ENTITY_ID", insertable =  false, updatable = false)
    @SequenceGenerator(name="dealJobGen", sequenceName="DEAL_JOB_SEQ", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "dealJobGen")
    public Integer getId() {
        return id;
    }

    private void setId(Integer dealId) {
        this.id = dealId;
    }



    @Column(name= "DEPENDS_ON_JOB_ID")
    public Integer getDependsOnJobId() {
        return dependsOnJobId;
    }

    public void setDependsOnJobId(Integer dependsOnJobId) {
        this.dependsOnJobId = dependsOnJobId;
    }

    @Embedded
    public DealJobDetail getDetail() {
        return detail;
    }

    public void setDetail(DealJobDetail dealJobDetail) {
        this.detail = dealJobDetail;
    }

    @Override
    protected void validate() throws OBValidationException {

    }

    public void updateStartExecution(
            LocalDateTime createdDateTime,
            String positionGenerationId,
            LocalDateTime startExecutionDateTime) {

        updateStartExecutionStatus(
                createdDateTime,
                startExecutionDateTime);
        detail.setPositionGenerationId(positionGenerationId);
        update();
    }


    public void updateStartValuation(
            LocalDateTime valuationDateTime,
            LocalDateTime valuingStartDateTime) {
        getDetail().setJobStatusCode(JobStatusCode.EXECUTING);
        detail.setValuationDateTime(valuationDateTime);
        getDetail().setExecutionStartDateTime(valuingStartDateTime);
        update();
    }

    protected void updateStartExecutionStatus(
            LocalDateTime createdDateTime,
            LocalDateTime startExecutionDateTime) {
        getDetail().setJobStatusCode(JobStatusCode.EXECUTING);
        getDetail().setCreatedDateTime(createdDateTime);
        getDetail().setExecutionStartDateTime(startExecutionDateTime);

    }

    public void updateEndExecution(LocalDateTime endDateTime) {
        getDetail().setJobStatusCode(JobStatusCode.COMPLETED);
        getDetail().setExecutionEndDateTime(endDateTime);
    }

    public void updateToFailedExecution(
            String errorCode,
            String errorMessage,
            LocalDateTime endDateTime) {
        getDetail().setJobStatusCode(JobStatusCode.FAILED);
        getDetail().setExecutionEndDateTime(endDateTime);
        getDetail().setErrorCode(errorCode);
        getDetail().setErrorMessage(errorMessage);

    }


    public void createWith(DealJobSnapshot snapshot) {
        if (snapshot.getDependsOnId() != null) {
            this.dependsOnJobId = snapshot.getDependsOnId().getId();
        }
        getDetail().copyFrom(snapshot.getDetail());
        getDetail().setJobStatusCode(JobStatusCode.PENDING);
        save();
    }

    @Override
    public void delete() {
        if (detail.getJobStatusCode() == JobStatusCode.FAILED || detail.getJobStatusCode() == JobStatusCode.COMPLETED) {
            super.delete();
        } else {
            throw new OBRuntimeException(JobErrorCode.DELETE_FAILED.getCode());
        }

    }

    public void updateWith(DealJobSnapshot snapshot) {
        getDetail().copyFrom(snapshot.getDetail());
    }

    public void changeJobStatus(JobActionCode actionCode) {
        switch (actionCode) {
            case CANCEL -> {
                switch (getDetail().getJobStatusCode()) {
                    case EXECUTING, QUEUED, PENDING -> {
                        updateJobStatus(JobStatusCode.FAILED);

                    }
                    case COMPLETED,FAILED -> throw new OBRuntimeException(JobErrorCode.CANCEL_FAILED.getCode());
                }
            }
            case DELETE -> {
                switch (getDetail().getJobStatusCode()) {
                    case COMPLETED,FAILED,PENDING,QUEUED -> {
                       delete();

                    }
                    case EXECUTING -> throw new OBRuntimeException(JobErrorCode.DELETE_FAILED.getCode());
                }
            }
            case COMPLETE -> {
                switch (detail.getJobStatusCode()) {
                    case FAILED, COMPLETED -> {return;}
                    case EXECUTING -> {updateJobStatus(JobStatusCode.COMPLETED);}
                    case PENDING -> {throw new OBRuntimeException(JobErrorCode.DELETE_FAILED.getCode());}
                }
            }
            case FAIL -> {
                switch (detail.getJobStatusCode()) {
                    case FAILED -> {return;}
                    case EXECUTING, PENDING, QUEUED -> {updateJobStatus(JobStatusCode.FAILED);}
                    case COMPLETED -> {throw new OBRuntimeException(JobErrorCode.FAIL_FAILED.getCode());}
                }

            }
            case QUEUE -> {
                switch (getDetail().getJobStatusCode()) {
                    case COMPLETED,FAILED,EXECUTING,QUEUED -> {
                        throw new OBRuntimeException(JobErrorCode.QUEUE_FAILED.getCode());

                    }
                    case PENDING -> updateJobStatus(JobStatusCode.QUEUED);
                }

            }
        }

    }

    protected void updateJobStatus(JobStatusCode jobStatusCode) {
        getDetail().setJobStatusCode(jobStatusCode);
        update();
    }
}
