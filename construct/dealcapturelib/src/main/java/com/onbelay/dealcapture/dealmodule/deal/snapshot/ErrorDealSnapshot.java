package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

import java.util.List;

public class ErrorDealSnapshot extends BaseDealSnapshot{

    public ErrorDealSnapshot() {
        super(DealTypeCode.ERROR);
    }

    public ErrorDealSnapshot(String message) {
        super(DealTypeCode.ERROR, message);

    }

    public ErrorDealSnapshot(String errorCode, boolean isPermissionException) {
        super(
                DealTypeCode.ERROR,
                errorCode,
                isPermissionException);
    }

    public ErrorDealSnapshot(
            String errorCode,
            List<String> parameters) {

        super(
                DealTypeCode.ERROR,
                errorCode,
                parameters);
    }
}
