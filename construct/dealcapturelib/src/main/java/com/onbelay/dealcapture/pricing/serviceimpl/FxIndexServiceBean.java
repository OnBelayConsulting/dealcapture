package com.onbelay.dealcapture.pricing.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.assembler.FxCurveSnapshotAssembler;
import com.onbelay.dealcapture.pricing.assembler.FxIndexSnapshotAssembler;
import com.onbelay.dealcapture.pricing.model.FxCurve;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service("fxIndexService")
@Transactional
public class FxIndexServiceBean extends BaseDomainService implements FxIndexService {

    @Autowired
    private FxIndexRepository fxIndexRepository;

    @Autowired
    private FxCurveRepository fxCurveRepository;

    @Override
    public FxIndexSnapshot load(EntityId id) {
        FxIndex fxIndex = fxIndexRepository.load(id);
        FxIndexSnapshotAssembler assembler = new FxIndexSnapshotAssembler();
        return assembler.assemble(fxIndex);
    }

    @Override
    public FxIndexSnapshot findByName(String name) {
        FxIndex fxIndex = fxIndexRepository.findFxIndexByName(name);
        FxIndexSnapshotAssembler assembler = new FxIndexSnapshotAssembler();
        return assembler.assemble(fxIndex);
    }

    @Override
    public TransactionResult save(List<FxIndexSnapshot> snapshots) {

        TransactionResult result = new TransactionResult();
        for (FxIndexSnapshot snapshot : snapshots) {
            TransactionResult childResult = save(snapshot);
            if (childResult.getId() != null)
                result.getIds().add(childResult.getId());
        }
        return result;
    }

    @Override
    public QuerySelectedPage findFxIndexIds(DefinedQuery definedQuery) {
        return new QuerySelectedPage(
                fxIndexRepository.findFxIndexIds(definedQuery),
                definedQuery.getOrderByClause());
    }

    @Override
    public List<FxIndexSnapshot> findByIds(QuerySelectedPage querySelectedPage) {
        List<FxIndex> indices = fxIndexRepository.fetchByIds(querySelectedPage);
        FxIndexSnapshotAssembler assembler = new FxIndexSnapshotAssembler();
        return assembler.assemble(indices);
    }

    @Override
    public List<FxIndexSnapshot> findActiveFxIndices() {
        List<FxIndex> indices = fxIndexRepository.findActiveFxIndices();
        FxIndexSnapshotAssembler assembler = new FxIndexSnapshotAssembler();
        return assembler.assemble(indices);
    }

    @Override
    public TransactionResult save(FxIndexSnapshot snapshot) {
        if (snapshot.getEntityState() == EntityState.NEW) {
            FxIndex index = FxIndex.create(snapshot);
            return new TransactionResult(index.getId());
        } else if (snapshot.getEntityState() == EntityState.MODIFIED) {
            FxIndex index = fxIndexRepository.load(snapshot.getEntityId());
            index.updateWith(snapshot);
            return new TransactionResult(index.getId());
        } else if (snapshot.getEntityState() == EntityState.DELETE) {
            FxIndex index = fxIndexRepository.load(snapshot.getEntityId());
            index.delete();
        }
        return new TransactionResult();
    }

    @Override
    public List<FxIndexSnapshot> findFxIndexByFromToCurrencyCodes(
            CurrencyCode from,
            CurrencyCode to) {

        List<FxIndex> indices = fxIndexRepository.findFxIndexByFromToCurrencyCodes(from, to);
        FxIndexSnapshotAssembler assembler = new FxIndexSnapshotAssembler();
        return assembler.assemble(indices);
    }

    /////////////////  Fx Curves  /////////////////
    @Override
    public TransactionResult saveFxCurves(
            EntityId fxIndexId,
            List<FxCurveSnapshot> snapshots) {
        FxIndex fxIndex = fxIndexRepository.load(fxIndexId);
        return new TransactionResult(
                fxIndex.saveFxCurves(snapshots));
    }

    @Override
    public List<CurveReport> fetchFxCurveReports(
            QuerySelectedPage selectedPage,
            LocalDate fromCurveDate,
            LocalDate toCurveDate,
            LocalDateTime observedDateTime) {

        return fxCurveRepository.fetchFxCurveReports(
                selectedPage.getIds(),
                fromCurveDate,
                toCurveDate,
                observedDateTime);
    }

    @Override
    public QuerySelectedPage findFxCurveIds(DefinedQuery definedQuery) {
        return new QuerySelectedPage(
                    fxCurveRepository.findFxCurveIds(definedQuery),
                    definedQuery.getOrderByClause());
    }

    @Override
    public List<FxCurveSnapshot> fetchFxCurvesByIds(QuerySelectedPage querySelectedPage) {
        List<FxCurve> curves = fxCurveRepository.fetchByIds(querySelectedPage);
        FxCurveSnapshotAssembler assembler = new FxCurveSnapshotAssembler();
        return assembler.assemble(curves);
    }
}
