package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

public class PhysicalPositionSnapshot extends DealPositionSnapshot {

    private String marketPriceIndexName;
    private EntityId marketPriceRiskFactorId;

    private EntityId marketPriceFxRiskFactorId;

    private String dealPriceIndexName;
    private EntityId dealPriceRiskFactorId;

    private EntityId dealPriceFxRiskFactorId;

    private PhysicalPositionPriceDetail priceDetail = new PhysicalPositionPriceDetail();

    public PhysicalPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(PhysicalPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    public PhysicalPositionSnapshot() {
        super(DealTypeCode.PHYSICAL_DEAL);
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

    public String getMarketPriceIndexName() {
        return marketPriceIndexName;
    }

    public void setMarketPriceIndexName(String marketPriceIndexName) {
        this.marketPriceIndexName = marketPriceIndexName;
    }

    public String getDealPriceIndexName() {
        return dealPriceIndexName;
    }

    public void setDealPriceIndexName(String dealPriceIndexName) {
        this.dealPriceIndexName = dealPriceIndexName;
    }
}
