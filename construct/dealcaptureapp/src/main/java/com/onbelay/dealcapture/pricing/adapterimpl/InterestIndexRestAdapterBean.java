package com.onbelay.dealcapture.pricing.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.adapter.InterestIndexRestAdapter;
import com.onbelay.dealcapture.pricing.curvesfilereader.FxCurvesFileReader;
import com.onbelay.dealcapture.pricing.curvesfilereader.InterestCurvesFileReader;
import com.onbelay.dealcapture.pricing.service.InterestIndexService;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class InterestIndexRestAdapterBean extends BaseRestAdapterBean implements InterestIndexRestAdapter {

    @Autowired
    private InterestIndexService interestIndexService;

    @Override
    public TransactionResult save(InterestIndexSnapshot snapshot) {
        initializeSession();
        return interestIndexService.save(snapshot);
    }

    @Override
    public TransactionResult save(List<InterestIndexSnapshot> snapshots) {
        initializeSession();
        return interestIndexService.save(snapshots);
    }

    @Override
    public InterestIndexSnapshot load(EntityId interestIndexId) {
        initializeSession();
        return interestIndexService.load(interestIndexId);
    }

    @Override
    public InterestIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText == null || queryText.equals("default")) {
            definedQuery = new DefinedQuery("InterestIndex");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("name"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("InterestIndex", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("name"));
            }
        }

        QuerySelectedPage selectedPage  = interestIndexService.findInterestIndexIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new InterestIndexSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<InterestIndexSnapshot> snapshots = interestIndexService.findByIds(querySelectedPage);

        return new InterestIndexSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }

    @Override
    public TransactionResult saveInterestCurves(
            Integer interestIndexId,
            List<InterestCurveSnapshot> snapshots) {

        initializeSession();

        return interestIndexService.saveInterestCurves(
                new EntityId(interestIndexId),
                snapshots);

    }

    @Override
    public TransactionResult saveInterestCurve(InterestCurveSnapshot snapshot) {
        initializeSession();

        return interestIndexService.saveInterestCurves(
                snapshot.getIndexId(),
                List.of(snapshot));
    }

    @Override
    public InterestCurveSnapshotCollection findInterestCurves(
            String queryText,
            Integer start,
            Integer limit) {
        initializeSession();

        DefinedQuery definedQuery;

        if (queryText.equals("default")) {
            definedQuery = new DefinedQuery("InterestCurve");
            definedQuery.getOrderByClause()
                    .addOrderExpression(
                            new DefinedOrderExpression("curveDate"));
        } else {
            DefinedQueryBuilder builder = new DefinedQueryBuilder("InterestCurve", queryText);
            definedQuery = builder.build();

            if (definedQuery.getOrderByClause().hasExpressions() == false) {
                definedQuery.getOrderByClause()
                        .addOrderExpression(
                                new DefinedOrderExpression("curveDate"));
            }
        }

        QuerySelectedPage selectedPage = interestIndexService.findInterestCurveIds(definedQuery);
        List<Integer> totalIds = selectedPage.getIds();

        int toIndex =  start.intValue() + limit;

        if (toIndex > totalIds.size())
            toIndex = totalIds.size();

        int fromIndex = start.intValue();

        if (fromIndex > toIndex)
            return new InterestCurveSnapshotCollection(
                    start,
                    limit,
                    totalIds.size());

        List<Integer> selectedIds =  totalIds.subList(fromIndex, toIndex);

        QuerySelectedPage querySelectedPage = new QuerySelectedPage(
                selectedIds,
                definedQuery.getOrderByClause());


        List<InterestCurveSnapshot> snapshots = interestIndexService.fetchInterestCurvesByIds(querySelectedPage);

        return new InterestCurveSnapshotCollection(
                start,
                limit,
                totalIds.size(),
                snapshots);
    }


    @Override
    public TransactionResult saveInterestCurvesFile(String originalFilename, byte[] fileContents) {
        initializeSession();

        ByteArrayInputStream fileStream = new ByteArrayInputStream(fileContents);
        InterestCurvesFileReader fileReader = new InterestCurvesFileReader(fileStream);

        fileReader.readContents();
        TransactionResult result = new TransactionResult();
        for (String indexName : fileReader.getCurveSnapshotMap().keySet()) {
            TransactionResult childResult = interestIndexService.saveInterestCurves(
                    new EntityId(indexName),
                    fileReader.getCurveSnapshotMap().get(indexName));
            result.setIds(childResult.getIds());
        }
        return result;
    }


}
