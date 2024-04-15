package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.time.LocalDateTime;

public class PowerProfilePositionEvaluator {

    private PowerProfilePositionView view;
    private ValuationIndexManager valuationIndexManager;


    public PowerProfilePositionEvaluator(ValuationIndexManager valuationIndexManager, PowerProfilePositionView view) {
        this.view = view;
        this.valuationIndexManager = valuationIndexManager;
    }

    public PowerProfilePositionValuationResult valuePosition(LocalDateTime valuationDate) {

        // apply rates
        PowerProfilePositionValuationResult result = new PowerProfilePositionValuationResult(
                view.getId(),
                valuationDate);

        for (int i=1; i < 25; i++) {
            Integer riskFactorId = view.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i);
            PriceRiskFactorSnapshot snapshot = valuationIndexManager.getPriceRiskFactor(riskFactorId);
            result.getHourPriceDayDetail().setHourPrice(i, snapshot.getDetail().getValue());
        }

        return result;
    }
}
