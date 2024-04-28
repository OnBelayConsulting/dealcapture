package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;

public class PowerProfilePositionHolder {


    private PriceHourHolderMap hourHolderMap = new PriceHourHolderMap();

    private PowerProfilePositionSnapshot snapshot = new PowerProfilePositionSnapshot();

    public PowerProfilePositionSnapshot getSnapshot() {
        return snapshot;
    }

    public PriceHourHolderMap getHourHolderMap() {
        return hourHolderMap;
    }

}
