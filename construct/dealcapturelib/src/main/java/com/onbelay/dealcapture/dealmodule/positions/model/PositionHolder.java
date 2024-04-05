package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class PositionHolder {

    private DealPositionSnapshot dealPositionSnapshot;

    public PositionHolder(DealPositionSnapshot dealPositionSnapshot) {
        this.dealPositionSnapshot = dealPositionSnapshot;
    }

    public DealPositionSnapshot getDealPositionSnapshot() {
        return dealPositionSnapshot;
    }


}
