package com.onbelay.dealcapture.pricing.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.adapter.PricingLocationRestAdapter;
import com.onbelay.dealcapture.pricing.service.PricingLocationService;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingLocationRestAdapterBean extends BaseRestAdapterBean implements PricingLocationRestAdapter {


    @Autowired
    private PricingLocationService pricingLocationService;

    @Override
    public TransactionResult save(PricingLocationSnapshot snapshot) {

        initializeSession();

        return pricingLocationService.save(snapshot);
    }

    @Override
    public TransactionResult save(List<PricingLocationSnapshot> snapshots) {

        initializeSession();

        return pricingLocationService.save(snapshots);
    }

    @Override
    public PricingLocationSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText.equals("default")) {
            definedQuery = new DefinedQuery("PricingLocation");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("name"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("PricingLocation", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("name"));
            }
        }

        QuerySelectedPage selectedPage  = pricingLocationService.findPricingLocationIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toLocation =  start.intValue() + limit;

        if (toLocation > totalIds.size())
            toLocation = totalIds.size();

        int fromLocation = start.intValue();

        if (fromLocation > toLocation)
            return new PricingLocationSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromLocation, toLocation);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<PricingLocationSnapshot> snapshots = pricingLocationService.findByIds(querySelectedPage);

        return new PricingLocationSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }

    @Override
    public PricingLocationSnapshot load(EntityId entityId) {
        initializeSession();
        return pricingLocationService.load(entityId);
    }
}
