package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDealPositionGenerator implements DealPositionGenerator {
    protected DealSummary dealSummary;

    protected RiskFactorManager riskFactorManager;

    protected List<PositionHolder> positionHolders = new ArrayList<>();

    public BaseDealPositionGenerator(DealSummary dealSummary, RiskFactorManager riskFactorManager) {
        this.dealSummary = dealSummary;
        this.riskFactorManager = riskFactorManager;
    }

    @Override
    public DealSummary getDealSummary() {
        return dealSummary;
    }

    public RiskFactorManager getRiskFactorManager() {
        return riskFactorManager;
    }

    @Override
    public List<PositionHolder> getPositionHolders() {
        return positionHolders;
    }

}
