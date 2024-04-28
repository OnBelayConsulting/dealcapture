package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDateTime;

public interface ValuePowerProfilePositionsService {
    TransactionResult valuePositions(
            EntityId powerProfileId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime);

    TransactionResult valuePositions(
            DefinedQuery definedQuery,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime);
}
