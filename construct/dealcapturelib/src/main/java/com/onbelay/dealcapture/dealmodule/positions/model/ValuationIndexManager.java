package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.InterestRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ValuationIndexManager {

    private InterestRate currentRiskFreeRate;

    private ConcurrentHashMap<Integer, FxIndexSnapshot> fxIndexMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, PriceIndexSnapshot> priceIndexMap = new ConcurrentHashMap<>();

    private HashMap<Integer, PriceRiskFactorSnapshot> priceRiskFactorMap = new HashMap<>();
    private HashMap<Integer, FxRiskFactorSnapshot> fxRiskFactorMap = new HashMap<>();


    public ValuationIndexManager(
            InterestRate currentRiskFreeRate,
            List<PriceIndexSnapshot> priceIndices,
            List<FxIndexSnapshot> fxIndices,
            List<PriceRiskFactorSnapshot> priceRiskFactors,
            List<FxRiskFactorSnapshot> fxRiskFactors) {

        this.currentRiskFreeRate = currentRiskFreeRate;

        fxIndices.forEach( c-> fxIndexMap.put(c.getEntityId().getId(), c));
        priceIndices.forEach( c-> priceIndexMap.put(c.getEntityId().getId(), c));

        priceRiskFactors.forEach(c-> priceRiskFactorMap.put(c.getEntityId().getId(), c));
        fxRiskFactors.forEach(c-> fxRiskFactorMap.put(c.getEntityId().getId(), c));
    }


    public FxRate generateFxRate(Integer fxIndexId, BigDecimal fxValue) {
        if ( fxIndexId == null || fxValue == null)
            return null;
        FxIndexSnapshot snapshot = getFxIndex(fxIndexId);

        return new FxRate(
                fxValue,
                snapshot.getDetail().getToCurrencyCode(),
                snapshot.getDetail().getFromCurrencyCode());
    }

    public Price generatePrice(Integer priceIndexId, BigDecimal priceValue) {
        if (priceIndexId == null)
            return null;

        PriceIndexSnapshot snapshot = getPriceIndex(priceIndexId);
        return new Price(
                priceValue,
                snapshot.getDetail().getCurrencyCode(),
                snapshot.getDetail().getUnitOfMeasureCode());
    }

    public PriceRiskFactorSnapshot getPriceRiskFactor(Integer priceRiskFactorId) {
        return priceRiskFactorMap.get(priceRiskFactorId);
    }

    public FxRiskFactorSnapshot getFxRiskFactor(Integer fxRiskFactorId) {
        return fxRiskFactorMap.get(fxRiskFactorId);
    }

    public FxIndexSnapshot getFxIndex(Integer id) {
        return fxIndexMap.get(id);
    }

    public PriceIndexSnapshot getPriceIndex(Integer id) {
        return priceIndexMap.get(id);
    }

    public InterestRate getCurrentRiskFreeRate() {
        return currentRiskFreeRate;
    }
}
