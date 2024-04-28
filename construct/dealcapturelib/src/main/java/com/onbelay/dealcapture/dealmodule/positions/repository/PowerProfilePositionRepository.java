package com.onbelay.dealcapture.dealmodule.positions.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePosition;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionView;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PowerProfilePositionRepository {
    public static final String BEAN_NAME = "powerProfilePositionRepository";

    PowerProfilePosition load(EntityId entityId);

    List<PowerProfilePosition> findByPowerProfile(EntityId powerProfileId);


    List<PowerProfilePositionView> findPowerProfilePositionViews(
            List<Integer> powerProfileIds,
            LocalDateTime createdDateTime);


    List<PowerProfilePositionView> findPowerProfilePositionViewsByDate(
            LocalDate startDate,
            LocalDate endDate,
            LocalDateTime createdDateTime);


    List<PowerProfilePosition> fetchByIds(QuerySelectedPage querySelectedPage);

    List<Integer> findPowerProfilePositionIds(DefinedQuery definedQuery);
}
