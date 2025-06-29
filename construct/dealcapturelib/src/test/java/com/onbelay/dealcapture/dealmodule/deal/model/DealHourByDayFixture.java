package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DealHourByDayFixture {

    public static DealHourByDay createHourByDayQuantity(
            BaseDeal deal,
            LocalDate dayDate,
            int startHour,
            int endHour,
            BigDecimal quantity) {

        DealHourByDaySnapshot snapshot = new DealHourByDaySnapshot();
        snapshot.getDetail().setDealDayTypeCode(DayTypeCode.QUANTITY);
        snapshot.getDetail().setDealDayDate(dayDate);
        for (int i = startHour; i <= endHour; i++) {
            snapshot.getDetail().setHourValue(i, quantity);
        }
        DealHourByDay dealHourByDay = new DealHourByDay();
        dealHourByDay.createWith(deal, snapshot);
        return dealHourByDay;
    }



    public static DealHourByDay createHourByDayPrices(
            BaseDeal deal,
            LocalDate dayDate,
            int startHour,
            int endHour,
            BigDecimal price) {

        DealHourByDaySnapshot snapshot = new DealHourByDaySnapshot();
        snapshot.getDetail().setDealDayTypeCode(DayTypeCode.PRICE);
        snapshot.getDetail().setDealDayDate(dayDate);
        for (int i = startHour; i <= endHour; i++) {
            snapshot.getDetail().setHourValue(i, price);
        }
        DealHourByDay dealHourByDay = new DealHourByDay();
        dealHourByDay.createWith(deal, snapshot);
        return dealHourByDay;
    }

}
