package com.onbelay.dealcapture.job.subscribe.runner;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePositionsService;
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

@Component
public class DealPositionValuationJobRunnerBean implements DealJobRunner{
    private static final Logger logger = LogManager.getLogger(DealPositionValuationJobRunnerBean.class);
    @Autowired
    private DealService dealService;

    @Autowired
    private DealJobService dealJobService;

    @Autowired
    ValuePositionsService valuePositionsService;

    @Override
    public void execute(DealJobRequestPublication publication) {

        DealJobSnapshot snapshot =  dealJobService.load(new EntityId(publication.getJobId()));
        LocalDateTime valuationDateTime = LocalDateTime.now();

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
        valuePositionsService.valuePositions(
                ids,
                snapshot.getDetail().getCurrencyCode(),
                snapshot.getDetail().getFromDate(),
                snapshot.getDetail().getToDate(),
                snapshot.getDetail().getCreatedDateTime(),
                valuationDateTime);
        dealJobService.endPositionValuationExecution(
                snapshot.getEntityId(),
                LocalDateTime.now());

    }
}
