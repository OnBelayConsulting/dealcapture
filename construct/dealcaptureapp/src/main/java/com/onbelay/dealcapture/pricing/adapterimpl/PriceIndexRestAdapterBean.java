package com.onbelay.dealcapture.pricing.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.adapter.PriceIndexRestAdapter;
import com.onbelay.dealcapture.pricing.priceCurvesfilereader.PriceCurvesFileReader;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshotCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
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
    public PriceCurveSnapshot loadPriceCurve(EntityId priceCurveId) {
        initializeSession();
        return priceIndexService.loadPriceCurve(priceCurveId);
    }

    @Override
    public TransactionResult savePriceCurve(PriceCurveSnapshot snapshot) {
        initializeSession();
        return priceIndexService.savePrices(snapshot.getIndexId(), List.of(snapshot));
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
    public TransactionResult savePriceCurves(
            Integer priceIndexId,
            List<PriceCurveSnapshot> snapshots) {

        initializeSession();

        return priceIndexService.savePrices(
                new EntityId(priceIndexId),
                snapshots);

    }

    @Override
    public PriceCurveSnapshotCollection findPriceCurves(
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

        QuerySelectedPage selectedPage = priceIndexService.findPriceCurveIds(definedQuery);
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

    @Override
    public TransactionResult savePriceCurvesFile(String originalFilename, byte[] fileContents) {
        initializeSession();

        ByteArrayInputStream fileStream = new ByteArrayInputStream(fileContents);
        PriceCurvesFileReader fileReader = new PriceCurvesFileReader(fileStream);

        fileReader.readContents();
        TransactionResult result = new TransactionResult();
        for (String indexName : fileReader.getCurveSnapshotMap().keySet()) {
            TransactionResult childResult = priceIndexService.savePrices(
                    new EntityId(indexName),
                    fileReader.getCurveSnapshotMap().get(indexName));
            result.setIds(childResult.getIds());
        }
        return result;
    }
}
