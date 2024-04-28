package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonthView;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

public class DealDaysContainer {

    private Integer dealId;

    private HashMap<LocalDate, DealDayByMonthView> dayPriceByDateMap = new HashMap<>();
    private HashMap<LocalDate, DealDayByMonthView> dayQuantityByDateMap = new HashMap<>();
    private HashMap<LocalDate, CostDayByMonthContainer> dayCostByDateMap = new HashMap<>();

    private HashMap<LocalDate, DealHourByDayView> hourPriceByDateMap = new HashMap<>();
    private HashMap<LocalDate, DealHourByDayView> hourQuantityByDateMap = new HashMap<>();
    private HashMap<LocalDate, CostHourByDayContainer> hourCostByDateMap = new HashMap<>();


    public DealDaysContainer(Integer dealId) {
        this.dealId = dealId;
    }

    public boolean hasDayByMonthCosts(LocalDate positionDate) {
        return dayCostByDateMap.containsKey(positionDate.withDayOfMonth(1));
    }

    public boolean hasDayByMonthQuantity(LocalDate positionDate) {
        return dayQuantityByDateMap.containsKey(positionDate.withDayOfMonth(1));
    }

    public boolean hasDayByMonthPrice(LocalDate positionDate) {
        return dayPriceByDateMap.containsKey(positionDate.withDayOfMonth(1));
    }

    public boolean hasHourByDayQuantity(LocalDate positionDate) {
        return hourQuantityByDateMap.containsKey(positionDate);
    }

    public boolean hasHourByDayPrice(LocalDate positionDate) {
        return hourPriceByDateMap.containsKey(positionDate);
    }

    public boolean hasHourByDayCosts(LocalDate positionDate) {
        return hourCostByDateMap.containsKey(positionDate);
    }


    public void processDealHourByDayView(DealHourByDayView view) {
        switch (view.getDetail().getDealDayTypeCode()) {

            case PRICE -> {
                hourPriceByDateMap.put(
                        view.getDetail().getDealDayDate(),
                        view);
            }

            case QUANTITY -> {
                hourQuantityByDateMap.put(
                        view.getDetail().getDealDayDate(),
                        view);
            }

            case COST -> {
                CostHourByDayContainer container = hourCostByDateMap.get(view.getDetail().getDealDayDate());
                if (container == null) {
                    container = new CostHourByDayContainer();
                    hourCostByDateMap.put(view.getDetail().getDealDayDate(), container);
                }
                container.processView(view);
            }

        }
    }

    public void processDealDayByMonthView(DealDayByMonthView view) {

        switch (view.getDetail().getDealDayTypeCode()) {

            case PRICE -> {
                dayPriceByDateMap.put(
                        view.getDetail().getDealMonthDate(),
                        view);
            }

            case QUANTITY -> {
                dayQuantityByDateMap.put(
                        view.getDetail().getDealMonthDate(),
                        view);
            }

            case COST -> {
                CostDayByMonthContainer container = dayCostByDateMap.get(view.getDetail().getDealMonthDate());
                if (container == null) {
                    container = new CostDayByMonthContainer();
                    dayCostByDateMap.put(view.getDetail().getDealMonthDate(), container);
                }
                container.processView(view);
            }

        }
    }

    public BigDecimal getDayQuantity(LocalDate positionDate) {
        int day = positionDate.getDayOfMonth();
        LocalDate firstOfMonth = positionDate.withDayOfMonth(1);
        DealDayByMonthView view = dayQuantityByDateMap.get(firstOfMonth);
        return view.getDetail().getDayValue(day);
    }


    public BigDecimal getDayPrice(LocalDate positionDate) {
        int day = positionDate.getDayOfMonth();
        LocalDate firstOfMonth = positionDate.withDayOfMonth(1);
        DealDayByMonthView view = dayPriceByDateMap.get(firstOfMonth);
        return view.getDetail().getDayValue(day);
    }

    public BigDecimal getDayCost(LocalDate positionDate, String costName) {
        int day = positionDate.getDayOfMonth();
        LocalDate firstOfMonth = positionDate.withDayOfMonth(1);
        CostDayByMonthContainer container = dayCostByDateMap.get(firstOfMonth);
        return container.getCost(day, costName);
    }

    public BigDecimal getHourPrice(LocalDate positionDate, int hourEnding)  {
        DealHourByDayView view = hourPriceByDateMap.get(positionDate);
        if (view == null) {
            return null;
        }
        return view.getDetail().getHourValue(hourEnding);
    }

    public BigDecimal getHourQuantity(LocalDate positionDate, int hourEnding)  {
        DealHourByDayView view = hourQuantityByDateMap.get(positionDate);
        if (view == null) {
            return null;
        }
        return view.getDetail().getHourValue(hourEnding);
    }


    public BigDecimal getHourCost(
            LocalDate positionDate,
            String costName,
            int hourEnding) {
        CostHourByDayContainer container = hourCostByDateMap.get(positionDate);

        if (container == null) {
            return null;
        }

        return container.getCost(
                hourEnding,
                costName);
    }


    public Integer getDealId() {
        return dealId;
    }
}
