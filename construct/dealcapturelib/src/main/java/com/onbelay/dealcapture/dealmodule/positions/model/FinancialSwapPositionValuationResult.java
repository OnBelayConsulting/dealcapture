package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionPriceDetail;

import java.time.LocalDateTime;

public class FinancialSwapPositionValuationResult extends PositionValuationResult {

    private FinancialSwapPositionPriceDetail priceDetail = new FinancialSwapPositionPriceDetail();

    public FinancialSwapPositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime) {
        super(
                positionId,
                currentDateTime);
    }

    public FinancialSwapPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(FinancialSwapPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }
}
