package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;

import java.time.LocalDateTime;

public interface ValuePositionsService {
    TransactionResult valuePositions(
            EntityId dealId,
            LocalDateTime currentDateTime);

    TransactionResult valuePositions(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime);
}
