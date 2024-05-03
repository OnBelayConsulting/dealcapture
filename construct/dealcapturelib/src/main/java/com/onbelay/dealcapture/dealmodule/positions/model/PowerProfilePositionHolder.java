package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;

public class PowerProfilePositionHolder {

    private PriceRiskFactorHolder priceRiskFactorHolder;

    private PriceHourHolderMap hourHolderMap = new PriceHourHolderMap();

    private PowerProfilePositionSnapshot snapshot = new PowerProfilePositionSnapshot();

    public PowerProfilePositionSnapshot getSnapshot() {
        return snapshot;
    }

    public PriceHourHolderMap getHourHolderMap() {
        return hourHolderMap;
    }

    public PriceRiskFactorHolder getPriceRiskFactorHolder() {
        return priceRiskFactorHolder;
    }

    public void setPriceRiskFactorHolder(PriceRiskFactorHolder priceRiskFactorHolder) {
        this.priceRiskFactorHolder = priceRiskFactorHolder;
    }
}
