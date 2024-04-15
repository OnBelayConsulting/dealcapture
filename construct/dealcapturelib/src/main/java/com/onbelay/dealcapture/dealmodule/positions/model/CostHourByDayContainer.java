package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayView;

import java.math.BigDecimal;
import java.util.HashMap;

public class CostHourByDayContainer {

    private HashMap<String, DealHourByDayView> costNameMap = new HashMap<>();

    public void processView(DealHourByDayView view) {
        costNameMap.put(view.getDetail().getDaySubTypeCodeValue(), view);
    }

    public BigDecimal getCost(
            Integer hour,
            String name) {
        DealHourByDayView view = costNameMap.get(name);
        if (view == null)
            return null;

        return view.getDetail().getHourValue(hour);

    }
}
