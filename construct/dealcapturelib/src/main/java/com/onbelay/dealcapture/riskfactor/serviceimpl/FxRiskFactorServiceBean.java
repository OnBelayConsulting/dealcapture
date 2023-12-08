package com.onbelay.dealcapture.riskfactor.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.riskfactor.assembler.FxRiskFactorAssembler;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.valuator.FxRiskFactorValuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service("fxRiskFactorService")
@Transactional
public class FxRiskFactorServiceBean implements FxRiskFactorService {

    @Autowired
    private FxRiskFactorRepository fxRiskFactorRepository;

    @Autowired
    private FxIndexRepository fxIndexRepository;

    @Autowired
    private FxRiskFactorValuator fxRiskFactorValuator;

    @Override
    public FxRiskFactorSnapshot load(EntityId id) {
        FxRiskFactor factor =  fxRiskFactorRepository.load(id);
        FxRiskFactorAssembler assembler = new FxRiskFactorAssembler();
        return assembler.assemble(factor);
    }

    @Override
    public TransactionResult saveFxRiskFactors(
            EntityId fxIndexId,
            List<FxRiskFactorSnapshot> riskFactors) {

        FxIndex index = fxIndexRepository.load(fxIndexId);
        if (index == null)
            throw new OBRuntimeException(PricingErrorCode.MISSING_FX_INDEX.getCode());

        List<EntityId> ids = index.saveFxRiskFactors(riskFactors);
        return new TransactionResult(ids);
    }

    @Override
    public void valueRiskFactors(EntityId fxIndexId) {
        fxRiskFactorValuator.valueRiskFactors(fxIndexId);
    }

    @Override
    public List<FxRiskFactorSnapshot> loadAll() {
        List<FxRiskFactor> factors = fxRiskFactorRepository.loadAll();
        FxRiskFactorAssembler assembler = new FxRiskFactorAssembler();
        return assembler.assemble(factors);
    }

    @Override
    public FxRiskFactorSnapshot findByMarketDate(
            EntityId fxIndexId,
            LocalDate marketDate) {
        FxRiskFactor factor = fxRiskFactorRepository.fetchByMarketDate(fxIndexId, marketDate);
        if (factor != null) {
            FxRiskFactorAssembler assembler = new FxRiskFactorAssembler();
            return assembler.assemble(factor);
        } else {
            return null;
        }
    }
}
