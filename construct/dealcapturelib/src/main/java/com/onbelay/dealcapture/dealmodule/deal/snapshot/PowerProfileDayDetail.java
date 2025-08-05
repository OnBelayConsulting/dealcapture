package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
@Embeddable
public class PowerProfileDayDetail {

    private Integer dayOfWeek;

    private List<String> hours = new ArrayList<>(24);
    
    public PowerProfileDayDetail() {
    }

    public void setDefaults() {
        for (int i = 1; i < 25; i++)
            hours.add(PowerFlowCode.NONE.getCode());
    }

    @Transient
    public List<String> getHours() {
        return hours;
    }

    public void setHours(List<String> hours) {
        this.hours = hours;
    }

    @Transient
    @JsonIgnore
    public PowerFlowCode getPowerFlowCode(int hourEnding) {
        return PowerFlowCode.lookUp(hours.get(hourEnding - 1));
    }

    public void setPowerFlowCode(int hourEnding, PowerFlowCode powerFlowCode) {
        assert(powerFlowCode != null);
        hours.set( hourEnding - 1, powerFlowCode.getCode());
    }

    public void copyFrom(PowerProfileDayDetail copy) {

        if (copy.dayOfWeek != null)
            this.dayOfWeek = copy.dayOfWeek;
        this.hours.clear();
        hours.addAll(copy.hours);
    }

    @Column(name = "DAY_OF_WEEK")
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


}
