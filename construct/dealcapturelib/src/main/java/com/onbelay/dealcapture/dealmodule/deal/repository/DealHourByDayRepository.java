package com.onbelay.dealcapture.dealmodule.deal.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDay;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DealHourByDayRepository {
    public static final String BEAN_NAME = "dealHourByDayRepository";
    DealHourByDay load(EntityId entityId);

    List<DealHourByDay> fetchDealHourByDays(
            Integer dealId,
            DayTypeCode code);

    List<DealHourByDay> fetchDealHourByDays(Integer dealId);

    List<DealHourByDay> fetchDealHourByDayForADay(
            Integer dealId,
            LocalDate dayDate);

    List<DealHourByDayView> fetchDealHourByDayViewsByType(
            EntityId dealId,
            DayTypeCode dayTypeCode,
            LocalDateTime fromDate,
            LocalDateTime toDate);

    List<DealHourByDayView> fetchDealHourByDayViews(EntityId dealId);

    List<DealHourByDayView> fetchAllDealHourByDayViewsByDates(
            List<Integer> dealIds,
            LocalDate fromDate,
            LocalDate toDate);
}
