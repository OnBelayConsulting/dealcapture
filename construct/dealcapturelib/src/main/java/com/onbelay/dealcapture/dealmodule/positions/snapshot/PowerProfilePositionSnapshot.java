package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

public class PowerProfilePositionSnapshot extends AbstractSnapshot {

    private EntityId powerProfileId;

    private EntityId priceRiskFactorId;

    private PowerProfilePositionDetail detail = new PowerProfilePositionDetail();

    private HourPriceDayDetail hourPriceDayDetail = new HourPriceDayDetail();

    private HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap = new HourPriceRiskFactorIdMap();

    public HourPriceDayDetail getHourPriceDayDetail() {
        return hourPriceDayDetail;
    }

    public void setHourPriceDayDetail(HourPriceDayDetail hourPriceDayDetail) {
        this.hourPriceDayDetail = hourPriceDayDetail;
    }

    public PowerProfilePositionDetail getDetail() {
        return detail;
    }

    public void setDetail(PowerProfilePositionDetail detail) {
        this.detail = detail;
    }

    public HourPriceRiskFactorIdMap getHourPriceRiskFactorIdMap() {
        return hourPriceRiskFactorIdMap;
    }

    public void setHourPriceRiskFactorIdMap(HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap) {
        this.hourPriceRiskFactorIdMap = hourPriceRiskFactorIdMap;
    }

    public EntityId getPowerProfileId() {
        return powerProfileId;
    }

    public void setPowerProfileId(EntityId powerProfileId) {
        this.powerProfileId = powerProfileId;
    }

    public EntityId getPriceRiskFactorId() {
        return priceRiskFactorId;
    }

    public void setPriceRiskFactorId(EntityId priceRiskFactorId) {
        this.priceRiskFactorId = priceRiskFactorId;
    }
}
