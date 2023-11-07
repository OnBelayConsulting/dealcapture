package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;

import java.util.List;

public class ErrorDealPositionSnapshot extends DealPositionSnapshot {

    public ErrorDealPositionSnapshot() {
        super(DealTypeCode.ERROR);
    }

    public ErrorDealPositionSnapshot(String message) {
        super(DealTypeCode.ERROR, message);

    }

    public ErrorDealPositionSnapshot(String errorCode, boolean isPermissionException) {
        super(
                DealTypeCode.ERROR,
                errorCode,
                isPermissionException);
    }

    public ErrorDealPositionSnapshot(
            String errorCode,
            List<String> parameters) {
        super(
                DealTypeCode.ERROR,
                errorCode,
                parameters);
    }


}
