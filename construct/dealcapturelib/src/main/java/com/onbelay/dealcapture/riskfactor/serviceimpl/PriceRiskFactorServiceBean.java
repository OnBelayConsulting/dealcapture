package com.onbelay.dealcapture.riskfactor.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.riskfactor.assembler.FxRiskFactorAssembler;
import com.onbelay.dealcapture.riskfactor.assembler.PriceRiskFactorAssembler;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.valuator.PriceRiskFactorValuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service("priceRiskFactorService")
@Transactional
public class PriceRiskFactorServiceBean implements PriceRiskFactorService {

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;

    @Autowired
    private PriceIndexRepository priceIndexRepository;

    @Autowired
    private PriceRiskFactorValuator priceRiskFactorValuator;

    @Override
    public PriceRiskFactorSnapshot load(EntityId id) {
        PriceRiskFactor factor =  priceRiskFactorRepository.load(id);
        PriceRiskFactorAssembler assembler = new PriceRiskFactorAssembler();
        return assembler.assemble(factor);
    }


    @Override
    public List<PriceRiskFactorSnapshot> loadAll() {
        List<PriceRiskFactor> factors = priceRiskFactorRepository.loadAll();
        PriceRiskFactorAssembler assembler = new PriceRiskFactorAssembler();
        return assembler.assemble(factors);
    }

    @Override
    public void valueRiskFactors(EntityId priceIndexId) {
        priceRiskFactorValuator.valueRiskFactors(priceIndexId);
    }

    @Override
    public TransactionResult savePriceRiskFactors(
            EntityId priceIndexId,
            List<PriceRiskFactorSnapshot> riskFactors) {

        PriceIndex index = priceIndexRepository.load(priceIndexId);
        List<EntityId> ids = index.savePriceRiskFactors(riskFactors);
        return new TransactionResult(ids);
    }

    @Override
    public PriceRiskFactorSnapshot findByMarketDate(
            EntityId priceIndexId,
            LocalDate marketDate) {

        PriceRiskFactor factor =  priceRiskFactorRepository.fetchByMarketDate(
                priceIndexId,
                marketDate);

        if (factor == null)
            return null;

        PriceRiskFactorAssembler assembler = new PriceRiskFactorAssembler();
        return assembler.assemble(factor);
    }
}
