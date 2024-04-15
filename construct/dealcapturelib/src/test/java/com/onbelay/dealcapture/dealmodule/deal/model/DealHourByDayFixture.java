package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DealHourByDayFixture {

    public static DealHourByDay createHourByDayQuantity(
            BaseDeal deal,
            LocalDate dayDate,
            Integer dayOfMonth,
            int startHour,
            int endHour,
            BigDecimal quantity) {

        DealHourByDaySnapshot snapshot = new DealHourByDaySnapshot();
        snapshot.getDetail().setDealDayTypeCode(DayTypeCode.QUANTITY);
        snapshot.getDetail().setDealDayDate(dayDate);
        for (int i = startHour; i <= endHour; i++) {
            snapshot.getDetail().setHourValue(dayOfMonth, quantity);
        }
        DealHourByDay dealHourByDay = new DealHourByDay();
        dealHourByDay.createWith(deal, snapshot);
        return dealHourByDay;
    }

}
