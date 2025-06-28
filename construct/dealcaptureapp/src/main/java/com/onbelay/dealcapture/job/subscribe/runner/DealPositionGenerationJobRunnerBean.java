package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DealPositionGenerationJobRunnerBean implements DealJobRunner {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private GeneratePositionsService generatePositionsService;

    @Autowired
    private DealService dealService;

    @Autowired
    private DealJobService dealJobService;


    @Override
    public void execute(DealJobRequestPublication publication) {

        DealJobSnapshot snapshot = dealJobService.load(new EntityId(publication.getJobId()));

        List<Integer> ids = new ArrayList<>();
        if (snapshot.getDetail().getDomainId() != null) {
            ids.add(snapshot.getDetail().getDomainId());
        } else {
            DefinedQuery definedQuery;

            if (snapshot.getDetail().getQueryText() != null) {
                if (snapshot.getDetail().getQueryText().equalsIgnoreCase("default")) {
                    definedQuery = new DefinedQuery("BaseDeal");
                } else {
                    DefinedQueryBuilder builder = new DefinedQueryBuilder("BaseDeal", snapshot.getDetail().getQueryText());
                    definedQuery = builder.build();
                }
            } else {
                definedQuery = new DefinedQuery("BaseDeal");
            }

            QuerySelectedPage selection = dealService.findDealIds(definedQuery);
            ids = selection.getIds();
        }
        dealService.updateDealPositionGenerationStatusToPending(ids);

        String positionGenerationIdentifier = UUID.randomUUID().toString();

        LocalDateTime createdDateTime = snapshot.getDetail().getCreatedDateTime();
        if (createdDateTime == null) {
            createdDateTime = LocalDateTime.now();
        }

        DealPositionsEvaluationContext dealPositionsEvaluationContext = new DealPositionsEvaluationContext(
                snapshot.getDetail().getCurrencyCode(),
                createdDateTime,
                snapshot.getDetail().getFromDate(),
                snapshot.getDetail().getToDate());



        if (dealPositionsEvaluationContext.validate() == false) {
            throw new OBRuntimeException(PositionErrorCode.MISSING_REQUIRED_EVAL_CONTEXT_FIELDS.getCode());
        }

        try {
            dealJobService.startPositionGenerationExecution(
                    snapshot.getEntityId(),
                    createdDateTime,
                    positionGenerationIdentifier,
                    LocalDateTime.now());

            generatePositionsService.generatePositions(
                    positionGenerationIdentifier,
                    dealPositionsEvaluationContext,
                    ids);
            dealJobService.endPositionGenerationExecution(
                    snapshot.getEntityId(),
                    LocalDateTime.now());
        } catch (OBRuntimeException e) {
            dealJobService.failJobExecution(
                    snapshot.getEntityId(),
                    e.getErrorCode(),
                    e.getMessage(),
                    LocalDateTime.now());
        } catch (RuntimeException e) {
            dealJobService.failJobExecution(
                    snapshot.getEntityId(),
                    PositionErrorCode.ERROR_POSITION_GENERATION_FAILED.getCode(),
                    e.getMessage(),
                    LocalDateTime.now());
        }


    }
}
