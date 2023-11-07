package com.onbelay.dealcapture.dealmodule.positions.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;

import java.util.List;

public interface DealPositionRepository {
    public static final String BEAN_NAME = "dealPositionsRepository";

    DealPosition load(EntityId entityId);

    List<DealPosition> findByDeal(EntityId dealEntityId);

    List<Integer> findPositionIds(DefinedQuery definedQuery);

    List<DealPosition> fetchByIds(QuerySelectedPage page);
}
