package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

public class PhysicalPositionSnapshot extends DealPositionSnapshot {

    private PhysicalPositionDetail detail = new PhysicalPositionDetail();

    private EntityId marketPriceRiskFactorId;

    private EntityId marketFxRiskFactorId;

    private EntityId dealPriceFxRiskFactorId;

    public PhysicalPositionSnapshot() {
        super.setDealTypeValue(DealTypeCode.PHYSICAL_DEAL.getCode());
    }

    public PhysicalPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(PhysicalPositionDetail detail) {
        this.detail = detail;
    }

    public EntityId getMarketPriceRiskFactorId() {
        return marketPriceRiskFactorId;
    }

    public void setMarketPriceRiskFactorId(EntityId marketPriceRiskFactorId) {
        this.marketPriceRiskFactorId = marketPriceRiskFactorId;
    }

    public EntityId getMarketFxRiskFactorId() {
        return marketFxRiskFactorId;
    }

    public void setMarketFxRiskFactorId(EntityId marketFxRiskFactorId) {
        this.marketFxRiskFactorId = marketFxRiskFactorId;
    }

    public EntityId getDealPriceFxRiskFactorId() {
        return dealPriceFxRiskFactorId;
    }

    public void setDealPriceFxRiskFactorId(EntityId dealPriceFxRiskFactorId) {
        this.dealPriceFxRiskFactorId = dealPriceFxRiskFactorId;
    }
}