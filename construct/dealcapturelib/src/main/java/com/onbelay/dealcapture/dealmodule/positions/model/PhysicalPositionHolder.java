package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.formulas.model.FxRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;

public class PhysicalPositionHolder extends PositionHolder {

    private PriceRiskFactorHolder marketRiskFactorHolder;
    private FxRiskFactorHolder marketFxRiskFactorHolder;
    private PriceRiskFactorHolder dealPriceRiskFactorHolder;
    private FxRiskFactorHolder dealPriceFxRiskFactorHolder;

    public PhysicalPositionHolder(DealPositionSnapshot snapshot) {
        super(snapshot);
    }

    public PriceRiskFactorHolder getMarketRiskFactorHolder() {
        return marketRiskFactorHolder;
    }

    public void setMarketRiskFactorHolder(PriceRiskFactorHolder marketRiskFactorHolder) {
        this.marketRiskFactorHolder = marketRiskFactorHolder;
    }

    public FxRiskFactorHolder getMarketFxRiskFactorHolder() {
        return marketFxRiskFactorHolder;
    }

    public void setMarketFxRiskFactorHolder(FxRiskFactorHolder marketFxRiskFactorHolder) {
        this.marketFxRiskFactorHolder = marketFxRiskFactorHolder;
    }

    public PriceRiskFactorHolder getDealPriceRiskFactorHolder() {
        return dealPriceRiskFactorHolder;
    }

    public void setDealPriceRiskFactorHolder(PriceRiskFactorHolder dealPriceRiskFactorHolder) {
        this.dealPriceRiskFactorHolder = dealPriceRiskFactorHolder;
    }

    public FxRiskFactorHolder getDealPriceFxRiskFactorHolder() {
        return dealPriceFxRiskFactorHolder;
    }

    public void setDealPriceFxRiskFactorHolder(FxRiskFactorHolder dealPriceFxRiskFactorHolder) {
        this.dealPriceFxRiskFactorHolder = dealPriceFxRiskFactorHolder;
    }
}
