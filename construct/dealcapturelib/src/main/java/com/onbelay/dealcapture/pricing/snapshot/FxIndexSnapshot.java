package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;

public class FxIndexSnapshot extends AbstractSnapshot {

    private FxIndexDetail detail = new FxIndexDetail();

    public FxIndexDetail getDetail() {
        return detail;
    }

    public void setDetail(FxIndexDetail detail) {
        this.detail = detail;
    }
}
