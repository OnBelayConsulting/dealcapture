package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;

public abstract class PositionHolder {

    private DealPositionSnapshot dealPositionSnapshot;
    private FxRiskFactorHolder costFxHolder;

    public PositionHolder(DealPositionSnapshot dealPositionSnapshot) {
        this.dealPositionSnapshot = dealPositionSnapshot;
    }

    public DealPositionSnapshot getDealPositionSnapshot() {
        return dealPositionSnapshot;
    }


    public FxRiskFactorHolder getCostFxHolder() {
        return costFxHolder;
    }

    public void setCostFxHolder(FxRiskFactorHolder costFxHolder) {
        this.costFxHolder = costFxHolder;
    }

}
