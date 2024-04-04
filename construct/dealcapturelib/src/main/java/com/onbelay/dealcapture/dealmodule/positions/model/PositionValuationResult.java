package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionSettlementDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PositionValuationResult extends BaseValuationResult {
    private static Logger logger = LogManager.getLogger();

    private PositionSettlementDetail settlementDetail = new PositionSettlementDetail();

    public PositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime) {
        super(positionId, currentDateTime);
    }

    public PositionSettlementDetail getSettlementDetail() {
        return settlementDetail;
    }

    public void setSettlementDetail(PositionSettlementDetail settlementDetail) {
        this.settlementDetail = settlementDetail;
    }
}
