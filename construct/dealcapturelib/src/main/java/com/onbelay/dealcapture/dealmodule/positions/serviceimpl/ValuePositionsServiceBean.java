package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.CostPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.DealPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.model.*;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ValuePositionsServiceBean implements ValuePositionsService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private DealService dealService;

    @Autowired
    private FxIndexService fxIndexService;

    @Autowired
    private PriceIndexService priceIndexService;

    @Autowired
    private DealPositionsBatchUpdater dealPositionsBatchUpdater;

    @Autowired
    private CostPositionsBatchUpdater costPositionsBatchUpdater;

    @Override
    public TransactionResult valuePositions(
            EntityId dealId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {

        valueCostsAndPositions(
                List.of(dealId.getId()),
                currencyCode,
                createdDateTime,
                currentDateTime);

        return new TransactionResult();
    }

    @Override
    public TransactionResult valuePositions(
            DefinedQuery definedQuery,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {

        QuerySelectedPage selectedPage  = dealService.findDealIds(definedQuery);
        valueCostsAndPositions(
                selectedPage.getIds(),
                currencyCode,
                createdDateTime,
                currentDateTime);

        return new TransactionResult();
    }

    private void valueCostsAndPositions(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {

        logger.info("value positions start: " + LocalDateTime.now().toString());

        List<PriceIndexSnapshot> activePriceIndices = priceIndexService.findActivePriceIndices();

        List<FxIndexSnapshot> activeFxIndices = fxIndexService.findActiveFxIndices();

        ValuationIndexManager valuationIndexManager = new ValuationIndexManager(
                activeFxIndices,
                activePriceIndices);


        valueCostPositions(
                dealIds,
                currencyCode,
                createdDateTime,
                valuationIndexManager,
                currentDateTime);

        List<TotalCostPositionSummary> costPositionSummaries = dealPositionService.calculateTotalCostPositionSummaries(
                dealIds,
                currencyCode,
                createdDateTime);

        HashMap<Integer, Map<LocalDate, TotalCostPositionSummary>> totalCostMap = new HashMap<>();
        for (TotalCostPositionSummary summary : costPositionSummaries) {
            Map<LocalDate, TotalCostPositionSummary> map = totalCostMap.get(summary.getDealId());
            if (map == null) {
                map = new HashMap<>();
                totalCostMap.put(summary.getDealId(), map);
            }
            map.put(summary.getStartDate(), summary);
        }

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                dealIds,
                currencyCode,
                createdDateTime);

        ArrayList<PositionValuationResult> results = new ArrayList<>();

        for (DealPositionView view : views) {
            TotalCostPositionSummary summary = null;
            Map<LocalDate, TotalCostPositionSummary> map = totalCostMap.get(view.getDealId());
            if (map != null)
                summary = map.get(view.getDetail().getStartDate());
            PhysicalPositionValuator valuator = new PhysicalPositionValuator(
                    valuationIndexManager,
                    summary,
                    view);

            results.add(valuator.valuePosition(currentDateTime));
        }

        SubLister<PositionValuationResult> subLister = new SubLister<>(results, 1000);
        while (subLister.moreElements()) {
            dealPositionsBatchUpdater.updatePositions(subLister.nextList());
        }
        logger.info("value positions end: " + LocalDateTime.now().toString());
    }


    private void valueCostPositions(
            List<Integer> ids,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            ValuationIndexManager valuationIndexManager,
            LocalDateTime currentDateTime) {
        logger.info("value cost positions start: " + LocalDateTime.now().toString());

        ArrayList<CostPositionValuationResult> results = new ArrayList<>();
        List<CostPositionView> views = dealPositionService.fetchCostPositionViewsWithFX(
                ids,
                currencyCode,
                createdDateTime);

        if (views.isEmpty()) {
            logger.info("value cost positions end: " + LocalDateTime.now().toString());
            return;
        }

        for (CostPositionView costPositionView : views) {
            CostPositionValuator valuator = new CostPositionValuator(
                    valuationIndexManager,
                    costPositionView);
            results.add(valuator.valuePosition(currentDateTime));

        }

        SubLister<CostPositionValuationResult> subLister = new SubLister<>(results, 1000);
        while (subLister.moreElements()) {
            costPositionsBatchUpdater.updatePositions(subLister.nextList());
        }
        logger.info("value cost positions end: " + LocalDateTime.now().toString());

    }

}
