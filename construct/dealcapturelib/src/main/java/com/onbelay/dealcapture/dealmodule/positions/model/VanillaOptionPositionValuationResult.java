package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionPriceDetail;

import java.time.LocalDateTime;

public class VanillaOptionPositionValuationResult extends PositionValuationResult {

    private VanillaOptionPositionPriceDetail priceDetail = new VanillaOptionPositionPriceDetail();

    public VanillaOptionPositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime) {
        super(
                positionId,
                currentDateTime);
    }

    public VanillaOptionPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(VanillaOptionPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }
}
