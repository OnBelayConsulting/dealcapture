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
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class FxRiskFactorValuationJobRunnerBean implements DealJobRunner{

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    @Autowired
    private DealJobService dealJobService;


    @Override
    public void execute(DealJobRequestPublication publication) {
        DealJobSnapshot snapshot  =  dealJobService.load(new EntityId(publication.getJobId()));

        DefinedQuery definedQuery;

        if (snapshot.getDetail().getQueryText() == null || snapshot.getDetail().getQueryText().equals("default")) {
            definedQuery = new DefinedQuery("FxRiskFactor");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("marketDate"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("FxRiskFactor", snapshot.getDetail().getQueryText());
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("marketDate"));
            }
        }
        LocalDateTime valuationDateTime = LocalDateTime.now();

        try {
            dealJobService.startPositionValuationExecution(
                    snapshot.getEntityId(),
                    valuationDateTime,
                    LocalDateTime.now());
            fxRiskFactorService.valueRiskFactors(
                    definedQuery,
                    valuationDateTime);
            dealJobService.endPositionValuationExecution(
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
                    PositionErrorCode.ERROR_INVALID_POSITION_VALUATION.getCode(),
                    e.getMessage(),
                    LocalDateTime.now());
        }


    }
}
