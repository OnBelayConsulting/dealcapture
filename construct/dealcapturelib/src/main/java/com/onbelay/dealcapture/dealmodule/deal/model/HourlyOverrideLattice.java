package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HourlyOverrideLattice {

    private LocalDate dayDate;
    
    private DealHourByDaySnapshot quantityDealHourByDaySnapshot;
    private DealHourByDaySnapshot priceDealHourByDaySnapshot;
    
    private Map<String, DealHourByDaySnapshot> dealHourCosts = new HashMap<>();

    public LocalDate getDayDate() {
        return dayDate;
    }

    public void setDayDate(LocalDate dayDate) {
        this.dayDate = dayDate;
    }

    public DealHourByDaySnapshot getQuantityDealHourByDaySnapshot() {
        return quantityDealHourByDaySnapshot;
    }

    public void setQuantityDealHourByDaySnapshot(DealHourByDaySnapshot quantityDealHourByDaySnapshot) {
        this.quantityDealHourByDaySnapshot = quantityDealHourByDaySnapshot;
    }

    public DealHourByDaySnapshot getPriceDealHourByDaySnapshot() {
        return priceDealHourByDaySnapshot;
    }

    public void setPriceDealHourByDaySnapshot(DealHourByDaySnapshot priceDealHourByDaySnapshot) {
        this.priceDealHourByDaySnapshot = priceDealHourByDaySnapshot;
    }

    public Map<String, DealHourByDaySnapshot> getDealHourCosts() {
        return dealHourCosts;
    }

    public void setDealHourCosts(Map<String, DealHourByDaySnapshot> dealHourCosts) {
        this.dealHourCosts = dealHourCosts;
    }

    public void addCostDealHourByDaySnapshot(DealHourByDaySnapshot dd) {
        dealHourCosts.put(dd.getDetail().getDaySubTypeCodeValue(), dd);
    }
}
