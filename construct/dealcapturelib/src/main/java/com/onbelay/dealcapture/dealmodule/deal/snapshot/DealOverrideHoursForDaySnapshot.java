package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DealOverrideHoursForDaySnapshot extends AbstractSnapshot {

    private List<String> headings = new ArrayList<>();
    private List<String> costHeadings = new ArrayList<>();
    private LocalDate dayDate;

    private List<DealOverrideHourSnapshot> overrideHours = new ArrayList<>();

    public DealOverrideHoursForDaySnapshot() {
    }

    public DealOverrideHoursForDaySnapshot(String errorCode) {
        super(errorCode);
    }

    public DealOverrideHoursForDaySnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public DealOverrideHoursForDaySnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public LocalDate getDayDate() {
        return dayDate;
    }

    public void setDayDate(LocalDate dayDate) {
        this.dayDate = dayDate;
    }


    public void createHourOverrides() {
        for (int i = 0; i < 24; i++) {
            overrideHours.add(new DealOverrideHourSnapshot(
                    dayDate,
                    i+1,
                    headings.size()));
        }
    }

    public List<DealOverrideHourSnapshot> getOverrideHours() {
        return overrideHours;
    }

    public DealOverrideHourSnapshot getOverrideHourAtHourEnding(int index) {
        return overrideHours.get(index-1);
    }

    public void setOverrideHours(List<DealOverrideHourSnapshot> overrideHours) {
        this.overrideHours = overrideHours;
    }

    public void processHeadings(List<String> headings) {
        this.headings = headings;
        this.costHeadings = headings
                .stream()
                .filter(c-> !(c.equalsIgnoreCase("PRICE") || c.equalsIgnoreCase("QUANTITY")) )
                .collect(Collectors.toList());
    }

    public List<String> getHeadings() {
        return headings;
    }

    public void setHeadings(List<String> headings) {
        this.headings = headings;
    }


    public void addQuantityHeading() {
        headings.add(DayTypeCode.QUANTITY.getCode());
    }

    @JsonIgnore
    public boolean hasQuantityHeading() {
        return headings.stream().anyMatch(c -> c.equalsIgnoreCase(DayTypeCode.QUANTITY.getCode()));
    }

    public int indexOfQuantityHeading() {
        return headings.indexOf(DayTypeCode.QUANTITY.getCode());
    }


    public void addPriceHeading() {
        headings.add(DayTypeCode.PRICE.getCode());
    }

    @JsonIgnore
    public boolean hasPriceHeading() {
        return headings.stream().anyMatch(c -> c.equalsIgnoreCase(DayTypeCode.PRICE.getCode()));
    }

    public int indexOfPriceHeading() {
        return headings.indexOf(DayTypeCode.PRICE.getCode());
    }

    public List<String> getCostHeadings() {
        return costHeadings;
    }

    public void setCostHeadings(List<String> costHeadings) {
        this.costHeadings = costHeadings;
    }

    @JsonIgnore
    public boolean hasCostHeading(String costHeading) {
        return headings.stream().anyMatch(c -> c.equalsIgnoreCase(costHeading));
    }

    public int indexOfCostHeading(String costHeading) {
        return headings.indexOf(costHeading);
    }


    public void addCostHeadings(List<String> costHeadings) {
        this.costHeadings = costHeadings;
        headings.addAll(costHeadings);
    }
}
