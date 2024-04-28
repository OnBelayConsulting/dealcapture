package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.ErrorHoldingSnapshot;

import java.util.List;

public class MarkToMarketResult extends ErrorHoldingSnapshot {

    private String positionGenerationIdentifier;

    public MarkToMarketResult() {
    }

    public MarkToMarketResult(String errorCode) {
        super(errorCode);
    }

    public MarkToMarketResult(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public MarkToMarketResult(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public String getPositionGenerationIdentifier() {
        return positionGenerationIdentifier;
    }

    public void setPositionGenerationIdentifier(String positionGenerationIdentifier) {
        this.positionGenerationIdentifier = positionGenerationIdentifier;
    }
}
