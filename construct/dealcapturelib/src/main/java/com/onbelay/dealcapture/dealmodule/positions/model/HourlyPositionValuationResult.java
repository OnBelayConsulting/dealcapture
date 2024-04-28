package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;

import java.time.LocalDateTime;

public class HourlyPositionValuationResult extends BaseValuationResult {

    private HourFixedValueDayDetail prices;

    public HourlyPositionValuationResult(
            Integer positionId,
            HourFixedValueDayDetail prices,
            LocalDateTime currentDateTime) {

        super(positionId, currentDateTime);
        this.prices = prices;
    }

    public HourFixedValueDayDetail getPrices() {
        return prices;
    }

}
