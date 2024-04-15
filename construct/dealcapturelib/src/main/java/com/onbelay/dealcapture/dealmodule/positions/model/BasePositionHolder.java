package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

public abstract class BasePositionHolder {

    private DealPositionSnapshot dealPositionSnapshot;
    private ProfilePriceHourHolderMap dealPriceHourHolderMap = new ProfilePriceHourHolderMap();
    private ProfilePriceHourHolderMap marketPriceHourHolderMap = new ProfilePriceHourHolderMap();

    private DealHourByDayDetail dealHourByDayPrice = new DealHourByDayDetail();
    private DealHourByDayDetail dealHourByDayQuantity = new DealHourByDayDetail();

    public BasePositionHolder(DealPositionSnapshot dealPositionSnapshot) {
        this.dealPositionSnapshot = dealPositionSnapshot;
    }

    public ProfilePriceHourHolderMap getDealPriceHourHolderMap() {
        return dealPriceHourHolderMap;
    }

    public ProfilePriceHourHolderMap getMarketPriceHourHolderMap() {
        return marketPriceHourHolderMap;
    }

    public DealPositionSnapshot getDealPositionSnapshot() {
        return dealPositionSnapshot;
    }

    public DealHourByDayDetail getDealHourByDayPrice() {
        return dealHourByDayPrice;
    }

    public DealHourByDayDetail getDealHourByDayQuantity() {
        return dealHourByDayQuantity;
    }
}
