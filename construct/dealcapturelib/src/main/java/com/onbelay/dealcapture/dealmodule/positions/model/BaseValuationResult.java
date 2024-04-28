package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseValuationResult {
    private static Logger logger = LogManager.getLogger();
    private static String SUCCESS = "SUCCESS";

    private Integer domainId;
    private LocalDateTime currentDateTime;
    private PositionErrorCode errorCode = PositionErrorCode.SUCCESS;

    private List<String> errorMessages = new ArrayList<>();

    public BaseValuationResult(
            Integer domainId,
            LocalDateTime currentDateTime) {
        this.domainId = domainId;
        this.currentDateTime = currentDateTime;
    }

    public Integer getDomainId() {
        return domainId;
    }

    public LocalDateTime getCurrentDateTime() {
        return currentDateTime;
    }

    public boolean hasErrors() {
        return errorCode != PositionErrorCode.SUCCESS;
    }

    public String getCompleteErrorCodeMessage() {
        if (errorMessages.isEmpty())
            return SUCCESS;

        if (errorMessages.size() > 10) {
            logger.error("Error message exceeds error message and will be truncated in database.");
            logger.error(errorMessages.toString());
            return errorMessages.stream().limit(10).collect(Collectors.joining(","));
        } else {
            return errorMessages.stream().collect(Collectors.joining(","));
        }
    }

    public PositionErrorCode getErrorCode() {
        return errorCode;
    }

    public void addErrorMessage(PositionErrorCode positionErrorCode) {
        this.errorCode = PositionErrorCode.ERROR_INVALID_POSITION_VALUATION;
        errorMessages.add(positionErrorCode.getDescription());
    }
}
