package com.onbelay.dealcapture.dealmodule.positions.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.DealHourlyPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.DealHourlyPositionView;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDateTime;
import java.util.List;

public interface DealHourlyPositionRepository {
    public static final String BEAN_NAME = "dealHourlyPositionRepository";

    DealHourlyPosition load(EntityId entityId);

    List<DealHourlyPosition> findByDeal(EntityId dealId);


    List<DealHourlyPositionView> findDealHourlyPositionViews(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);

    List<Integer> findDealHourlyPositionIds(DefinedQuery definedQuery);

    List<Integer> findDealHourlyPositionIdsByDeal(EntityId dealId);

    List<DealHourlyPosition> fetchByIds(QuerySelectedPage page);
}
