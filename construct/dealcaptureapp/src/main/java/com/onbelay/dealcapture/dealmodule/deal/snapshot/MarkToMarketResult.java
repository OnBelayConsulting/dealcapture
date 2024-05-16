package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.ErrorHoldingSnapshot;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MarkToMarketResult extends ErrorHoldingSnapshot {

    private String positionGenerationIdentifier;
    private LocalDateTime startDateTime;
    private Long days;
    private Long hours;
    private Long minutes;
    private Long seconds;
    private Long milliseconds;
    private String formattedElapsedTime;

    public MarkToMarketResult(LocalDateTime startDateTime, String positionGenerationIdentifier) {
    }

    public MarkToMarketResult(String errorCode) {
        super(errorCode);
    }

    public MarkToMarketResult(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public MarkToMarketResult(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public String getPositionGenerationIdentifier() {
        return positionGenerationIdentifier;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void computeElapsedTime(LocalDateTime endDateTime) {
        days = startDateTime.until(endDateTime, ChronoUnit.DAYS);
        hours = startDateTime.until(endDateTime, ChronoUnit.HOURS);
        minutes = startDateTime.until(endDateTime, ChronoUnit.MINUTES);
        seconds = startDateTime.until(endDateTime, ChronoUnit.SECONDS);
        milliseconds = startDateTime.until(endDateTime, ChronoUnit.MILLIS);
        formattedElapsedTime =  "Days: " + days + " Hours: " + hours + " Minutes: " + minutes + " Seconds: " + seconds;
    }

    public String getFormattedElapsedTime() {
        return formattedElapsedTime;
    }

    public void setFormattedElapsedTime(String formmattedElapsedTime) {
        this.formattedElapsedTime = formmattedElapsedTime;
    }
}
