package com.onbelay.dealcapture.riskfactor.service;

import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceRiskFactorServiceHourlyTest extends DealCaptureSpringTestCase {
    private static final Logger logger = LogManager.getLogger();
    private PricingLocation location;
    private PriceIndex hourlyPriceIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 1, 2);

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;
    
    @Override
    public void setUp() {
        super.setUp();

        location = PricingLocationFixture.createPricingLocation("West");
        hourlyPriceIndex = PriceIndexFixture.createPriceIndex(
                "MY_HOURLY",
                FrequencyCode.HOURLY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        PriceRiskFactorFixture.createPriceRiskFactors(
                hourlyPriceIndex,
                fromMarketDate,
                toMarketDate);

        PriceIndexFixture.generateHourlyPriceCurves(
                hourlyPriceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.23),
                LocalDateTime.now());

        flush();
    }


    @Test
    public void valueRiskFactors() {
        priceRiskFactorService.valueRiskFactors(hourlyPriceIndex.generateEntityId());
        List<PriceRiskFactorSnapshot> snapshots = priceRiskFactorService.findByMarketDate(
                hourlyPriceIndex.generateEntityId(),
                LocalDate.of(2023, 1, 1));

        assertEquals(24, snapshots.size());
        PriceRiskFactorSnapshot snapshot = snapshots.get(0);
        assertEquals(0, BigDecimal.valueOf(1.23).compareTo(snapshot.getDetail().getValue()));
    }

}
