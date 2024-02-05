package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;

import java.util.ArrayList;
import java.util.List;

public class PhysicalPositionHolder extends PositionHolder {

    private PriceRiskFactorHolder marketRiskFactorHolder;
    private List<PriceRiskFactorHolder> basisToHubMarketHolders = new ArrayList<>();

    private FxRiskFactorHolder fixedDealPriceFxHolder;

    private PriceRiskFactorHolder dealPriceRiskFactorHolder;
    private List<PriceRiskFactorHolder> basisToHubDealPriceHolders = new ArrayList<>();

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
        return marketRiskFactorHolder.getFxRiskFactorHolder();
    }

    public void addBasisToHubMarketRiskFactorHolder(PriceRiskFactorHolder holder) {
        basisToHubMarketHolders.add(holder);
    }

    public List<PriceRiskFactorHolder> getBasisToHubMarketHolders() {
        return basisToHubMarketHolders;
    }

    public PriceRiskFactorHolder getDealPriceRiskFactorHolder() {
        return dealPriceRiskFactorHolder;
    }

    public void setDealPriceRiskFactorHolder(PriceRiskFactorHolder dealPriceRiskFactorHolder) {
        this.dealPriceRiskFactorHolder = dealPriceRiskFactorHolder;
    }

    public FxRiskFactorHolder getDealPriceFxHolder() {
        return dealPriceRiskFactorHolder.getFxRiskFactorHolder();
    }

    public void setFixedDealPriceFxHolder(FxRiskFactorHolder fixedDealPriceFxHolder) {
        this.fixedDealPriceFxHolder = fixedDealPriceFxHolder;
    }

    public FxRiskFactorHolder getFixedDealPriceFxHolder() {
        return fixedDealPriceFxHolder;
    }

    public void addBasisToHubDealPriceRiskFactorHolder(PriceRiskFactorHolder holder) {
        basisToHubDealPriceHolders.add(holder);
    }

    public List<PriceRiskFactorHolder> getBasisToHubDealPriceHolders() {
        return basisToHubDealPriceHolders;
    }
}
