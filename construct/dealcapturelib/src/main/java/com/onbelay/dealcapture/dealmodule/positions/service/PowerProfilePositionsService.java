package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionView;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PowerProfilePositionsService {

    public TransactionResult savePowerProfilePositions(
            String positionGeneratorIdentifier,
            List<PowerProfilePositionSnapshot> positions);

    List<PowerProfilePositionView> fetchPowerProfilePositionViews(
            List<Integer> powerProfileIds,
            LocalDateTime createdDateTime);


    List<PowerProfilePositionView> fetchPowerProfilePositionViews(
            LocalDate startPositionDate,
            LocalDate endPositionDate,
            LocalDateTime createdDateTime);


    List<PowerProfilePositionSnapshot> findByPowerProfile(EntityId powerProfileId);

    QuerySelectedPage findPositionIds(DefinedQuery definedQuery);

    List<PowerProfilePositionSnapshot> findByIds(QuerySelectedPage selectedPage);

    PowerProfilePositionSnapshot load(EntityId entityId);

}
