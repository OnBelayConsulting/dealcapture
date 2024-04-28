package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionDetail;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;

public class DealHourlyPositionHolder {
    private DealHourlyPositionDetail detail = new DealHourlyPositionDetail();

    private FxRiskFactorHolder fxRiskFactorHolder;

    private EntityId powerProfilePositionId;

    private EntityId priceIndexId;

    private PriceHourHolderMap priceHourHolderMap = new PriceHourHolderMap();

    private EntityId dealId;

    public DealHourlyPositionDetail getDetail() {
        return detail;
    }

    public FxRiskFactorHolder getFxRiskFactorHolder() {
        return fxRiskFactorHolder;
    }

    public void setFxRiskFactorHolder(FxRiskFactorHolder fxRiskFactorHolder) {
        this.fxRiskFactorHolder = fxRiskFactorHolder;
    }

    public PriceHourHolderMap getPriceHourHolderMap() {
        return priceHourHolderMap;
    }

    public EntityId getPowerProfilePositionId() {
        return powerProfilePositionId;
    }

    public void setPowerProfilePositionId(EntityId powerProfilePositionId) {
        this.powerProfilePositionId = powerProfilePositionId;
    }

    public EntityId getDealId() {
        return dealId;
    }

    public void setDealId(EntityId dealId) {
        this.dealId = dealId;
    }

    public EntityId getPriceIndexId() {
        return priceIndexId;
    }

    public void setPriceIndexId(EntityId priceIndexId) {
        this.priceIndexId = priceIndexId;
    }
}
