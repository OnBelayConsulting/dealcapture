package com.onbelay.dealcapture.dealmodule.positions.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.positions.adapter.DealPositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter.DealPositionFileWriter;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.service.PowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CreatedDateTimeCollection;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FileReportResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealPositionRestAdapterBean extends BaseRestAdapterBean implements DealPositionRestAdapter {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    @Autowired
    private ValuePositionsService valuePositionsService;

    @Autowired
    private DealService dealService;

    @Override
    @PreAuthorize("hasAnyRole('DEAL-ADMIN','VIEW-DEALS')")
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
    @PreAuthorize("hasAnyRole('DEAL-ADMIN','SAVE-DEALS')")
    public TransactionResult save(
            EntityId dealId,
            DealPositionSnapshot positionSnapshot) {
        initializeSession();
        positionSnapshot.setDealId(dealId);
        String positionGenerationIdentifier = "PG_" + Thread.currentThread().getId();
        TransactionResult result = dealPositionService.saveDealPositions(
                positionGenerationIdentifier,
                List.of(positionSnapshot));

        return result;
    }

    @Override
    @PreAuthorize("hasAnyRole('DEAL-ADMIN','SAVE-DEALS')")
    public TransactionResult save(List<DealPositionSnapshot> snapshots) {
        initializeSession();
        String positionGenerationIdentifier = "PG_" + Thread.currentThread().getId();
        TransactionResult result =  dealPositionService.saveDealPositions(
                positionGenerationIdentifier,
                snapshots);
        return result;
    }

    @Override
    @PreAuthorize("hasAnyRole('DEAL-ADMIN','SAVE-DEALS')")
    public DealPositionSnapshot load(EntityId entityId) {
        initializeSession();
        return dealPositionService.load(entityId);
    }

    @Override
    @PreAuthorize("hasAnyRole('DEAL-ADMIN','VIEW-DEALS')")
    public FileReportResult findPositionsAsCSV(
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
            return new FileReportResult("error");
        }

        int toIndex = start + limit;

        if (toIndex > allIds.getIds().size())
            toIndex =  allIds.getIds().size();
        int fromIndex = start;

        List<Integer> selected = allIds.getIds().subList(fromIndex, toIndex);
        QuerySelectedPage limitedPageSelection = new QuerySelectedPage(
                selected,
                allIds.getOrderByClause());

        List<DealPositionView> views = dealPositionService.findViewsByIds(limitedPageSelection);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            DealPositionFileWriter writer = new DealPositionFileWriter(outStream);
            writer.write(views);
            return new FileReportResult(
                    outStream.toByteArray(),
                    "deal positions");
        } catch (IOException e) {
            return new FileReportResult("error");
        }
    }

    @Override
    public CreatedDateTimeCollection getCreatedDateTimeCollection() {
        List<LocalDateTime> dateTimes =  dealPositionService.getPositionCreatedDateTimes();
        List<LocalDateTime> ppDateTimes = powerProfilePositionsService.getPositionCreatedDateTimes();
        dateTimes.addAll(ppDateTimes);
        dateTimes = dateTimes.stream().distinct().collect(Collectors.toList());
        return new CreatedDateTimeCollection(dateTimes);
    }
}
