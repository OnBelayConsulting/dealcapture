package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DealPositionGeneratorFactory {

    private Map<DealTypeCode, BiFunction<DealSummary, RiskFactorManager, DealPositionGenerator>> generatorMap = new HashMap<>();


    public DealPositionGeneratorFactory() {
        initialize();
    }

    private void initialize() {
        generatorMap.put(DealTypeCode.PHYSICAL_DEAL, PhysicalDealPositionGenerator::newGenerator);
    }

    public DealPositionGenerator newGenerator(
            DealSummary dealSummary,
            RiskFactorManager riskFactorManager) {
         return generatorMap.get(dealSummary.getDealTypeCode()).apply(
                 dealSummary,
                 riskFactorManager);
    }

}
