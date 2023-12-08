package com.onbelay.dealcapture.dealmodule.positions.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.publish.publisher.GeneratePositionsRequestPublisher;
import com.onbelay.dealcapture.dealmodule.positions.adapter.DealPositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealPositionRestAdapterBean extends BaseRestAdapterBean implements DealPositionRestAdapter {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsRequestPublisher generatePositionsRequestPublisher;


    @Override
    public TransactionResult valuePositions(String queryText) {
        initializeSession();
        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("DealPosition");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("DealPosition", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("DealPosition");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "ticketNo"));
        }

        return dealPositionService.valuePositions(definedQuery);
    }

    @Override
    public DealPositionSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("DealPosition");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("DealPosition", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("DealPosition");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "ticketNo"));
        }

        QuerySelectedPage allIds = dealPositionService.findPositionIds(definedQuery);

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new DealPositionSnapshotCollection(
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

        List<DealPositionSnapshot> snapshots = dealPositionService.findByIds(limitedPageSelection);
        return new DealPositionSnapshotCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

    @Override
    public TransactionResult save(
            EntityId dealId,
            DealPositionSnapshot dealSnapshot) {
        initializeSession();
        TransactionResult result = dealPositionService.saveDealPositions(
                dealId,
                List.of(dealSnapshot));

        return result;
    }

    @Override
    public TransactionResult save(List<DealPositionSnapshot> snapshots) {
        initializeSession();
        TransactionResult result =  dealPositionService.saveAllDealPositions(snapshots);
        return result;
    }

    @Override
    public DealPositionSnapshot load(EntityId entityId) {
        initializeSession();
        return dealPositionService.load(entityId);
    }
}
