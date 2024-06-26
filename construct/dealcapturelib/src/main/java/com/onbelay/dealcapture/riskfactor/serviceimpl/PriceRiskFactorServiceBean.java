package com.onbelay.dealcapture.riskfactor.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.riskfactor.assembler.PriceRiskFactorAssembler;
import com.onbelay.dealcapture.riskfactor.evaluator.PriceRiskFactorEvaluator;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service("priceRiskFactorService")
@Transactional
public class PriceRiskFactorServiceBean implements PriceRiskFactorService {

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;

    @Autowired
    private PriceIndexRepository priceIndexRepository;

    @Autowired
    private PriceRiskFactorEvaluator priceRiskFactorEvaluator;

    @Override
    public PriceRiskFactorSnapshot load(EntityId id) {
        PriceRiskFactor factor =  priceRiskFactorRepository.load(id);
        PriceRiskFactorAssembler assembler = new PriceRiskFactorAssembler();
        return assembler.assemble(factor);
    }

    @Override
    public QuerySelectedPage findPriceRiskFactorIds(DefinedQuery definedQuery) {

        return new QuerySelectedPage(
                priceRiskFactorRepository.findPriceRiskFactorIds(definedQuery),
                definedQuery.getOrderByClause());
    }

    @Override
    public List<PriceRiskFactorSnapshot> findByIds(QuerySelectedPage selectedPage) {
        List<PriceRiskFactor> riskFactors = priceRiskFactorRepository.fetchByIds(selectedPage);
        PriceRiskFactorAssembler assembler = new PriceRiskFactorAssembler();
        return assembler.assemble(riskFactors);
    }

    @Override
    public List<PriceRiskFactorSnapshot> findByPriceIndexIds(
            List<Integer> priceIndexIds,
            LocalDate fromDate,
            LocalDate toDate) {
        List<PriceRiskFactor> riskFactors = priceRiskFactorRepository.fetchByPriceIndices(
                priceIndexIds,
                fromDate,
                toDate);

        PriceRiskFactorAssembler assembler = new PriceRiskFactorAssembler();
        return assembler.assemble(riskFactors);
    }

    @Override
    public void valueRiskFactors(EntityId priceIndexId) {
        priceRiskFactorEvaluator.valueRiskFactors(priceIndexId);
    }

    @Override
    public void valueRiskFactors(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime) {
        priceRiskFactorEvaluator.valueRiskFactors(
                definedQuery,
                currentDateTime);
    }

    @Override
    public TransactionResult save(
            EntityId priceIndexId,
            List<PriceRiskFactorSnapshot> riskFactors) {

        PriceIndex index = priceIndexRepository.load(priceIndexId);
        List<Integer> ids = index.savePriceRiskFactors(riskFactors);
        return new TransactionResult(ids);
    }

    @Override
    public List<PriceRiskFactorSnapshot> findByMarketDate(
            EntityId priceIndexId,
            LocalDate marketDate) {

        List<PriceRiskFactor> factors =  priceRiskFactorRepository.fetchByMarketDate(
                priceIndexId,
                marketDate);


        PriceRiskFactorAssembler assembler = new PriceRiskFactorAssembler();
        return assembler.assemble(factors);
    }
}
