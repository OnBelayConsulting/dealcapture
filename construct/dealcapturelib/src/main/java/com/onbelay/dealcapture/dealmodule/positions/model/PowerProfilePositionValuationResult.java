package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourPriceDayDetail;

import java.time.LocalDateTime;

public class PowerProfilePositionValuationResult extends BaseValuationResult {

    private HourPriceDayDetail hourPriceDayDetail = new HourPriceDayDetail();

    public PowerProfilePositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime) {

        super(positionId, currentDateTime);
    }

    public HourPriceDayDetail getHourPriceDayDetail() {
        return hourPriceDayDetail;
    }

    public void setHourPriceDayDetail(HourPriceDayDetail hourPriceDayDetail) {
        this.hourPriceDayDetail = hourPriceDayDetail;
    }
}
