package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ValuePositionsService {
    TransactionResult valuePositions(
            EntityId dealId,
            CurrencyCode currencyCode,
            LocalDate fromPositionDate,
            LocalDate toPositionDate,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime);

    TransactionResult valuePositions(
            DefinedQuery definedQuery,
            CurrencyCode currencyCode,
            LocalDate fromPositionDate,
            LocalDate toPositionDate,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime);
}
