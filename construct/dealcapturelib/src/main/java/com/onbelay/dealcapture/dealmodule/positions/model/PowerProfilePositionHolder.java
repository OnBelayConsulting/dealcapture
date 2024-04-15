package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;

public class PowerProfilePositionHolder {

    private PriceRiskFactorHolder priceRiskFactorHolder;

    private FxRiskFactorHolder priceFxHolder;

    private ProfilePriceHourHolderMap hourHolderMap = new ProfilePriceHourHolderMap();

    private PowerProfilePositionSnapshot snapshot = new PowerProfilePositionSnapshot();

    public PowerProfilePositionSnapshot getSnapshot() {
        return snapshot;
    }

    public ProfilePriceHourHolderMap getHourHolderMap() {
        return hourHolderMap;
    }

    public PriceRiskFactorHolder getPriceRiskFactorHolder() {
        return priceRiskFactorHolder;
    }

    public void setPriceRiskFactorHolder(PriceRiskFactorHolder priceRiskFactorHolder) {
        this.priceRiskFactorHolder = priceRiskFactorHolder;
    }

    public FxRiskFactorHolder getPriceFxHolder() {
        return priceFxHolder;
    }

    public void setPriceFxHolder(FxRiskFactorHolder priceFxHolder) {
        this.priceFxHolder = priceFxHolder;
    }
}
