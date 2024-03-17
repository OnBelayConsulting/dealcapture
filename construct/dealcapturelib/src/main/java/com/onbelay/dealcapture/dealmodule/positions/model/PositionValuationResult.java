package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionSettlementDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PositionValuationResult {
    private static Logger logger = LogManager.getLogger();
    private static String SUCCESS = "SUCCESS";

    private Integer positionId;
    private LocalDateTime currentDateTime;
    private PositionSettlementDetail settlementDetail = new PositionSettlementDetail();
    private List<PositionErrorCode> errorCodes = new ArrayList<>();

    public PositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime) {
        this.positionId = positionId;
        this.currentDateTime = currentDateTime;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public LocalDateTime getCurrentDateTime() {
        return currentDateTime;
    }

    public boolean hasErrors() {
        return errorCodes.isEmpty() == false;
    }

    public String getCompleteErrorCodeMessage() {
        if (errorCodes.isEmpty())
            return SUCCESS;

        if (errorCodes.size() > 10) {
            logger.error("Error message exceeds error message and will be truncated in database.");
            logger.error(errorCodes.toString());
            return errorCodes.stream().limit(10).map(c -> c.getCode()).collect(Collectors.joining(","));
        } else {
            return errorCodes.stream().map(c -> c.getCode()).collect(Collectors.joining(","));
        }
    }

    public PositionSettlementDetail getSettlementDetail() {
        return settlementDetail;
    }

    public void setSettlementDetail(PositionSettlementDetail settlementDetail) {
        this.settlementDetail = settlementDetail;
    }

    public List<PositionErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public void addErrorCode(PositionErrorCode positionErrorCode) {
        errorCodes.add(positionErrorCode);
    }
}
