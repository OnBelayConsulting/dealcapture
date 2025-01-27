package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class PositionEvaluatorFactory {

    private LocalDateTime currentDateTime;
    private ValuationIndexManager valuationIndexManager;

    private Map<DealTypeCode, BiFunction<DealPositionView, ValuationIndexManager, DealPositionEvaluator>> evaluatorMap = new HashMap<>();


    public PositionEvaluatorFactory(
            LocalDateTime currentDateTime,
            ValuationIndexManager valuationIndexManager) {
        this.currentDateTime = currentDateTime;
        this.valuationIndexManager = valuationIndexManager;

        initialize();
    }

    private void initialize() {
        evaluatorMap.put(DealTypeCode.PHYSICAL_DEAL, PhysicalPositionEvaluator::new);
        evaluatorMap.put(DealTypeCode.FINANCIAL_SWAP, FinancialSwapPositionEvaluator::new);
        evaluatorMap.put(DealTypeCode.VANILLA_OPTION, VanillaOptionsPositionEvaluator::new);
    }

    public DealPositionEvaluator createPositionEvaluator(DealPositionView view) {
        DealPositionEvaluator evaluator =  evaluatorMap.get(view.getViewDetail().getDealTypeCode()).apply(view, valuationIndexManager);
        evaluator.setCurrentDateTime(currentDateTime);
        return evaluator;
    }

}
