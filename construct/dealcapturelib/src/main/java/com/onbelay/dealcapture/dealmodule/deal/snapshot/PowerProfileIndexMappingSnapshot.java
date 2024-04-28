package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

public class PowerProfileIndexMappingSnapshot extends AbstractSnapshot {

    private EntityId powerProfileId;
    private EntityId priceIndexId;

    private PowerProfileIndexMappingDetail detail = new PowerProfileIndexMappingDetail();

    public EntityId getPowerProfileId() {
        return powerProfileId;
    }

    public void setPowerProfileId(EntityId powerProfileId) {
        this.powerProfileId = powerProfileId;
    }

    public EntityId getPriceIndexId() {
        return priceIndexId;
    }

    public void setPriceIndexId(EntityId priceIndexId) {
        this.priceIndexId = priceIndexId;
    }

    public PowerProfileIndexMappingDetail getDetail() {
        return detail;
    }

    public void setDetail(PowerProfileIndexMappingDetail detail) {
        this.detail = detail;
    }
}
