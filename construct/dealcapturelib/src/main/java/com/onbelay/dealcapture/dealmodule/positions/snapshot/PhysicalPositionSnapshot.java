package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

public class PhysicalPositionSnapshot extends DealPositionSnapshot {

    private PhysicalPositionDetail detail = new PhysicalPositionDetail();

    private EntityId marketPriceRiskFactorId;

    private EntityId marketPriceFxRiskFactorId;

    private EntityId dealPriceRiskFactorId;

    private EntityId dealPriceFxRiskFactorId;

    private EntityId fixedPriceFxRiskFactorId;



    public PhysicalPositionSnapshot() {
        super(DealTypeCode.PHYSICAL_DEAL);
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

    public EntityId getMarketPriceFxRiskFactorId() {
        return marketPriceFxRiskFactorId;
    }

    public void setMarketPriceFxRiskFactorId(EntityId marketPriceFxRiskFactorId) {
        this.marketPriceFxRiskFactorId = marketPriceFxRiskFactorId;
    }

    public EntityId getFixedPriceFxRiskFactorId() {
        return fixedPriceFxRiskFactorId;
    }

    public void setFixedPriceFxRiskFactorId(EntityId fixedPriceFxRiskFactorId) {
        this.fixedPriceFxRiskFactorId = fixedPriceFxRiskFactorId;
    }

    public EntityId getDealPriceRiskFactorId() {
        return dealPriceRiskFactorId;
    }

    public void setDealPriceRiskFactorId(EntityId dealPriceRiskFactorId) {
        this.dealPriceRiskFactorId = dealPriceRiskFactorId;
    }

    public EntityId getDealPriceFxRiskFactorId() {
        return dealPriceFxRiskFactorId;
    }

    public void setDealPriceFxRiskFactorId(EntityId dealPriceFxRiskFactorId) {
        this.dealPriceFxRiskFactorId = dealPriceFxRiskFactorId;
    }
}
