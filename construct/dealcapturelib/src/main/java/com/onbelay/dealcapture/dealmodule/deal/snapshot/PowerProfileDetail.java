package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;

public class PowerProfileDetail {

    private String name;
    private String description;
    private String positionGenerationStatusValue;
    private String positionGenerationIdentifier;
    private LocalDateTime positionGenerationDateTime;


    public void setDefaults() {
        this.setPositionGenerationStatusCode(PositionGenerationStatusCode.NONE);
    }

    public void copyFrom(PowerProfileDetail copy) {

        if (copy.name != null)
            this.name = copy.name;

        if (copy.description != null)
            this.description = copy.description;

        if (copy.positionGenerationStatusValue != null)
            this.positionGenerationStatusValue = copy.positionGenerationStatusValue;

        if (copy.positionGenerationIdentifier != null)
            this.positionGenerationIdentifier = copy.positionGenerationIdentifier;

        if (copy.positionGenerationDateTime != null)
            this.positionGenerationDateTime = copy.positionGenerationDateTime;

    }

    @Column(name = "PROFILE_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "PROFILE_DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    @JsonIgnore
    public PositionGenerationStatusCode getPositionGenerationStatusCode() {
        return PositionGenerationStatusCode.lookUp(positionGenerationStatusValue);
    }

    public void setPositionGenerationStatusCode(PositionGenerationStatusCode code) {
        if (code != null)
            this.positionGenerationStatusValue = code.getCode();
        else
            this.positionGenerationStatusValue = null;
    }

    @Column(name = "POSITION_GENERATION_STATUS_CODE")
    public String getPositionGenerationStatusValue() {
        return positionGenerationStatusValue;
    }

    public void setPositionGenerationStatusValue(String positionGenerationStatusValue) {
        this.positionGenerationStatusValue = positionGenerationStatusValue;
    }

    @Column(name = "POSITION_GENERATION_IDENTIFIER")
    public String getPositionGenerationIdentifier() {
        return positionGenerationIdentifier;
    }

    public void setPositionGenerationIdentifier(String positionGenerationIdentifier) {
        this.positionGenerationIdentifier = positionGenerationIdentifier;
    }

    @Column(name = "POSITION_GENERATION_DATE_TIME")
    public LocalDateTime getPositionGenerationDateTime() {
        return positionGenerationDateTime;
    }

    public void setPositionGenerationDateTime(LocalDateTime positionGenerationDateTime) {
        this.positionGenerationDateTime = positionGenerationDateTime;
    }

}
