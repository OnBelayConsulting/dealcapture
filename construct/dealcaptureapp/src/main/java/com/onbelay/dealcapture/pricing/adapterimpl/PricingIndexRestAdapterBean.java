package com.onbelay.dealcapture.pricing.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.adapter.PricingIndexRestAdapter;
import com.onbelay.dealcapture.pricing.assembler.IndexPriceSnapshotAssembler;
import com.onbelay.dealcapture.pricing.assembler.PricingIndexSnapshotAssembler;
import com.onbelay.dealcapture.pricing.model.IndexPrice;
import com.onbelay.dealcapture.pricing.model.PricingIndex;
import com.onbelay.dealcapture.pricing.repository.PricingIndexRepository;
import com.onbelay.dealcapture.pricing.service.PricingIndexService;
import com.onbelay.dealcapture.pricing.snapshot.IndexPriceSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.IndexPriceSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingIndexRestAdapterBean extends BaseRestAdapterBean implements PricingIndexRestAdapter {

    @Autowired
    private PricingIndexService pricingIndexService;

    @Override
    public TransactionResult save(PricingIndexSnapshot snapshot) {
        initializeSession();
        return pricingIndexService.save(snapshot);
    }

    @Override
    public PricingIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText == null || queryText.equals("default")) {
            definedQuery = new DefinedQuery("PricingIndex");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("name"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("PricingIndex", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("name"));
            }
        }

        QuerySelectedPage selectedPage  = pricingIndexService.findPricingIndexIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new PricingIndexSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<PricingIndexSnapshot> snapshots = pricingIndexService.findByIds(querySelectedPage);

        return new PricingIndexSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }

    @Override
    public IndexPriceSnapshotCollection findPrices(
            String queryText,
            Integer start,
            Integer limit) {
        initializeSession();

        DefinedQuery definedQuery;

        if (queryText.equals("default")) {
            definedQuery = new DefinedQuery("IndexPrice");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("startDate"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("IndexPrice", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("startDate"));
            }
        }

        QuerySelectedPage selectedPage = pricingIndexService.findPricingIndexIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new IndexPriceSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<IndexPriceSnapshot> snapshots = pricingIndexService.fetchPricesByIds(querySelectedPage);

        return new IndexPriceSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }
}
