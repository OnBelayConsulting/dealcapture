package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.shared.enums.CurrencyCode;

import java.util.List;

public interface FxIndexService {
    public static String BEAN_NAME = "fxIndexService";

    public FxIndexSnapshot load(EntityId id);

    public FxIndexSnapshot findByName(String name);

    TransactionResult save(FxIndexSnapshot snapshot);

    TransactionResult save(List<FxIndexSnapshot> snapshots);

    TransactionResult saveFxCurves(
            EntityId fxIndexId,
            List<FxCurveSnapshot> snapshots);

    List<FxIndexSnapshot> findFxIndexByFromToCurrencyCodes(
            CurrencyCode firstCode,
            CurrencyCode secondCode);

    List<FxIndexSnapshot> loadAll();
}
