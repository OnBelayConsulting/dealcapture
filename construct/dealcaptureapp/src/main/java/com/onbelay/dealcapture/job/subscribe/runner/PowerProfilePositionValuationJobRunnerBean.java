package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePowerProfilePositionsService;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PowerProfilePositionValuationJobRunnerBean implements DealJobRunner {


    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private ValuePowerProfilePositionsService valuePowerProfilePositionsService;

    @Autowired
    private DealJobService dealJobService;


    @Override
    public void execute(DealJobRequestPublication publication) {

        DealJobSnapshot snapshot =  dealJobService.load(new EntityId(publication.getJobId()));

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
                                "ticketNo"));
            }

            QuerySelectedPage allIds = powerProfileService.findPowerProfileIds(definedQuery);
            ids = allIds.getIds();
        }
        LocalDateTime valuationDateTime = LocalDateTime.now();

        if (ids.isEmpty()) {
            dealJobService.startPositionValuationExecution(
                    snapshot.getEntityId(),
                    valuationDateTime,
                    LocalDateTime.now());
            dealJobService.endPositionValuationExecution(
                    snapshot.getEntityId(),
                    LocalDateTime.now());
            return;
        }

        dealJobService.startPositionValuationExecution(
                snapshot.getEntityId(),
                valuationDateTime,
                LocalDateTime.now());
        TransactionResult result = valuePowerProfilePositionsService.valuePositions(
                ids,
                snapshot.getDetail().getCurrencyCode(),
                snapshot.getDetail().getCreatedDateTime(),
                valuationDateTime);

    }
}
