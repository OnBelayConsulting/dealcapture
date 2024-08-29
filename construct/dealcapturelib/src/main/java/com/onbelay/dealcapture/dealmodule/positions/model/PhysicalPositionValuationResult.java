package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionPriceDetail;

import java.time.LocalDateTime;

public class PhysicalPositionValuationResult extends PositionValuationResult {

    private PhysicalPositionPriceDetail priceDetail = new PhysicalPositionPriceDetail();

    public PhysicalPositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime) {
        super(
                positionId,
                currentDateTime);
    }

    public PhysicalPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(PhysicalPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }
}
