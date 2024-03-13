package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.model.DealDayView;

import java.math.BigDecimal;
import java.util.HashMap;

public class CostDaysContainer {

    private HashMap<String, DealDayView> costNameMap = new HashMap<>();

    public void processDealDayView(DealDayView view) {
        costNameMap.put(view.getDetail().getDaySubTypeCodeValue(), view);
    }

    public BigDecimal getDayCost(Integer day, String name) {
        DealDayView view = costNameMap.get(name);
        if (view == null)
            return null;

        return view.getDetail().getDayValue(day);

    }
}
