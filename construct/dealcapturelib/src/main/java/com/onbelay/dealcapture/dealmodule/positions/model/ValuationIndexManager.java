package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ValuationIndexManager {

    private ConcurrentHashMap<Integer, FxIndexSnapshot> fxIndexMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, PriceIndexSnapshot> priceIndexMap = new ConcurrentHashMap<>();

    public ValuationIndexManager(
            List<FxIndexSnapshot> fxIndices,
            List<PriceIndexSnapshot> priceIndices) {

        processFxIndices(fxIndices);
        processPriceIndices(priceIndices);
    }

    private void processFxIndices(List<FxIndexSnapshot> fxIndices) {
        fxIndices.forEach( c-> fxIndexMap.put(c.getEntityId().getId(), c));
    }

    private void processPriceIndices(List<PriceIndexSnapshot> priceIndices) {
        priceIndices.forEach( c-> priceIndexMap.put(c.getEntityId().getId(), c));
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

    public FxIndexSnapshot getFxIndex(Integer id) {
        return fxIndexMap.get(id);
    }

    public PriceIndexSnapshot getPriceIndex(Integer id) {
        return priceIndexMap.get(id);
    }

}
