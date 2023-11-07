package com.onbelay.dealcapture.riskfactor.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.Price;

public class PriceRiskFactorSnapshot extends AbstractSnapshot {

    private EntityId priceIndexId;

    private RiskFactorDetail detail = new RiskFactorDetail();

    public EntityId getPriceIndexId() {
        return priceIndexId;
    }

    public void setPriceIndexId(EntityId priceIndexId) {
        this.priceIndexId = priceIndexId;
    }

    public RiskFactorDetail getDetail() {
        return detail;
    }

    public void setDetail(RiskFactorDetail detail) {
        this.detail = detail;
    }

    public Price fetchCurrentPrice() {
        return null;
    }
}
