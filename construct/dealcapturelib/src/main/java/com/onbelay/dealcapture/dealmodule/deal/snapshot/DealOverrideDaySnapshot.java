package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DealOverrideDaySnapshot captures an array of override values for Quantity, Price and costs for a single day in a month (DealOverrideMonthSnapshot)
 * The cost override values are in order of the headings defined in the DealOverrideSnapshot parent.
 * A null value indicates that the value should be set to null in the override. All nulls should result in the DealDayByMonth to be deleted.
 */
public class DealOverrideDaySnapshot extends AbstractSnapshot {
    private LocalDate overrideDate;
    private List<BigDecimal> values  = new ArrayList<>();


    public void initializeValuesSize(int size) {
        for (int i = 0; i < size; i++) {
            values.add(null);
        }
    }

    public Integer getDayOfMonth() {
        return overrideDate.getDayOfMonth();
    }

    public void setOverrideDateWithDayOfMonth(
            LocalDate monthDate,
            Integer dayOfMonth) {
        this.overrideDate = monthDate.withDayOfMonth(dayOfMonth);
    }

    public LocalDate getMonthDate() {
        return overrideDate.withDayOfMonth(1);
    }

    public List<BigDecimal> getValues() {
        return values;
    }

    public void setValues(List<BigDecimal> values) {
        this.values = values;
    }


    public void setDayValue(int i, BigDecimal value) {
        values.add(i, value);
    }

    public LocalDate getOverrideDate() {
        return overrideDate;
    }

    public void setOverrideDate(LocalDate overrideDate) {
        this.overrideDate = overrideDate;
    }
}
