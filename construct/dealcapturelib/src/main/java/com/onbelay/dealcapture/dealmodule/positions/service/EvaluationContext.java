package com.onbelay.dealcapture.dealmodule.positions.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EvaluationContext {

    private final LocalDate startPositionDate;
    private final LocalDate endPositionDate;
    private final LocalDateTime createdDateTime;

    public EvaluationContext(
        LocalDateTime createdDateTime,
        LocalDate startPositionDate,
        LocalDate endPositionDate) {

        this.createdDateTime = createdDateTime;
        this.startPositionDate = startPositionDate;
        this.endPositionDate = endPositionDate;
    }

    public boolean validate() {
        return startPositionDate != null && createdDateTime != null && endPositionDate != null;
    }

    public LocalDate getStartPositionDate() {
        return startPositionDate;
    }

    public LocalDate getEndPositionDate() {
        return endPositionDate;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

}
