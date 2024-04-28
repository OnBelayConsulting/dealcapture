package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

public class PowerProfileIndexMappingDetail {

    private String powerFlowCodeValue;

    public void copyFrom(PowerProfileIndexMappingDetail copy) {
        if (copy.powerFlowCodeValue != null)
            this.powerFlowCodeValue = copy.powerFlowCodeValue;
    }

    @Transient
    @JsonIgnore
    public PowerFlowCode getPowerFlowCode() {
        return PowerFlowCode.valueOf(powerFlowCodeValue);
    }

    public void setPowerFlowCode(PowerFlowCode powerFlowCode) {
        this.powerFlowCodeValue = powerFlowCode.toString();
    }

    @Column(name = "POWER_FLOW_CODE")
    public String getPowerFlowCodeValue() {
        return powerFlowCodeValue;
    }

    public void setPowerFlowCodeValue(String powerFlowCodeValue) {
        this.powerFlowCodeValue = powerFlowCodeValue;
    }
}
