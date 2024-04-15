package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.FrequencyCode;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriceIndexPositionDateContainer {

    private PriceIndexSnapshot priceIndex;

    private boolean isInitialized = false;

    private PriceIndexPositionDateContainer basisToHubContainer;

    private ConcurrentHashMap<LocalDate, PriceRiskFactorSnapshot> factorMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<LocalDate, Map<Integer, PriceRiskFactorSnapshot>> hourFactorMap = new ConcurrentHashMap<>();

    public PriceIndexPositionDateContainer(PriceIndexSnapshot priceIndex) {
        this.priceIndex = priceIndex;
    }

    public PriceIndexPositionDateContainer(
            PriceIndexSnapshot priceIndex,
            PriceIndexPositionDateContainer basisToHubContainer) {
        this.priceIndex = priceIndex;
        this.basisToHubContainer = basisToHubContainer;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void putRiskFactor(PriceRiskFactorSnapshot snapshot) {
        isInitialized = true;
        if (priceIndex.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            Map<Integer, PriceRiskFactorSnapshot> hourMap = hourFactorMap.computeIfAbsent(snapshot.getDetail().getMarketDate(), k -> new HashMap<>());
            hourMap.put(snapshot.getDetail().getHourEnding(), snapshot);
        } else {
            factorMap.put(snapshot.getDetail().getMarketDate(), snapshot);
        }
    }

    public PriceRiskFactorSnapshot findRiskFactor(LocalDate marketDate) {
        return factorMap.get(marketDate);
    }


    public PriceRiskFactorSnapshot findRiskFactor(
            LocalDate marketDate,
            int hourEnding) {
        Map<Integer, PriceRiskFactorSnapshot> hourMap = hourFactorMap.get(marketDate);
        if (hourMap != null) {
            return hourMap.get(hourEnding);
        }else {
            return null;
        }
    }

    public boolean isBasis() {
        return basisToHubContainer != null;
    }

    public PriceIndexSnapshot getPriceIndex() {
        return priceIndex;
    }

    public void setBasisToHubContainer(PriceIndexPositionDateContainer basisToHubContainer) {
        this.basisToHubContainer = basisToHubContainer;
    }

    public PriceIndexPositionDateContainer getBasisToHubContainer() {
        return basisToHubContainer;
    }
}
