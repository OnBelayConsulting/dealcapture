package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.ErrorHoldingSnapshot;

import java.time.LocalDateTime;
import java.util.List;

public class MarkToMarketResult extends ErrorHoldingSnapshot {

    private LocalDateTime createdDateTime;

    public MarkToMarketResult(LocalDateTime startDateTime) {
    }

    public MarkToMarketResult(String errorCode, List<String> parms) {
        super(errorCode, parms);
    }

    public MarkToMarketResult(String message) {
        super(message);
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
