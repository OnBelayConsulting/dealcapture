package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDayDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionSettlementDetail;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePositionHolder {

    private DealSummary  dealSummary;

    private DealPositionDetail dealPositionDetail = new DealPositionDetail();

    private PositionSettlementDetail settlementDetail = new PositionSettlementDetail();

    private DealHourByDayDetail dealHourByDayPrice = new DealHourByDayDetail();
    private DealHourByDayDetail dealHourByDayQuantity = new DealHourByDayDetail();

    private HourFixedValueDayDetail hourSlotsForPowerProfile;

    protected List<DealHourlyPositionHolder> hourlyPositionHolders = new ArrayList<>();


    public BasePositionHolder(DealSummary dealSummary) {
        this.dealSummary = dealSummary;
    }

    public DealHourByDayDetail getDealHourByDayPrice() {
        return dealHourByDayPrice;
    }

    public DealHourByDayDetail getDealHourByDayQuantity() {
        return dealHourByDayQuantity;
    }

    public DealSummary getDealSummary() {
        return dealSummary;
    }

    public DealPositionDetail getDealPositionDetail() {
        return dealPositionDetail;
    }

    public PositionSettlementDetail getSettlementDetail() {
        return settlementDetail;
    }

    public List<DealHourlyPositionHolder> getHourlyPositionHolders() {
        return hourlyPositionHolders;
    }

    public void setHourlyPositionHolders(List<DealHourlyPositionHolder> hourlyPositionHolders) {
        this.hourlyPositionHolders = hourlyPositionHolders;
    }

    public HourFixedValueDayDetail getHourSlotsForPowerProfile() {
        return hourSlotsForPowerProfile;
    }

    public void setHourSlotsForPowerProfile(HourFixedValueDayDetail hourSlotsForPowerProfile) {
        this.hourSlotsForPowerProfile = hourSlotsForPowerProfile;
    }
}
