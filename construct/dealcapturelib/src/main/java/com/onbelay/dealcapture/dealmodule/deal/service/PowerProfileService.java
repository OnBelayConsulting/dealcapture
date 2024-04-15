package com.onbelay.dealcapture.dealmodule.deal.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;

import java.util.List;

public interface PowerProfileService {
    public static final String BEAN_NAME = "powerProfileService";

    public QuerySelectedPage findPowerProfileIds(DefinedQuery definedQuery);

    public List<PowerProfileSnapshot> findByIds(QuerySelectedPage selectedPage);

    public TransactionResult save(PowerProfileSnapshot snapshot);

    public TransactionResult save(List<PowerProfileSnapshot> snapshots);

    public PowerProfileSnapshot load(EntityId entityId);

}
