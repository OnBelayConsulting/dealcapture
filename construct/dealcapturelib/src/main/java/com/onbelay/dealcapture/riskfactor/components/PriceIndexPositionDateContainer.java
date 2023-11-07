package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

public class PriceIndexPositionDateContainer {

    private PriceIndexSnapshot priceIndex;

    private ConcurrentHashMap<LocalDate, PriceRiskFactorSnapshot> factorMap = new ConcurrentHashMap<>();

    public PriceIndexPositionDateContainer(PriceIndexSnapshot priceIndex) {
        this.priceIndex = priceIndex;
    }

    public void putRiskFactor(PriceRiskFactorSnapshot snapshot) {
        factorMap.put(snapshot.getDetail().getMarketDate(), snapshot);
    }

    public PriceRiskFactorSnapshot findRiskFactor(LocalDate marketDate) {

        final LocalDate searchDate;
        if (priceIndex.getDetail().getFrequencyCode() == FrequencyCode.MONTHLY) {
            searchDate = marketDate.withDayOfMonth(1);
        } else {
            searchDate = marketDate;
        }

        return factorMap.get(searchDate);
    }

    public PriceIndexSnapshot getPriceIndex() {
        return priceIndex;
    }

}
