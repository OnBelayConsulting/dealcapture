package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.CostPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.DealHourlyPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.DealPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.model.*;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.PowerProfilePositionsService;
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
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

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
            LocalDate fromPositionDate,
            LocalDate toPositionDate,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {

        valueCostsAndPositions(
                List.of(dealId.getId()),
                currencyCode,
                fromPositionDate,
                toPositionDate,
                createdDateTime,
                currentDateTime);

        return new TransactionResult();
    }

    @Override
    public TransactionResult valuePositions(
            DefinedQuery definedQuery,
            CurrencyCode currencyCode,
            LocalDate fromPositionDate,
            LocalDate toPositionDate,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {


        QuerySelectedPage selectedPage  = dealService.findDealIds(definedQuery);
        valueCostsAndPositions(
                selectedPage.getIds(),
                currencyCode,
                fromPositionDate,
                toPositionDate,
                createdDateTime,
                currentDateTime);

        return new TransactionResult();
    }

    private void valueCostsAndPositions(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDate fromPositionDate,
            LocalDate toPositionDate,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {

        logger.info("value positions start: " + LocalDateTime.now().toString());

        logger.info("create valuationIndexManager start: " + LocalDateTime.now().toString());
        ValuationIndexManager valuationIndexManager = createValuationIndexManager(
                fromPositionDate,
                toPositionDate);
        logger.info("create valuationIndexManager end: " + LocalDateTime.now().toString());


        for (Integer dealId : dealIds) {
            List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                    dealId,
                    currencyCode,
                    createdDateTime);
            logger.debug("fetch deal position views end: " + LocalDateTime.now().toString());

            if (views.isEmpty()) {
                logger.debug("No positions to value.");
                continue;
            }

            List<DealHourlyPositionView> hourlyPositionViews = dealPositionService.fetchDealHourlyPositionViews(
                    dealId,
                    currencyCode,
                    createdDateTime);

            valueCostsAndPositionsByDeal(
                    valuationIndexManager,
                    dealId,
                    views,
                    hourlyPositionViews,
                    currencyCode,
                    createdDateTime,
                    currentDateTime);
        }

        logger.info("value positions end: " + LocalDateTime.now().toString());
    }

private void valueCostsAndPositionsByDeal(
        ValuationIndexManager valuationIndexManager,
        Integer dealId,
        List<DealPositionView> dealPositionViews,
        List<DealHourlyPositionView> hourlyPositionViews,
        CurrencyCode currencyCode,
        LocalDateTime createdDateTime,
        LocalDateTime currentDateTime) {

        valueCostPositions(
                dealId,
                currencyCode,
                createdDateTime,
                valuationIndexManager,
                currentDateTime);

        logger.debug("fetch cost summaries start: " + LocalDateTime.now().toString());
        List<TotalCostPositionSummary> costPositionSummaries = dealPositionService.calculateTotalCostPositionSummaries(
                dealId,
                currencyCode,
                createdDateTime);
        logger.debug("fetch cost summaries end: " + LocalDateTime.now().toString());

        HashMap<Integer, Map<LocalDate, TotalCostPositionSummary>> totalCostMap = new HashMap<>();
        for (TotalCostPositionSummary summary : costPositionSummaries) {
            Map<LocalDate, TotalCostPositionSummary> map = totalCostMap.get(summary.getDealId());
            if (map == null) {
                map = new HashMap<>();
                totalCostMap.put(summary.getDealId(), map);
            }
            map.put(summary.getStartDate(), summary);
        }

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

        QuerySelectedPage selectedPage = powerProfileService.findPowerProfileIds(new DefinedQuery("PowerProfile"));
        List<PowerProfilePositionView> powerProfilePositionViews = powerProfilePositionsService.fetchPowerProfilePositionViews(
                selectedPage.getIds(),
                createdDateTime);
        Map<Integer, Map<LocalDate, List<PowerProfilePositionView>>> powerProfilePositionViewMap = new HashMap<>();
        for (PowerProfilePositionView view : powerProfilePositionViews) {
            Map<LocalDate, List<PowerProfilePositionView>> powerPositionByDateMap = powerProfilePositionViewMap.computeIfAbsent(
                    view.getPowerProfileId(),
                    k -> new HashMap<>());

            List<PowerProfilePositionView> positionViewList = powerPositionByDateMap.computeIfAbsent(
                    view.getDetail().getStartDate(),
                    k -> new ArrayList<>());
            positionViewList.add(view);
        }


        ArrayList<PositionValuationResult> results = new ArrayList<>();
        ArrayList<HourlyPositionValuationResult> hourlyPositionValuationResults = new ArrayList<>();

        for (DealPositionView view : dealPositionViews) {

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
                if (viewList != null && view.getDetail().getPowerFlowCode() != null) {
                    List<DealHourlyPositionView> viewListByFlowCode = viewList
                            .stream()
                            .filter( c-> c.getDetail().getPowerFlowCode() == view.getDetail().getPowerFlowCode())
                            .toList();
                    evaluator.withHourlyPositions(viewListByFlowCode);
                }
            }

            if (view.getPowerProfileId() != null) {
                Map<LocalDate, List<PowerProfilePositionView>> powerPositionByDateMap = powerProfilePositionViewMap.get(
                        view.getPowerProfileId());
                List<PowerProfilePositionView> viewList = powerPositionByDateMap.get(view.getDetail().getStartDate());
                evaluator.withPowerProfilePositions(viewList);
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

    }


    private void valueCostPositions(
            Integer dealId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            ValuationIndexManager valuationIndexManager,
            LocalDateTime currentDateTime) {
        logger.debug("value cost positions start: " + LocalDateTime.now().toString());

        ArrayList<CostPositionValuationResult> results = new ArrayList<>();
        List<CostPositionView> views = dealPositionService.fetchCostPositionViewsWithFX(
                dealId,
                currencyCode,
                createdDateTime);

        if (views.isEmpty()) {
            logger.debug("value cost positions end: " + LocalDateTime.now().toString());
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
        logger.debug("value cost positions end: " + LocalDateTime.now().toString());

    }

}
