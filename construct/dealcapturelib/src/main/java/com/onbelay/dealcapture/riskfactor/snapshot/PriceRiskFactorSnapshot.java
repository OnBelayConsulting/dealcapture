package com.onbelay.dealcapture.riskfactor.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.Price;

import java.util.List;

public class PriceRiskFactorSnapshot extends AbstractSnapshot {

    private EntityId priceIndexId;

    private RiskFactorDetail detail = new RiskFactorDetail();

    public PriceRiskFactorSnapshot() {
    }

    public PriceRiskFactorSnapshot(EntityId entityId) {
        super(entityId);
    }

    public PriceRiskFactorSnapshot(String errorCode) {
        super(errorCode);
    }

    public PriceRiskFactorSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public PriceRiskFactorSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

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
}
