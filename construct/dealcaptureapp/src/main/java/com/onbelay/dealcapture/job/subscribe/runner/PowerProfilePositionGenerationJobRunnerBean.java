package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePowerProfilePositionsService;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PowerProfilePositionGenerationJobRunnerBean implements DealJobRunner {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Autowired
    private DealJobService dealJobService;

    @Override
    public void execute(DealJobRequestPublication publication) {

        DealJobSnapshot snapshot = dealJobService.load(new EntityId(publication.getJobId()));

        String positionGenerationIdentifier = UUID.randomUUID().toString();

        LocalDateTime createdDateTime = snapshot.getDetail().getCreatedDateTime();
        if (createdDateTime == null) {
            createdDateTime = LocalDateTime.now();
        }

        List<Integer> ids = new ArrayList<>();
        if (snapshot.getDetail().getDomainId() != null) {
            ids.add(snapshot.getDetail().getDomainId());
        } else {
            DefinedQuery definedQuery;

            if (snapshot.getDetail().getQueryText() != null) {
                if (snapshot.getDetail().getQueryText().equalsIgnoreCase("default")) {
                    definedQuery = new DefinedQuery("PowerProfile");
                } else {
                    DefinedQueryBuilder builder = new DefinedQueryBuilder("PowerProfile", snapshot.getDetail().getQueryText());
                    definedQuery = builder.build();
                }
            } else {
                definedQuery = new DefinedQuery("PowerProfile");
            }

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause().addOrderExpression(
                        new DefinedOrderExpression(
                                "name"));
            }

            QuerySelectedPage allIds = powerProfileService.findPowerProfileIds(definedQuery);
            ids = allIds.getIds();
        }

        if (ids.isEmpty()) {
            logger.error("No ids found for PowerProfile");
            dealJobService.startPositionGenerationExecution(
                    snapshot.getEntityId(),
                    createdDateTime,
                    positionGenerationIdentifier,
                    LocalDateTime.now());
            dealJobService.endPositionGenerationExecution(
                    snapshot.getEntityId(),
                    LocalDateTime.now());
            return;
        }

        powerProfileService.updatePositionGenerationStatusToPending(ids);


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                snapshot.getDetail().getCurrencyCode(),
                createdDateTime,
                snapshot.getDetail().getFromDate(),
                snapshot.getDetail().getToDate());

        dealJobService.startPositionGenerationExecution(
                snapshot.getEntityId(),
                createdDateTime,
                positionGenerationIdentifier,
                LocalDateTime.now());

        generatePowerProfilePositionsService.generatePowerProfilePositions(
                positionGenerationIdentifier,
                context,
                ids);
        dealJobService.endPositionGenerationExecution(
                snapshot.getEntityId(),
                LocalDateTime.now());

    }
}
