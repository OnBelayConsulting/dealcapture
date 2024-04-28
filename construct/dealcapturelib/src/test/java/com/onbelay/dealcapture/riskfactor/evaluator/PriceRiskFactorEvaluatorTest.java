package com.onbelay.dealcapture.riskfactor.evaluator;

import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PriceRiskFactorEvaluatorTest extends DealCaptureSpringTestCase {

    private PricingLocation location;
    private PriceIndex monthlyPriceIndex;

    private PriceIndex dailyPriceIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 7, 31);

    private LocalDateTime observedDateTime = LocalDateTime.of(2023, 10, 1, 2, 3);

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;

    @Autowired
    private PriceRiskFactorEvaluator priceRiskFactorEvaluator;

    @Override
    public void setUp() {
        super.setUp();

        location = PricingLocationFixture.createPricingLocation("West");
        monthlyPriceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        dailyPriceIndex = PriceIndexFixture.createPriceIndex(
                "ADDLY",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        PriceRiskFactorFixture.createPriceRiskFactors(
                dailyPriceIndex,
                fromMarketDate,
                toMarketDate);
        flush();

        PriceIndexFixture.generateDailyPriceCurves(
                dailyPriceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.23),
                LocalDateTime.of(2023, 1, 1, 0, 1));

    }

    @Test
    public void valueRiskFactorsWithoutFactors() {
        priceRiskFactorEvaluator.valueRiskFactors(monthlyPriceIndex.generateEntityId());
        List<PriceRiskFactor> factors = priceRiskFactorRepository.fetchByPriceIndex(monthlyPriceIndex.generateEntityId());
        flush();
        factors.forEach( f -> assertNull(f.getDetail().getValue()));
    }

    @Test
    public void valueRiskFactorsWithPrices() {
        priceRiskFactorEvaluator.valueRiskFactors(dailyPriceIndex.generateEntityId());
        flush();
        clearCache();
        List<PriceRiskFactor> factors = priceRiskFactorRepository.fetchByPriceIndex(dailyPriceIndex.generateEntityId());
        factors.forEach( f -> assertNotNull(f.getDetail().getValue()));
    }

    @Test
    public void valueRiskFactorsWithoutPrices() {

        LocalDate extendedFromDate = LocalDate.of(2023, 8, 1);
        LocalDate extendedToDate = LocalDate.of(2023, 8, 31);

        List<PriceRiskFactor> factors = PriceRiskFactorFixture.createPriceRiskFactors(
                dailyPriceIndex,
                extendedFromDate,
                extendedToDate);
        flush();
        priceRiskFactorEvaluator.valueRiskFactors(
                new QuerySelectedPage(factors
                        .stream()
                        .map(c-> c.getId())
                        .collect(Collectors.toList())),
                LocalDateTime.now());
        clearCache();
        factors = priceRiskFactorRepository.fetchByPriceIndex(dailyPriceIndex.generateEntityId());
        factors.forEach( f -> assertNull(f.getDetail().getValue()));

    }

}
