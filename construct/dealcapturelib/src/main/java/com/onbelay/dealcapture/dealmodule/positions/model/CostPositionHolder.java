package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;

public class CostPositionHolder {

    private CostPositionSnapshot snapshot = new CostPositionSnapshot();

    private DealCostSummary dealCostSummary;

    private FxRiskFactorHolder costFxHolder;

    public CostPositionSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(CostPositionSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public FxRiskFactorHolder getCostFxHolder() {
        return costFxHolder;
    }

    public void setCostFxHolder(FxRiskFactorHolder costFxHolder) {
        this.costFxHolder = costFxHolder;
    }

    public DealCostSummary getDealCostSummary() {
        return dealCostSummary;
    }

    public void setDealCostSummary(DealCostSummary dealCostSummary) {
        this.dealCostSummary = dealCostSummary;
    }
}
