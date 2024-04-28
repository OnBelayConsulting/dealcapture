package com.onbelay.dealcapture.riskfactor.evaluator;

import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FxRiskFactorEvaluatorTest extends DealCaptureSpringTestCase {

    @Autowired
    private FxRiskFactorEvaluator fxRiskFactorEvaluator;

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    @Autowired
    private FxRiskFactorRepository fxRiskFactorRepository;

    private FxIndex fxIndex;

    private FxIndex fxDailyIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 10, 31);

    private LocalDateTime observedDateTime = LocalDateTime.of(2023, 10, 1, 2, 3);

    @Override
    public void setUp() {
        super.setUp();

        fxIndex = FxIndexFixture.createDailyFxIndex(
                CurrencyCode.CAD,
                CurrencyCode.USD);

        fxDailyIndex = FxIndexFixture.createDailyFxIndex(
                CurrencyCode.USD,
                CurrencyCode.CAD);


        FxRiskFactorFixture.createFxRiskFactors(
                fxIndex,
                fromMarketDate,
                toMarketDate);
        flush();
    }

    @Test
    public void valueRiskFactorsNoPrice() {

        fxRiskFactorEvaluator.valueRiskFactors(fxIndex.generateEntityId());
        flush();

        List<FxRiskFactor> factors = fxRiskFactorRepository.fetchByFxIndex(fxIndex.generateEntityId());
        factors.forEach( f-> assertNull(f.getDetail().getValue()));

    }



    @Test
    public void valueRiskFactorsWithRates() {

        FxIndexFixture.generateDailyFxCurves(
                fxDailyIndex,
                fromMarketDate,
                toMarketDate,
                observedDateTime);

        fxRiskFactorEvaluator.valueRiskFactors(fxDailyIndex.generateEntityId());
        flush();

        List<FxRiskFactor> factors = fxRiskFactorRepository.fetchByFxIndex(fxDailyIndex.generateEntityId());
        factors.forEach( f-> assertNotNull(f.getDetail().getValue()));

    }

}
