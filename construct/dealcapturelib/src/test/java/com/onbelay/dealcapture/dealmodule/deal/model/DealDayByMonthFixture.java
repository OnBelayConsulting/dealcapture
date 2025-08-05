package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthSnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DealDayByMonthFixture {

    public static DealDayByMonth createDayByMonthQuantity(
            BaseDeal deal,
            LocalDate dayDate,
            Integer dayOfMonth,
            BigDecimal quantity) {

        DealDayByMonthSnapshot snapshot = new DealDayByMonthSnapshot();
        snapshot.getDetail().setDealDayTypeCode(DayTypeCode.QUANTITY);
        snapshot.getDetail().setDealMonthDate(dayDate);
        snapshot.getDetail().setDayValue(dayOfMonth, quantity);
        DealDayByMonth dealDayByMonth = new DealDayByMonth();
        dealDayByMonth.createWith(deal, snapshot);
        return dealDayByMonth;
    }


    public static DealDayByMonth createDayByMonthPrice(
            BaseDeal deal,
            LocalDate dayDate,
            Integer dayOfMonth,
            BigDecimal price) {

        DealDayByMonthSnapshot snapshot = new DealDayByMonthSnapshot();
        snapshot.getDetail().setDealDayTypeCode(DayTypeCode.PRICE);
        snapshot.getDetail().setDealMonthDate(dayDate);
        snapshot.getDetail().setDayValue(dayOfMonth, price);
        DealDayByMonth dealDayByMonth = new DealDayByMonth();
        dealDayByMonth.createWith(deal, snapshot);
        return dealDayByMonth;
    }

}
