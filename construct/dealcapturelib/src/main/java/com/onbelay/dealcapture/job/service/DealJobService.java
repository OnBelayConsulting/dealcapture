package com.onbelay.dealcapture.job.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.job.enums.JobActionCode;
import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;

import java.time.LocalDateTime;
import java.util.List;

public interface DealJobService {

    TransactionResult save(DealJobSnapshot snapshot);

    public DealJobSnapshot load(EntityId entityId);

    public TransactionResult save(List<DealJobSnapshot> snapshots);

    QuerySelectedPage findJobIds(DefinedQuery definedQuery);

    List<DealJobSnapshot> findByIds(QuerySelectedPage selectedPage);


    public void startPositionGenerationExecution(
            EntityId jobId,
            LocalDateTime createdDateTime,
            String positionGenerationIdentifier,
            LocalDateTime startExecutionTime);

    void endPositionGenerationExecution(
            EntityId jobId,
            LocalDateTime executionEndDateTime);

    void failJobExecution(
            EntityId jobId,
            String errorCode,
            String errorMessage,
            LocalDateTime executionEndDateTime);

    void startPositionValuationExecution(
            EntityId jobId,
            LocalDateTime valuationDateTime,
            LocalDateTime executionStartDateTime);

    void endPositionValuationExecution(
            EntityId jobId,
            LocalDateTime executionEndDateTime);

    void changeJobStatus(
            EntityId entityId,
            JobActionCode actionCodeCode);
}
