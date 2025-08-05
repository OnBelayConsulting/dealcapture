package com.onbelay.dealcapture.pricing.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.adapter.FxIndexRestAdapter;
import com.onbelay.dealcapture.pricing.curvesfilereader.FxCurvesFileReader;
import com.onbelay.dealcapture.pricing.curvesfilereader.PriceCurvesFileReader;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class FxIndexRestAdapterBean extends BaseRestAdapterBean implements FxIndexRestAdapter {

    @Autowired
    private FxIndexService fxIndexService;

    @Override
    public TransactionResult save(FxIndexSnapshot snapshot) {
        initializeSession();
        return fxIndexService.save(snapshot);
    }

    @Override
    public TransactionResult save(List<FxIndexSnapshot> snapshots) {
        initializeSession();
        return fxIndexService.save(snapshots);
    }

    @Override
    public FxIndexSnapshot load(EntityId fxIndexId) {
        initializeSession();
        return fxIndexService.load(fxIndexId);
    }

    @Override
    public FxIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText == null || queryText.equals("default")) {
            definedQuery = new DefinedQuery("FxIndex");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("name"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("FxIndex", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("name"));
            }
        }

        QuerySelectedPage selectedPage  = fxIndexService.findFxIndexIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new FxIndexSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<FxIndexSnapshot> snapshots = fxIndexService.findByIds(querySelectedPage);

        return new FxIndexSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }

    @Override
    public TransactionResult saveFxCurves(
            Integer fxIndexId,
            List<FxCurveSnapshot> snapshots) {

        initializeSession();

        return fxIndexService.saveFxCurves(
                new EntityId(fxIndexId),
                snapshots);

    }

    @Override
    public FxCurveSnapshotCollection findFxCurves(
            String queryText,
            Integer start,
            Integer limit) {
        initializeSession();

        DefinedQuery definedQuery;

        if (queryText.equals("default")) {
            definedQuery = new DefinedQuery("FxCurve");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("curveDate"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("FxCurve", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("curveDate"));
            }
        }

        QuerySelectedPage selectedPage = fxIndexService.findFxCurveIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new FxCurveSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<FxCurveSnapshot> snapshots = fxIndexService.fetchFxCurvesByIds(querySelectedPage);

        return new FxCurveSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }


    @Override
    public TransactionResult saveFxCurvesFile(String originalFilename, byte[] fileContents) {
        initializeSession();

        ByteArrayInputStream fileStream = new ByteArrayInputStream(fileContents);
        FxCurvesFileReader fileReader = new FxCurvesFileReader(fileStream);

        fileReader.readContents();
        TransactionResult result = new TransactionResult();
        for (String indexName : fileReader.getCurveSnapshotMap().keySet()) {
            TransactionResult childResult = fxIndexService.saveFxCurves(
                    new EntityId(indexName),
                    fileReader.getCurveSnapshotMap().get(indexName));
            result.setIds(childResult.getIds());
        }
        return result;
    }

}
