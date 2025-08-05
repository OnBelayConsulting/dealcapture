package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DealOverrideMonthSnapshot extends AbstractSnapshot {

    private List<String> headings = new ArrayList<>();
    private LocalDate monthDate;

    private LocalDate monthStartDate;
    private LocalDate monthEndDate;

    private List<DealOverrideDaySnapshot> overrideDays = new ArrayList<>();

    public DealOverrideMonthSnapshot() {
    }

    public DealOverrideMonthSnapshot(String errorCode) {
        super(errorCode);
    }

    public DealOverrideMonthSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public DealOverrideMonthSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public LocalDate getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(LocalDate monthDate) {
        this.monthDate = monthDate;
    }


    public void createDayOverrides(int size) {
        LocalDate currentDate = monthStartDate;
        while (currentDate.isAfter(monthEndDate) == false) {
            DealOverrideDaySnapshot day = new DealOverrideDaySnapshot();
            day.setOverrideDate(currentDate);
            day.initializeValuesSize(size);
            overrideDays.add(day);
            currentDate = currentDate.plusDays(1);
        }
    }

    public LocalDate getMonthStartDate() {
        return monthStartDate;
    }

    public void setMonthStartDate(LocalDate monthStartDate) {
        this.monthStartDate = monthStartDate;
    }

    public LocalDate getMonthEndDate() {
        return monthEndDate;
    }

    public void setMonthEndDate(LocalDate monthEndDate) {
        this.monthEndDate = monthEndDate;
    }

    public List<DealOverrideDaySnapshot> getOverrideDays() {
        return overrideDays;
    }

    public void setOverrideDays(List<DealOverrideDaySnapshot> overrideDays) {
        this.overrideDays = overrideDays;
    }

    public void addDayOverride(DealOverrideDaySnapshot dayOverrideSnapshot) {
        this.overrideDays.add(dayOverrideSnapshot);
    }

    public List<String> getHeadings() {
        return headings;
    }

    public void setHeadings(List<String> headings) {
        this.headings = headings;
    }
}
