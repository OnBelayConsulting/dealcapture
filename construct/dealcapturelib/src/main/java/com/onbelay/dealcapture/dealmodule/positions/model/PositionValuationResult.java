package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionSettlementDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class PositionValuationResult extends BaseValuationResult {
    private static Logger logger = LogManager.getLogger();

    private List<HourlyPositionValuationResult> hourlyPositionValuationResults = new ArrayList<>();

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

    public List<HourlyPositionValuationResult> getHourlyPositionResults() {
        return hourlyPositionValuationResults;
    }

    public void addHourlyPositionResult(HourlyPositionValuationResult hourlyPositionValuationResult) {
        hourlyPositionValuationResults.add(hourlyPositionValuationResult);
    }
}
