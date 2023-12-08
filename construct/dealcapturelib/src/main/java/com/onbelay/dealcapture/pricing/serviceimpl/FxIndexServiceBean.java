package com.onbelay.dealcapture.pricing.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.assembler.FxIndexSnapshotAssembler;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.valuator.FxRiskFactorValuator;
import com.onbelay.shared.enums.CurrencyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("fxIndexService")
@Transactional
public class FxIndexServiceBean extends BaseDomainService implements FxIndexService {

    @Autowired
    private FxIndexRepository fxIndexRepository;

    private FxRiskFactorValuator fxRiskFactorValuator;

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
    public TransactionResult saveFxCurves(
            EntityId fxIndexId,
            List<FxCurveSnapshot> snapshots) {
        FxIndex fxIndex = fxIndexRepository.load(fxIndexId);
        return new TransactionResult(
                fxIndex.saveFxCurves(snapshots));
    }

    @Override
    public TransactionResult save(List<FxIndexSnapshot> snapshots) {

        TransactionResult result = new TransactionResult();
        for (FxIndexSnapshot snapshot : snapshots) {
            TransactionResult childResult = save(snapshot);
            if (childResult.getEntityId() != null)
                result.addEntityId(childResult.getEntityId());
        }
        return result;
    }

    @Override
    public TransactionResult save(FxIndexSnapshot snapshot) {
        if (snapshot.getEntityState() == EntityState.NEW) {
            FxIndex index = FxIndex.create(snapshot);
            return new TransactionResult(index.generateEntityId());
        } else if (snapshot.getEntityState() == EntityState.MODIFIED) {
            FxIndex index = fxIndexRepository.load(snapshot.getEntityId());
            index.updateWith(snapshot);
            return new TransactionResult(index.generateEntityId());
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

    @Override
    public List<FxIndexSnapshot> loadAll() {
        List<FxIndex> indices = fxIndexRepository.loadAll();
        FxIndexSnapshotAssembler assembler = new FxIndexSnapshotAssembler();
        return assembler.assemble(indices);
    }
}
