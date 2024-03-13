package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

public class DealDaysContainer {

    private Integer dealId;

    private HashMap<LocalDate, DealDayView> priceByDateMap = new HashMap<>();
    private HashMap<LocalDate, DealDayView> quantityByDateMap = new HashMap<>();

    private HashMap<LocalDate, CostDaysContainer> costByDateMap = new HashMap<>();

    public DealDaysContainer(Integer dealId) {
        this.dealId = dealId;
    }

    public boolean hasMonthCosts(LocalDate positionDate) {
        return costByDateMap.containsKey(positionDate.withDayOfMonth(1));
    }

    public boolean hasMonthQuantity(LocalDate positionDate) {
        return quantityByDateMap.containsKey(positionDate.withDayOfMonth(1));
    }

    public boolean hasMonthPrice(LocalDate positionDate) {
        return priceByDateMap.containsKey(positionDate.withDayOfMonth(1));
    }

    public void processDealDayView(DealDayView view) {
        if (view.getDetail().getDealDayTypeCode() == DayTypeCode.PRICE) {
            priceByDateMap.put(view.getDetail().getDealDayDate(), view);
        } else if (view.getDetail().getDealDayTypeCode() == DayTypeCode.QUANTITY) {
            quantityByDateMap.put(view.getDetail().getDealDayDate(), view);
        } else {
            CostDaysContainer container = costByDateMap.get(view.getDetail().getDealDayDate());
            if (container == null) {
                container = new CostDaysContainer();
                costByDateMap.put(view.getDetail().getDealDayDate(), container);
            }
            container.processDealDayView(view);
        }
    }

    public BigDecimal getDayQuantity(LocalDate positionDate) {
        int day = positionDate.getDayOfMonth();
        LocalDate firstOfMonth = positionDate.withDayOfMonth(1);
        DealDayView view = quantityByDateMap.get(firstOfMonth);
        return view.getDetail().getDayValue(day);
    }


    public BigDecimal getDayPrice(LocalDate positionDate) {
        int day = positionDate.getDayOfMonth();
        LocalDate firstOfMonth = positionDate.withDayOfMonth(1);
        DealDayView view = priceByDateMap.get(firstOfMonth);
        return view.getDetail().getDayValue(day);
    }

    public BigDecimal getDayCost(LocalDate positionDate, String costName) {
        int day = positionDate.getDayOfMonth();
        LocalDate firstOfMonth = positionDate.withDayOfMonth(1);
        CostDaysContainer container = costByDateMap.get(firstOfMonth);
        return container.getDayCost(day, costName);
    }

    public Integer getDealId() {
        return dealId;
    }
}
