package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class PriceRiskFactorValuationJobRunnerBean implements DealJobRunner{

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private DealJobService dealJobService;


    @Override
    public void execute(DealJobRequestPublication publication) {
        DealJobSnapshot snapshot  = dealJobService.load(new EntityId(publication.getJobId()));
        DefinedQuery definedQuery;

        if (snapshot.getDetail().getQueryText() == null || snapshot.getDetail().getQueryText().equals("default")) {
            definedQuery = new DefinedQuery("PriceRiskFactor");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("marketDate"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("PriceRiskFactor", snapshot.getDetail().getQueryText());
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("marketDate"));
            }
        }
        LocalDateTime valuationDateTime = LocalDateTime.now();

        dealJobService.startPositionValuationExecution(
                snapshot.getEntityId(),
                valuationDateTime,
                LocalDateTime.now());
        priceRiskFactorService.valueRiskFactors(
                definedQuery,
                valuationDateTime);
        dealJobService.endPositionValuationExecution(
                snapshot.getEntityId(),
                LocalDateTime.now());


    }
}
