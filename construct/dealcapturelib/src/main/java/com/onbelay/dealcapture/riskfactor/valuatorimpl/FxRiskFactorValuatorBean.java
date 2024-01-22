package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.valuator.FxRiskFactorValuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class FxRiskFactorValuatorBean implements FxRiskFactorValuator {

    @Autowired
    private FxRiskFactorRepository fxRiskFactorRepository;

    @Autowired
    private FxCurveRepository fxCurveRepository;

    @Autowired
    private FxIndexRepository fxIndexRepository;

    @Override
    public void valueRiskFactors(EntityId fxIndexId) {
            List<FxRiskFactor> factors = fxRiskFactorRepository.fetchByFxIndex(fxIndexId);

            doValueRiskFactors(
                    factors,
                    LocalDateTime.now());
    }

    @Override
    public void valueRiskFactors(
            QuerySelectedPage selectedPage,
            LocalDateTime currentDateTime) {

        List<FxRiskFactor> factors = fxRiskFactorRepository.fetchByIds(selectedPage);
        doValueRiskFactors(factors, currentDateTime);
    }

    @Override
    public void valueRiskFactors(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime) {

        List<Integer> ids = fxRiskFactorRepository.findFxRiskFactorIds(definedQuery);
        List<FxRiskFactor> factors = fxRiskFactorRepository.fetchByIds(new QuerySelectedPage(ids));
        doValueRiskFactors(factors, currentDateTime);

    }

    private void doValueRiskFactors(
            List<FxRiskFactor> factors,
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

        FxCurveDiscoveryCalculator calculator = FxCurveDiscoveryCalculator
                .newCalculator()
                .withIndices(
                        fxIndexRepository.fetchFxIndexReports(distinctIndexIds))
                .withRates(
                        fxCurveRepository.fetchFxCurveReports(
                                distinctIndexIds,
                                fromCurveDate,
                                toCurveDate,
                                currentDateTime));

        for (FxRiskFactor factor : factors) {
            factor.updateRate(
                    calculator.calculateRate(
                            factor.getIndex().getId(),
                            factor.getDetail().getMarketDate()));
        }
    }
}
