package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

import java.util.List;

public class PowerProfilePositionSnapshot extends AbstractSnapshot {

    private EntityId powerProfileId;

    private EntityId priceIndexId;

    private PowerProfilePositionDetail detail = new PowerProfilePositionDetail();

    private HourPriceDayDetail hourPriceDayDetail = new HourPriceDayDetail();

    private HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap = new HourPriceRiskFactorIdMap();

    public PowerProfilePositionSnapshot() {
    }

    public PowerProfilePositionSnapshot(EntityId entityId) {
        super(entityId);
    }

    public PowerProfilePositionSnapshot(String errorCode) {
        super(errorCode);
    }

    public PowerProfilePositionSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public PowerProfilePositionSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

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

    public EntityId getPriceIndexId() {
        return priceIndexId;
    }

    public void setPriceIndexId(EntityId priceIndexId) {
        this.priceIndexId = priceIndexId;
    }
}
