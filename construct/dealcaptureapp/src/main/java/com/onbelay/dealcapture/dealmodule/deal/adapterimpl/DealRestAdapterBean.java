package com.onbelay.dealcapture.dealmodule.deal.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.adapter.DealRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.component.DealFileReader;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.EvaluationContextRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.sql.exec.spi.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class DealRestAdapterBean extends BaseRestAdapterBean implements DealRestAdapter {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private DealService dealService;


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
            definedQuery = new DefinedQuery("BaseDeal");
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
        return dealService.save(dealSnapshot);
    }

    @Override
    public TransactionResult generatePositions(
            Integer dealId,
            EvaluationContextRequest context) {
        return null;
    }

    @Override
    public TransactionResult save(List<BaseDealSnapshot> snapshots) {
        initializeSession();
        return dealService.save(snapshots);
    }

    @Override
    public BaseDealSnapshot load(EntityId dealId) {
        initializeSession();
        return dealService.load(dealId);
    }

    @Override
    public TransactionResult saveFile(String originalFileName, byte[] fileContents) {
        initializeSession();

        ByteArrayInputStream fileStream = new ByteArrayInputStream(fileContents);
        DealFileReader dealFileReader = new DealFileReader(fileStream);

        dealFileReader.readContents();
        return dealService.save(dealFileReader.getDealSnapshots());
    }
}
