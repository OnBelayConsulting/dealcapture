package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.pricing.repository.PriceCurveRepository;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.riskfactor.batch.sql.PriceRiskFactorBatchUpdater;
import com.onbelay.dealcapture.riskfactor.evaluator.PriceRiskFactorEvaluator;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class PriceRiskFactorEvaluatorBean implements PriceRiskFactorEvaluator {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;

    @Autowired
    private PriceCurveRepository priceCurveRepository;

    @Autowired
    private PriceIndexRepository priceIndexRepository;

    @Autowired
    private PriceRiskFactorBatchUpdater priceRiskFactorBatchUpdater;

    @Override
    public void valueRiskFactors(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime) {
        logger.error("Value risk factors start: " + LocalDateTime.now());
        List<Integer> ids = priceRiskFactorRepository.findPriceRiskFactorIds(definedQuery);
        List<PriceRiskFactor> factors = priceRiskFactorRepository.fetchByIds(new QuerySelectedPage(ids));

        doValueRiskFactors(factors, currentDateTime);
        logger.error("Value risk factors end: " + LocalDateTime.now());
    }

    @Override
    public void valueRiskFactors(
            QuerySelectedPage selectedPage,
            LocalDateTime currentDateTime) {

        List<PriceRiskFactor> factors = priceRiskFactorRepository.fetchByIds(selectedPage);

        doValueRiskFactors(factors, currentDateTime);
    }

    @Override
    public void valueRiskFactors(EntityId priceIndexId) {
        List<PriceRiskFactor> factors = priceRiskFactorRepository.fetchByPriceIndex(priceIndexId);
        doValueRiskFactors(factors, LocalDateTime.now());
    }

    private void doValueRiskFactors(
            List<PriceRiskFactor> factors ,
            LocalDateTime currentDateTime) {

        if (factors.isEmpty())
            return;

        LocalDate fromCurveDate = factors
                .stream()
                .map(c-> c.getDetail().getMarketDate())
                .min(LocalDate::compareTo)
                .get();
        LocalDate toCurveDate = factors
                .stream()
                .map(c-> c.getDetail().getMarketDate())
                .max(LocalDate::compareTo)
                .get();

        List<Integer> distinctIndexIds = factors
                .stream()
                .map(c-> c.getIndex().getId())
                .collect(Collectors.toSet())
                .stream()
                .toList();

        PriceCurveDiscoveryCalculator calculator = PriceCurveDiscoveryCalculator
                .newCalculator()
                .withIndices(
                        priceIndexRepository.fetchPriceIndexReports(distinctIndexIds))
                .withPrices(priceCurveRepository.fetchPriceCurveReports(
                        distinctIndexIds,
                        fromCurveDate,
                        toCurveDate,
                        currentDateTime));


        ArrayList<PriceRiskFactorEvaluationResult> results = new ArrayList<>();

        for (PriceRiskFactor factor : factors) {
            Price price = calculator.calculatePrice(
                            factor.getIndex().getId(),
                            factor.getDetail().getMarketDate(),
                            factor.getDetail().getHourEnding());
            results.add(
                    new PriceRiskFactorEvaluationResult(
                            factor.getId(),
                            currentDateTime,
                            price));

        }

        SubLister<PriceRiskFactorEvaluationResult> subLister = new SubLister<>(results, 1000);
        while (subLister.moreElements()) {
            priceRiskFactorBatchUpdater.updatePositions(subLister.nextList());
        }


    }

}
