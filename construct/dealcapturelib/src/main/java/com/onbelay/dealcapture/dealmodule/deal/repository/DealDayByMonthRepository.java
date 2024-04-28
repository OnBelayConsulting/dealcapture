package com.onbelay.dealcapture.dealmodule.deal.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonth;
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonthView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DealDayByMonthRepository {
    public static final String BEAN_NAME = "dealDayByMonthRepository";
    DealDayByMonth load(EntityId entityId);

    List<DealDayByMonth> fetchDealDayByMonths(
            Integer dealId,
            DayTypeCode code);

    List<DealDayByMonth> fetchDealDayByMonths(Integer dealId);

    List<DealDayByMonthView> fetchDealDayViewsByType(
            EntityId dealId,
            DayTypeCode dayTypeCode,
            LocalDateTime fromDate,
            LocalDateTime toDate);

    List<DealDayByMonthView> fetchDealDayViews(EntityId dealId);

    List<DealDayByMonthView> fetchAllDealDayViewsByDates(
            List<Integer> dealIds,
            LocalDate fromDate,
            LocalDate toDate);
}
