package com.onbelay.dealcapture.job.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.job.adapter.DealJobRestAdapter;
import com.onbelay.dealcapture.job.enums.JobActionCode;
import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.publish.publisher.DealJobRequestPublisher;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshotCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealJobRestAdapterBean extends BaseRestAdapterBean implements DealJobRestAdapter {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealJobService dealJobService;

    @Autowired
    DealJobRequestPublisher dealJobRequestPublisher;

    @Override
    public TransactionResult createAndQueueDealJob(DealJobSnapshot snapshot) {

        initializeSession();

        snapshot.getDetail().setJobStatusCode(JobStatusCode.PENDING);

        TransactionResult result = dealJobService.save(snapshot);

        dealJobService.changeJobStatus(result.getEntityId(), JobActionCode.QUEUE);

        dealJobRequestPublisher.publish(new DealJobRequestPublication(result.getId()));
        return result;
    }

    @Override
    public DealJobSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("DealJob");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("DealJob", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("DealJob");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "ticketNo"));
        }

        QuerySelectedPage allIds = dealJobService.findJobIds(definedQuery);

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new DealJobSnapshotCollection(
                    start,
                    limit,
                    allIds.getIds().size());
        }

        int toIndex = start + limit;

        if (toIndex > allIds.getIds().size())
            toIndex =  allIds.getIds().size();
        int fromIndex = start;

        List<Integer> selected = allIds.getIds().subList(fromIndex, toIndex);
        QuerySelectedPage limitedPageSelection = new QuerySelectedPage(
                selected,
                allIds.getOrderByClause());

        List<DealJobSnapshot> snapshots = dealJobService.findByIds(limitedPageSelection);
        return new DealJobSnapshotCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

    @Override
    public DealJobSnapshot load(EntityId entityId) {
        initializeSession();
        return dealJobService.load(entityId);
    }

    @Override
    public TransactionResult cancelJob(EntityId dealId) {
        initializeSession();
        dealJobService.changeJobStatus(dealId, JobActionCode.CANCEL);
        return new TransactionResult(dealId.getId());
    }

    @Override
    public TransactionResult deleteJob(EntityId dealId) {
        initializeSession();
        dealJobService.changeJobStatus(dealId, JobActionCode.DELETE);
        return new TransactionResult();
    }
}
