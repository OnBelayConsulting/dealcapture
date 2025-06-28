package com.onbelay.dealcapture.riskfactor.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.riskfactor.adapter.FxRiskFactorRestAdapter;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FxRiskFactorRestAdapterBean extends BaseRestAdapterBean implements FxRiskFactorRestAdapter {

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    @Override
    public TransactionResult save(
            EntityId fxIndexId, 
            List<FxRiskFactorSnapshot> snapshots) {
        return fxRiskFactorService.save(
                fxIndexId,
                snapshots);
    }

    @Override
    public FxRiskFactorSnapshot load(EntityId fxRiskFactorId) {
        initializeSession();
        return fxRiskFactorService.load(fxRiskFactorId);
    }

    @Override
    public FxRiskFactorSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText == null || queryText.equals("default")) {
            definedQuery = new DefinedQuery("FxRiskFactor");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("marketDate"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("FxRiskFactor", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("marketDate"));
            }
        }

        QuerySelectedPage selectedPage  = fxRiskFactorService.findFxRiskFactorIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new FxRiskFactorSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<FxRiskFactorSnapshot> snapshots = fxRiskFactorService.findByIds(querySelectedPage);

        return new FxRiskFactorSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }

}
