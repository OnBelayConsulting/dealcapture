package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealType;

import java.util.List;

public class ErrorDealSnapshot extends BaseDealSnapshot{

    public ErrorDealSnapshot() {
        super(DealType.ERROR);
    }

    public ErrorDealSnapshot(String message) {
        super(DealType.ERROR, message);

    }

    public ErrorDealSnapshot(String errorCode, boolean isPermissionException) {
        super(
                DealType.ERROR,
                errorCode,
                isPermissionException);
    }

    public ErrorDealSnapshot(
            String errorCode,
            List<String> parameters) {

        super(
                DealType.ERROR,
                errorCode,
                parameters);
    }
}
