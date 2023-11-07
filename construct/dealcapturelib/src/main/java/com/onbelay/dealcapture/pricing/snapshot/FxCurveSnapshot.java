package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;

public class FxCurveSnapshot extends AbstractSnapshot {

    private CurveDetail detail = new CurveDetail();

    public CurveDetail getDetail() {
        return detail;
    }

    public void setDetail(CurveDetail detail) {
        this.detail = detail;
    }
}
