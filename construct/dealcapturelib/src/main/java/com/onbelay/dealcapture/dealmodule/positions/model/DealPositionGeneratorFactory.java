package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayView;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class DealPositionGeneratorFactory {

    private Map<DealTypeCode, BiFunction<DealSummary, RiskFactorManager, DealPositionGenerator>> generatorMap = new HashMap<>();

    private HashMap<Integer, List<DealCostSummary>> costMap = new HashMap<>();
    private HashMap<Integer, DealDaysContainer> dealDayMap = new HashMap<>();

    public static DealPositionGeneratorFactory newFactory() {
        return new DealPositionGeneratorFactory();
    }

    public DealPositionGeneratorFactory withCosts(List<DealCostSummary> dealCostSummaries) {
        initializeCostMap(dealCostSummaries);
        return this;
    }

    public DealPositionGeneratorFactory withDealDays( List<DealDayView> dealDayViews) {
        initializeDealDayMap(dealDayViews);
        return this;
    }

    private DealPositionGeneratorFactory() {
        initialize();
    }

    private void initialize() {
        generatorMap.put(DealTypeCode.PHYSICAL_DEAL, PhysicalDealPositionGenerator::newGenerator);
    }

    private void initializeCostMap(List<DealCostSummary> dealCostSummaries) {
        for (DealCostSummary summary : dealCostSummaries) {
            List<DealCostSummary> summaries = costMap.get(summary.getDealId());
            if (summaries == null) {
                summaries = new ArrayList<>();
                costMap.put(summary.getDealId(),  summaries);
            }
            summaries.add(summary);

        }
    }

    private void initializeDealDayMap(List<DealDayView> dealDayViews) {
        for (DealDayView summary : dealDayViews) {
            DealDaysContainer container = dealDayMap.get(summary.getDealId());
            if (container == null) {
                container = new DealDaysContainer(summary.getDealId());
                dealDayMap.put(summary.getDealId(),  container);
            }
            container.processDealDayView(summary);
        }
    }

    public DealPositionGenerator newGenerator(
            DealSummary dealSummary,
            RiskFactorManager riskFactorManager) {
        DealPositionGenerator generator = generatorMap.get(dealSummary.getDealTypeCode()).apply(
                 dealSummary,
                 riskFactorManager);

        if (costMap.containsKey(dealSummary.getDealId().getId()))
            generator.withCosts(costMap.get(dealSummary.getDealId().getId()));

        if (dealDayMap.containsKey(dealSummary.getDealId().getId()))
            generator.withDealDays(dealDayMap.get(dealSummary.getDealId().getId()));

        return generator;
    }

}
