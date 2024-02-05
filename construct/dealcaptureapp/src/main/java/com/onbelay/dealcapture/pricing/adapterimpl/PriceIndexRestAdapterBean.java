package com.onbelay.dealcapture.pricing.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.adapter.PriceIndexRestAdapter;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceIndexRestAdapterBean extends BaseRestAdapterBean implements PriceIndexRestAdapter {

    @Autowired
    private PriceIndexService priceIndexService;

    @Override
    public TransactionResult save(PriceIndexSnapshot snapshot) {
        initializeSession();
        return priceIndexService.save(snapshot);
    }

    @Override
    public TransactionResult save(List<PriceIndexSnapshot> snapshots) {
        initializeSession();
        return priceIndexService.save(snapshots);
    }

    @Override
    public PriceIndexSnapshot load(EntityId priceIndexId) {
        initializeSession();
        return priceIndexService.load(priceIndexId);
    }

    @Override
    public PriceIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText == null || queryText.equals("default")) {
            definedQuery = new DefinedQuery("PriceIndex");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("name"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("PriceIndex", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("name"));
            }
        }

        QuerySelectedPage selectedPage  = priceIndexService.findPriceIndexIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new PriceIndexSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<PriceIndexSnapshot> snapshots = priceIndexService.findByIds(querySelectedPage);

        return new PriceIndexSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }

    @Override
    public TransactionResult savePrices(
            Integer priceIndexId,
            List<PriceCurveSnapshot> snapshots) {

        initializeSession();

        return priceIndexService.savePrices(
                new EntityId(priceIndexId),
                snapshots);

    }

    @Override
    public PriceCurveSnapshotCollection findPrices(
            String queryText,
            Integer start,
            Integer limit) {
        initializeSession();

        DefinedQuery definedQuery;

        if (queryText.equals("default")) {
            definedQuery = new DefinedQuery("PriceCurve");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("startDate"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("PriceCurve", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("startDate"));
            }
        }

        QuerySelectedPage selectedPage = priceIndexService.findPriceIndexIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new PriceCurveSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<PriceCurveSnapshot> snapshots = priceIndexService.fetchPriceCurvesByIds(querySelectedPage);

        return new PriceCurveSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }
}
