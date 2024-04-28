package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonthView;

import java.math.BigDecimal;
import java.util.HashMap;

public class CostDayByMonthContainer {

    private HashMap<String, DealDayByMonthView> costNameMap = new HashMap<>();

    public void processView(DealDayByMonthView view) {
        costNameMap.put(view.getDetail().getDaySubTypeCodeValue(), view);
    }

    public BigDecimal getCost(Integer day, String name) {
        DealDayByMonthView view = costNameMap.get(name);
        if (view == null)
            return null;

        return view.getDetail().getDayValue(day);

    }
}
