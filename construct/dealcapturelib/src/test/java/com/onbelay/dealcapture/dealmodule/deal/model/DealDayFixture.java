package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDaySnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DealDayFixture {

    public static DealDay createDayQuantity(
            BaseDeal deal,
            LocalDate dayDate,
            Integer dayOfMonth,
            BigDecimal quantity) {

        DealDaySnapshot snapshot = new DealDaySnapshot();
        snapshot.getDetail().setDealDayTypeCode(DayTypeCode.QUANTITY);
        snapshot.getDetail().setDealDayDate(dayDate);
        snapshot.getDetail().setDayValue(dayOfMonth, quantity);
        DealDay dealDay = new DealDay();
        dealDay.createWith(deal, snapshot);
        return dealDay;
    }

}
