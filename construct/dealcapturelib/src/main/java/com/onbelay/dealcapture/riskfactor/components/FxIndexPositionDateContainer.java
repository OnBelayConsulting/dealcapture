package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.shared.enums.FrequencyCode;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

public class FxIndexPositionDateContainer {

    private FxIndexSnapshot fxIndex;

    public FxIndexPositionDateContainer(FxIndexSnapshot fxIndex) {
        this.fxIndex = fxIndex;
    }

    private ConcurrentHashMap<LocalDate, FxRiskFactorSnapshot> factorMap = new ConcurrentHashMap<>();

    public void putRiskFactor(FxRiskFactorSnapshot snapshot) {
        factorMap.put(snapshot.getDetail().getMarketDate(), snapshot);
    }

    public FxRiskFactorSnapshot findRiskFactor(LocalDate marketDate) {

        final LocalDate searchDate;
        if (fxIndex.getDetail().getFrequencyCode() == FrequencyCode.MONTHLY) {
            searchDate = marketDate.withDayOfMonth(1);
        } else {
            searchDate = marketDate;
        }

        return factorMap.get(searchDate);
    }

}
