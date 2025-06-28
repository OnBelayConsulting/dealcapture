package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

public class PowerProfileDaySnapshot extends AbstractSnapshot {

    private EntityId powerProfileId;

    private PowerProfileDayDetail detail = new PowerProfileDayDetail();

    public PowerProfileDaySnapshot() {
        detail.setDefaults();
    }

    public PowerProfileDayDetail getDetail() {
        return detail;
    }

    public void setDetail(PowerProfileDayDetail detail) {
        this.detail = detail;
    }

    public EntityId getPowerProfileId() {
        return powerProfileId;
    }

    public void setPowerProfileId(EntityId powerProfileId) {
        this.powerProfileId = powerProfileId;
    }
}
