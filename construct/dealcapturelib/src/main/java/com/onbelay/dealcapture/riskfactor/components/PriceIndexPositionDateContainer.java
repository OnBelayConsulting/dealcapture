package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.FrequencyCode;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

public class PriceIndexPositionDateContainer {

    private PriceIndexSnapshot priceIndex;

    private boolean isInitialized = false;

    private PriceIndexPositionDateContainer basisToHubContainer;

    private ConcurrentHashMap<LocalDate, PriceRiskFactorSnapshot> factorMap = new ConcurrentHashMap<>();

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
        factorMap.put(snapshot.getDetail().getMarketDate(), snapshot);
    }

    public PriceRiskFactorSnapshot findRiskFactor(LocalDate marketDate) {
        return factorMap.get(marketDate);
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
