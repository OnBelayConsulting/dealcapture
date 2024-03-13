package com.onbelay.dealcapture.dealmodule.deal.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealDay;
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DealDayRepository {
    DealDay load(EntityId entityId);

    List<DealDay> fetchDealDays(
            Integer dealId,
            DayTypeCode code);

    List<DealDay> fetchDealDays(Integer dealId);

    List<DealDayView> fetchDealDayViewsByType(
            EntityId dealId,
            DayTypeCode dayTypeCode,
            LocalDateTime fromDate,
            LocalDateTime toDate);

    List<DealDayView> fetchDealDayViews(EntityId dealId);

    List<DealDayView> fetchAllDealDayViewsByDates(
            List<Integer> dealIds,
            LocalDate fromDate,
            LocalDate toDate);
}
