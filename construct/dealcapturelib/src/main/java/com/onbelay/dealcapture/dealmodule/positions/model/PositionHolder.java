package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

public abstract class PositionHolder {

    private DealPositionSnapshot dealPositionSnapshot;

    public PositionHolder(DealPositionSnapshot dealPositionSnapshot) {
        this.dealPositionSnapshot = dealPositionSnapshot;
    }

    public DealPositionSnapshot getDealPositionSnapshot() {
        return dealPositionSnapshot;
    }
}
