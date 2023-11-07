package com.onbelay.dealcapture.dealmodule.deal.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.adapter.DealRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.publish.publisher.GeneratePositionsRequestPublisher;
import com.onbelay.dealcapture.dealmodule.deal.publish.snapshot.GeneratePositionsRequest;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DealRestAdapterBean extends BaseRestAdapterBean implements DealRestAdapter {

    @Autowired
    private DealService dealService;

    @Autowired
    private GeneratePositionsRequestPublisher generatePositionsRequestPublisher;

    @Override
    public DealSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("BaseDeal");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("BaseDeal", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("Deal");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "ticketNo"));
        }

        QuerySelectedPage allIds = dealService.findDealIds(definedQuery);

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new DealSnapshotCollection(
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

        List<BaseDealSnapshot> snapshots = dealService.findByIds(limitedPageSelection);
        return new DealSnapshotCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

    @Override
    public TransactionResult save(BaseDealSnapshot dealSnapshot) {
        initializeSession();
        TransactionResult result = dealService.save(dealSnapshot);
        BaseDealSnapshot saved = dealService.load(result.getEntityId());
        GeneratePositionsRequest request = new GeneratePositionsRequest(
                LocalDateTime.now(),
                saved.getDealDetail().getReportingCurrencyCode(),
                saved.getEntityId().getId());

        generatePositionsRequestPublisher.publish(request);

        return result;
    }

    @Override
    public TransactionResult save(List<BaseDealSnapshot> snapshots) {
        initializeSession();
        TransactionResult result =  dealService.save(snapshots);
        for (EntityId id : result.getEntityIds()) {
            BaseDealSnapshot saved = dealService.load(id);
            GeneratePositionsRequest request = new GeneratePositionsRequest(
                    LocalDateTime.now(),
                    saved.getDealDetail().getReportingCurrencyCode(),
                    saved.getEntityId().getId());

            generatePositionsRequestPublisher.publish(request);
        }
        return result;
    }

    @Override
    public BaseDealSnapshot load(EntityId entityId) {
        initializeSession();
        return dealService.load(entityId);
    }
}
