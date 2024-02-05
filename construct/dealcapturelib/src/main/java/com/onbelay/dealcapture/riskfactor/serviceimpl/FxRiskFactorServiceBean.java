package com.onbelay.dealcapture.riskfactor.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.assembler.PriceIndexSnapshotAssembler;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
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
import java.time.LocalDateTime;
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
    public QuerySelectedPage findFxRiskFactorIds(DefinedQuery definedQuery) {
        return new QuerySelectedPage(
                fxRiskFactorRepository.findFxRiskFactorIds(definedQuery),
                definedQuery.getOrderByClause());
    }

    @Override
    public List<FxRiskFactorSnapshot> findByIds(QuerySelectedPage selectedPage) {
        List<FxRiskFactor> riskFactors = fxRiskFactorRepository.fetchByIds(selectedPage);
        FxRiskFactorAssembler assembler = new FxRiskFactorAssembler();
        return assembler.assemble(riskFactors);
    }

    @Override
    public List<FxRiskFactorSnapshot> findByFxIndexIds(
            List<Integer> fxIndexIds,
            LocalDate fromDate,
            LocalDate toDate) {

        List<FxRiskFactor> riskFactors = fxRiskFactorRepository.fetchByFxIndices(
                fxIndexIds,
                fromDate,
                toDate);

        FxRiskFactorAssembler assembler = new FxRiskFactorAssembler();
        return assembler.assemble(riskFactors);
    }

    @Override
    public FxRiskFactorSnapshot load(EntityId id) {
        FxRiskFactor factor =  fxRiskFactorRepository.load(id);
        FxRiskFactorAssembler assembler = new FxRiskFactorAssembler();
        return assembler.assemble(factor);
    }

    @Override
    public TransactionResult save(
            EntityId fxIndexId,
            List<FxRiskFactorSnapshot> riskFactors) {

        FxIndex index = fxIndexRepository.load(fxIndexId);
        if (index == null)
            throw new OBRuntimeException(PricingErrorCode.MISSING_FX_INDEX.getCode());

        List<Integer> ids = index.saveFxRiskFactors(riskFactors);
        return new TransactionResult(ids);
    }

    @Override
    public void valueRiskFactors(EntityId fxIndexId) {
        fxRiskFactorValuator.valueRiskFactors(fxIndexId);
    }

    @Override
    public void valueRiskFactors(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime) {
        fxRiskFactorValuator.valueRiskFactors(
                definedQuery,
                currentDateTime);
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
