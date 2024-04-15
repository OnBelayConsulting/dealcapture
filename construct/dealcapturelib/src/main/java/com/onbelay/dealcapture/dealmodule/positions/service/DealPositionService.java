package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.CostPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDateTime;
import java.util.List;

public interface DealPositionService {

    public TransactionResult saveDealPositions(
            String positionGeneratorIdentifier,
            List<DealPositionSnapshot> positions);

    List<DealPositionView> fetchDealPositionViews(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);

    List<CostPositionView> fetchCostPositionViewsWithFX(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);

    List<DealPositionSnapshot> findPositionsByDeal(EntityId dealId);

    List<DealHourlyPositionSnapshot> findHourlyPositionsByDeal(EntityId dealId);


    List<Integer> findIdsByDeal(EntityId entityId);

    List<Integer> findCostPositionIdsByDeal(EntityId dealId);

    List<CostPositionSnapshot> findCostPositionsByIds(QuerySelectedPage selectedPage);

    QuerySelectedPage findPositionIds(DefinedQuery definedQuery);

    List<DealPositionSnapshot> findByIds(QuerySelectedPage selectedPage);

    DealPositionSnapshot load(EntityId entityId);

    List<TotalCostPositionSummary> calculateTotalCostPositionSummaries(
            Integer dealId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);

    List<TotalCostPositionSummary> calculateTotalCostPositionSummaries(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);
}
