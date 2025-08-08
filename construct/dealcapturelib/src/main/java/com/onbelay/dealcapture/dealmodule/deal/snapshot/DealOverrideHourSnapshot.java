package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DealOverrideHourSnapshot {
    private LocalDate dayDate;
    private Integer hourEnding;
    private List<BigDecimal> values  = new ArrayList<>(24);

    public DealOverrideHourSnapshot() {
    }

    public DealOverrideHourSnapshot(
            LocalDate overrideDate,
            Integer hourEnding,
            int numberOfValues) {

        this.dayDate = overrideDate;
        this.hourEnding = hourEnding;
        initializeValuesSize(numberOfValues);
    }

    public void initializeValuesSize(int numberOfValues) {
        for (int i = 0; i < numberOfValues; i++) {
            values.add(null);
        }
    }

    public List<BigDecimal> getValues() {
        return values;
    }

    public void setValues(List<BigDecimal> values) {
        this.values = values;
    }

    public void setValueAt(int index, BigDecimal value) {
        values.set(index, value);
    }

    public void setDayValue(int i, BigDecimal value) {
        values.add(i, value);
    }

    public void setDayDate(LocalDate dayDate) {
        this.dayDate = dayDate;
    }

    public void setHourEnding(Integer hourEnding) {
        this.hourEnding = hourEnding;
    }

    public LocalDate getDayDate() {
        return dayDate;
    }

    public Integer getHourEnding() {
        return hourEnding;
    }
}
