package com.onbelay.dealcapture.dealmodule.positions.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PositionValuationResult {

    private Integer positionId;
    private LocalDateTime currentDateTime;
    private String errorCode = "SUCCESS";
    private BigDecimal mtmValue;

    public PositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime,
            String errorCode) {
        this.positionId = positionId;
        this.currentDateTime = currentDateTime;
        this.errorCode = errorCode;
    }

    public PositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime,
            BigDecimal mtmValue) {
        this.positionId = positionId;
        this.currentDateTime = currentDateTime;
        this.mtmValue = mtmValue;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public LocalDateTime getCurrentDateTime() {
        return currentDateTime;
    }

    public boolean hasError() {
        return "SUCCESS".equals(errorCode) == false;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public BigDecimal getMtmValue() {
        return mtmValue;
    }
}
