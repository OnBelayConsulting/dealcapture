package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionSettlementDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

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
