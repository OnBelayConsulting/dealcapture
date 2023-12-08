package com.onbelay.dealcapture.riskfactor.valuator;

import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PriceRiskFactorValuatorTest extends DealCaptureSpringTestCase {

    private PricingLocation location;
    private PriceIndex priceIndex;

    private PriceIndex priceDailyIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 10, 31);

    private LocalDateTime observedDateTime = LocalDateTime.of(2023, 10, 1, 2, 3);

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;

    @Autowired
    private PriceRiskFactorValuator priceRiskFactorValuator;

    @Override
    public void setUp() {
        super.setUp();

        location = PricingLocationFixture.createPricingLocation("West");
        priceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                location);

        priceDailyIndex = PriceIndexFixture.createPriceIndex(
                "ADDLY",
                FrequencyCode.DAILY,
                location);
        PriceRiskFactorFixture.createPriceRiskFactors(
                priceIndex,
                fromMarketDate,
                toMarketDate);
        flush();
    }

    @Test
    public void valueRiskFactorsWithoutPrices() {
        priceRiskFactorValuator.valueRiskFactors(priceDailyIndex.generateEntityId());
        List<PriceRiskFactor> factors = priceRiskFactorRepository.fetchByPriceIndex(priceDailyIndex.generateEntityId());
        flush();
        factors.forEach( f -> assertNull(f.getDetail().getValue()));
    }

    @Test
    public void valueRiskFactorsWithPrices() {
        PriceIndexFixture.generateDailyPriceCurves(
                priceDailyIndex,
                fromMarketDate,
                toMarketDate,
                observedDateTime);
        flush();
        priceRiskFactorValuator.valueRiskFactors(priceDailyIndex.generateEntityId());
        flush();
        List<PriceRiskFactor> factors = priceRiskFactorRepository.fetchByPriceIndex(priceDailyIndex.generateEntityId());
        factors.forEach( f -> assertNotNull(f.getDetail().getValue()));
    }

}
