package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthSnapshot;

import java.time.LocalDate;
import java.util.HashMap;

public class OverrideLattice {

    private LocalDate monthStartDate;
    private LocalDate monthEndDate;
    private LocalDate monthDate;
    
    private DealDayByMonthSnapshot quantityDealDayByMonthSnapshot;
    private DealDayByMonthSnapshot priceDealDayByMonthSnapshot;
    
    private HashMap<String, DealDayByMonthSnapshot> dealDayCosts = new HashMap<>();

    public LocalDate getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(LocalDate monthDate) {
        this.monthDate = monthDate;
    }

    public DealDayByMonthSnapshot getQuantityDealDayByMonthSnapshot() {
        return quantityDealDayByMonthSnapshot;
    }

    public void setQuantityDealDayByMonthSnapshot(DealDayByMonthSnapshot quantityDealDayByMonthSnapshot) {
        this.quantityDealDayByMonthSnapshot = quantityDealDayByMonthSnapshot;
    }

    public DealDayByMonthSnapshot getPriceDealDayByMonthSnapshot() {
        return priceDealDayByMonthSnapshot;
    }

    public void setPriceDealDayByMonthSnapshot(DealDayByMonthSnapshot priceDealDayByMonthSnapshot) {
        this.priceDealDayByMonthSnapshot = priceDealDayByMonthSnapshot;
    }

    public HashMap<String, DealDayByMonthSnapshot> getDealDayCosts() {
        return dealDayCosts;
    }

    public void setDealDayCosts(HashMap<String, DealDayByMonthSnapshot> dealDayCosts) {
        this.dealDayCosts = dealDayCosts;
    }

    public void addCostDealDayByMonthSnapshot(DealDayByMonthSnapshot dd) {
        dealDayCosts.put(dd.getDetail().getDaySubTypeCodeValue(), dd);
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
}
