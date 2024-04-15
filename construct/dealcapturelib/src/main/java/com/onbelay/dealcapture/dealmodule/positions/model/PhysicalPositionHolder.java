package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;

import java.util.ArrayList;
import java.util.List;

public class PhysicalPositionHolder extends BasePositionHolder {

    private FxRiskFactorHolder fixedPriceFxHolder;

    private PriceRiskFactorHolder marketRiskFactorHolder;
    private FxRiskFactorHolder marketFxHolder;

    private List<PriceRiskFactorHolder> basisMarketHolders = new ArrayList<>();

    private PriceRiskFactorHolder dealPriceRiskFactorHolder;
    private FxRiskFactorHolder dealPriceFxHolder;

    private List<PriceRiskFactorHolder> basisDealPriceHolders = new ArrayList<>();

    public PhysicalPositionHolder(DealPositionSnapshot snapshot) {
        super(snapshot);
    }

    public PriceRiskFactorHolder getMarketRiskFactorHolder() {
        return marketRiskFactorHolder;
    }

    public void setMarketRiskFactorHolder(PriceRiskFactorHolder marketRiskFactorHolder) {
        this.marketRiskFactorHolder = marketRiskFactorHolder;
    }

    public FxRiskFactorHolder getMarketFxHolder() {
        return marketFxHolder;
    }

    public void setMarketFxHolder(FxRiskFactorHolder marketFxHolder) {
        this.marketFxHolder = marketFxHolder;
    }

    public PriceRiskFactorHolder getDealPriceRiskFactorHolder() {
        return dealPriceRiskFactorHolder;
    }

    public void setDealPriceRiskFactorHolder(PriceRiskFactorHolder dealPriceRiskFactorHolder) {
        this.dealPriceRiskFactorHolder = dealPriceRiskFactorHolder;
    }

    public FxRiskFactorHolder getDealPriceFxHolder() {
        return dealPriceFxHolder;
    }

    public void setDealPriceFxHolder(FxRiskFactorHolder dealPriceFxHolder) {
        this.dealPriceFxHolder = dealPriceFxHolder;
    }

    public void setFixedPriceFxHolder(FxRiskFactorHolder fixedDealPriceFxHolder) {
        this.fixedPriceFxHolder = fixedDealPriceFxHolder;
    }

    public FxRiskFactorHolder getFixedPriceFxHolder() {
        return fixedPriceFxHolder;
    }

    public List<PriceRiskFactorHolder> getBasisDealPriceHolders() {
        return basisDealPriceHolders;
    }
    public void setBasisDealPriceHolders(List<PriceRiskFactorHolder> basisDealPriceHolders) {
        this.basisDealPriceHolders = basisDealPriceHolders;
    }

    public List<PriceRiskFactorHolder> getBasisMarketHolders() {
        return basisMarketHolders;
    }

    public void setBasisMarketHolders(List<PriceRiskFactorHolder> basisMarketHolders) {
        this.basisMarketHolders = basisMarketHolders;
    }

}
