package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DealOverrideSnapshot extends AbstractSnapshot {

    private LocalDate startDate;
    private LocalDate endDate;

    private List<String> headings = new ArrayList<>();

    private List<DealOverrideMonthSnapshot> overrideMonths = new ArrayList<>();

    public DealOverrideSnapshot() {
    }

    public DealOverrideSnapshot(String errorCode) {
        super(errorCode);
    }

    public DealOverrideSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public DealOverrideSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void addQuantityHeading() {
        headings.add(DayTypeCode.QUANTITY.getCode());
    }

    public boolean hasQuantityHeading() {
        return headings.stream().anyMatch(c -> c.equalsIgnoreCase(DayTypeCode.QUANTITY.getCode()));
    }

    public int indexOfQuantityHeading() {
        return headings.indexOf(DayTypeCode.QUANTITY.getCode());
    }


    public void addPriceHeading() {
        headings.add(DayTypeCode.PRICE.getCode());
    }

    public boolean hasPriceHeading() {
        return headings.stream().anyMatch(c -> c.equalsIgnoreCase(DayTypeCode.PRICE.getCode()));
    }

    public int indexOfPriceHeading() {
        return headings.indexOf(DayTypeCode.PRICE.getCode());
    }

    public void addCostHeading(String costHeading) {
        headings.add(costHeading);
    }

    public boolean hasCostHeading(String costHeading) {
        return headings.stream().anyMatch(c -> c.equalsIgnoreCase(costHeading));
    }

    public int indexOfCostHeading(String costHeading) {
        return headings.indexOf(costHeading);
    }

    public List<String> getHeadings() {
        return headings;
    }

    public void setHeadings(List<String> headings) {
        this.headings = headings;
    }

    public void addOverrideMonth(DealOverrideMonthSnapshot snapshot) {
        overrideMonths.add(snapshot);
    }

    public List<DealOverrideMonthSnapshot> getOverrideMonths() {
        return overrideMonths;
    }

    public void setOverrideMonths(List<DealOverrideMonthSnapshot> overrideMonths) {
        this.overrideMonths = overrideMonths;
    }

    public void addCostHeadings(List<String> costHeadings) {
        headings.addAll(costHeadings);
    }

    public void addDealOverride(DealOverrideDaySnapshot daySnapshot) {

        DealOverrideMonthSnapshot monthSnapshot = overrideMonths
                .stream()
                .filter( c-> c.getMonthDate().equals(daySnapshot.getMonthDate()))
                .findFirst().orElse(null);
        if (monthSnapshot == null) {
            monthSnapshot = new DealOverrideMonthSnapshot();
            monthSnapshot.setHeadings(headings);
            monthSnapshot.setMonthDate(daySnapshot.getMonthDate());
            overrideMonths.add(monthSnapshot);
        }
        monthSnapshot.getOverrideDays().add(daySnapshot);

    }
}
