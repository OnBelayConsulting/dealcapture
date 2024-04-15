package com.onbelay.dealcapture.dealmodule.positions.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.CostPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.CostPositionView;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDateTime;
import java.util.List;

public interface CostPositionRepository {
    public static final String BEAN_NAME = "costPositionRepository";

    CostPosition load(EntityId entityId);

    List<CostPosition> findByDeal(EntityId dealId);

    List<CostPositionView> findCostPositionViewsWithFX(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);

    List<TotalCostPositionSummary> calculateTotalCostSummaries(
            Integer dealId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);

    List<TotalCostPositionSummary> calculateTotalCostSummaries(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);


    List<Integer> findCostPositionIds(DefinedQuery definedQuery);

    List<Integer> findCostPositionIdsByDeal(EntityId dealId);

    List<CostPosition> fetchByIds(QuerySelectedPage page);
}
