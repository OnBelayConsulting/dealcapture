package com.onbelay.dealcapture.riskfactor.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.riskfactor.adapter.PriceRiskFactorRestAdapter;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceRiskFactorRestAdapterBean extends BaseRestAdapterBean implements PriceRiskFactorRestAdapter {

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Override
    public TransactionResult save(
            EntityId priceIndexId,
            List<PriceRiskFactorSnapshot> snapshots) {
        return priceRiskFactorService.save(
                priceIndexId,
                snapshots);
    }

    @Override
    public PriceRiskFactorSnapshot load(EntityId priceRiskFactorId) {
        initializeSession();
        return priceRiskFactorService.load(priceRiskFactorId);
    }

    @Override
    public PriceRiskFactorSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText == null || queryText.equals("default")) {
            definedQuery = new DefinedQuery("PriceRiskFactor");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("marketDate"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("PriceRiskFactor", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("marketDate"));
            }
        }

        QuerySelectedPage selectedPage  = priceRiskFactorService.findPriceRiskFactorIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new PriceRiskFactorSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<PriceRiskFactorSnapshot> snapshots = priceRiskFactorService.findByIds(querySelectedPage);

        return new PriceRiskFactorSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }

}
