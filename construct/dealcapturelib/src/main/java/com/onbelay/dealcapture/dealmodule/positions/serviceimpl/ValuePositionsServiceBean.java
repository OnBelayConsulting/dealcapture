package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.CostPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.DealHourlyPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.DealPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.model.*;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
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
public class ValuePositionsServiceBean extends AbstractValuePositionsServiceBean implements ValuePositionsService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private DealService dealService;

    @Autowired
    private DealPositionsBatchUpdater dealPositionsBatchUpdater;

    @Autowired
    private CostPositionsBatchUpdater costPositionsBatchUpdater;

    @Autowired
    private DealHourlyPositionsBatchUpdater dealHourlyPositionsBatchUpdater;

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

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                dealIds,
                currencyCode,
                createdDateTime);

        LocalDate startDate = views.stream().map(c -> c.getDetail().getStartDate()).min(LocalDate::compareTo).get();
        LocalDate endDate = views.stream().map(c -> c.getDetail().getStartDate()).max(LocalDate::compareTo).get();

        ValuationIndexManager valuationIndexManager = createValuationIndexManager(
                startDate,
                endDate);


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

        List<DealHourlyPositionView> hourlyPositionViews = dealPositionService.fetchDealHourlyPositionViews(
                dealIds,
                currencyCode,
                createdDateTime);

        HashMap<Integer, Map<LocalDate, List<DealHourlyPositionView>>> hourlyPositionViewMap = new HashMap<>();
        for (DealHourlyPositionView view : hourlyPositionViews) {
            Map<LocalDate, List<DealHourlyPositionView>> viewMap = hourlyPositionViewMap.computeIfAbsent(
                    view.getDealId(),
                    k -> new HashMap<>());

            List<DealHourlyPositionView> viewList = viewMap.computeIfAbsent(
                    view.getDetail().getStartDate(),
                    k -> new ArrayList<>());

            viewList.add(view);
        }


        ArrayList<PositionValuationResult> results = new ArrayList<>();
        ArrayList<HourlyPositionValuationResult> hourlyPositionValuationResults = new ArrayList<>();

        for (DealPositionView view : views) {

            PhysicalPositionEvaluator evaluator = PhysicalPositionEvaluator.build(
                    currentDateTime,
                    valuationIndexManager,
                    view);

            Map<LocalDate, TotalCostPositionSummary> map = totalCostMap.get(view.getDealId());
            if (map != null)
                evaluator.withCosts(map.get(view.getDetail().getStartDate()));

            Map<LocalDate, List<DealHourlyPositionView>> viewMap = hourlyPositionViewMap.get(view.getDealId());
            if (viewMap != null) {
                List<DealHourlyPositionView> viewList = viewMap.get(view.getDetail().getStartDate());
                if (viewList != null) {
                    evaluator.withHourlyPositions(viewList);
                }
            }

            PositionValuationResult valuationResult = evaluator.valuePosition();
            hourlyPositionValuationResults.addAll(valuationResult.getHourlyPositionResults());
            results.add(valuationResult);
        }

        SubLister<PositionValuationResult> subLister = new SubLister<>(results, 1000);
        while (subLister.moreElements()) {
            dealPositionsBatchUpdater.updatePositions(subLister.nextList());
        }

        if (hourlyPositionValuationResults.isEmpty() == false) {
            SubLister<HourlyPositionValuationResult> hourlySubLister = new SubLister<>(hourlyPositionValuationResults, 1000);
            while (hourlySubLister.moreElements()) {
                dealHourlyPositionsBatchUpdater.updatePositions(hourlySubLister.nextList());
            }
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
            CostPositionEvaluator evaluator = new CostPositionEvaluator(
                    currentDateTime,
                    valuationIndexManager,
                    costPositionView);
            results.add(evaluator.valuePosition());

        }

        SubLister<CostPositionValuationResult> subLister = new SubLister<>(results, 1000);
        while (subLister.moreElements()) {
            costPositionsBatchUpdater.updatePositions(subLister.nextList());
        }
        logger.info("value cost positions end: " + LocalDateTime.now().toString());

    }

}
