package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FxIndexService {
    public static String BEAN_NAME = "fxIndexService";

    public FxIndexSnapshot load(EntityId id);

    public FxIndexSnapshot findByName(String name);

    TransactionResult save(FxIndexSnapshot snapshot);

    TransactionResult save(List<FxIndexSnapshot> snapshots);

    List<FxIndexSnapshot> findFxIndexByFromToCurrencyCodes(
            CurrencyCode firstCode,
            CurrencyCode secondCode);

    List<FxIndexSnapshot> findActiveFxIndices();


    public QuerySelectedPage findFxIndexIds(DefinedQuery definedQuery);

    List<FxIndexSnapshot> findByIds(QuerySelectedPage querySelectedPage);


    ///////////////// Fx Curves /////////////////////

    TransactionResult saveFxCurves(
            EntityId fxIndexId,
            List<FxCurveSnapshot> snapshots);


    public List<CurveReport> fetchFxCurveReports(
            QuerySelectedPage fxIndices,
            LocalDate fromCurveDate,
            LocalDate toCurveDate,
            LocalDateTime observedDateTime);

    public QuerySelectedPage findFxCurveIds(DefinedQuery definedQuery);

    List<FxCurveSnapshot> fetchFxCurvesByIds(QuerySelectedPage querySelectedPage);



}
