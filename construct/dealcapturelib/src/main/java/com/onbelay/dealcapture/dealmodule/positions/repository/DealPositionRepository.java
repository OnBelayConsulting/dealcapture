package com.onbelay.dealcapture.dealmodule.positions.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDateTime;
import java.util.List;

public interface DealPositionRepository {
    public static final String BEAN_NAME = "dealPositionsRepository";

    DealPosition load(EntityId entityId);

    long reserveSequenceRange(String sequenceName, int rangeSize);

    List<DealPosition> findByDeal(EntityId dealEntityId);

    List<Integer> findIdsByDeal(EntityId dealEntityId);

    List<Integer> findPositionIds(DefinedQuery definedQuery);

    List<DealPositionView> findDealPositionViews(
            List<Integer> dealIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime);

    List<DealPosition> fetchByIds(QuerySelectedPage page);
}
