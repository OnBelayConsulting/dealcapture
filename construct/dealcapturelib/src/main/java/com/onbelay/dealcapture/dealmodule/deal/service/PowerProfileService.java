package com.onbelay.dealcapture.dealmodule.deal.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;

import java.time.LocalDateTime;
import java.util.List;

public interface PowerProfileService {
    public static final String BEAN_NAME = "powerProfileService";

    public QuerySelectedPage findPowerProfileIds(DefinedQuery definedQuery);

    public List<PowerProfileSnapshot> findByIds(QuerySelectedPage selectedPage);

    void assignPositionIdentifierToPowerProfiles(
            String positionGenerationIdentifier,
            List<Integer> powerProfileIds);

    List<PowerProfileSnapshot> getAssignedPowerProfiles(String positionGenerationIdentifier);

    void updatePositionStatusToComplete(
            List<Integer> powerProfileIds,
            LocalDateTime observedDateTime);


    public TransactionResult save(PowerProfileSnapshot snapshot);

    public TransactionResult save(List<PowerProfileSnapshot> snapshots);

    public PowerProfileSnapshot load(EntityId entityId);

    void updatePositionGenerationStatusToPending(List<Integer> powerProfileIds);
}
