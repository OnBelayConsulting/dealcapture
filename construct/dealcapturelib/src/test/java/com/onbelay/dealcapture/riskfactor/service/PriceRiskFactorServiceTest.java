package com.onbelay.dealcapture.riskfactor.service;

import com.onbelay.core.exception.OBValidationException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorErrorCode;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorAudit;
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

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PriceRiskFactorServiceTest extends DealCaptureSpringTestCase {
    private static final Logger logger = LogManager.getLogger();
    private PricingLocation location;
    private PriceIndex aceeDailyPriceIndex;
    
    private PriceIndex priceDailyIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 10, 31);

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;
    
    @Override
    public void setUp() {
        super.setUp();

        location = PricingLocationFixture.createPricingLocation("West");
        aceeDailyPriceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);
        
        priceDailyIndex = PriceIndexFixture.createPriceIndex(
                "ADDLY",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                UnitOfMeasureCode.GJ,
                location);

        PriceRiskFactorFixture.createPriceRiskFactors(
                aceeDailyPriceIndex,
                fromMarketDate,
                toMarketDate);

        PriceIndexFixture.generateDailyPriceCurves(
                aceeDailyPriceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.23),
                LocalDateTime.now());

        flush();
    }

    @Test
    public void createRiskFactor() {
        PriceRiskFactorSnapshot snapshot = new PriceRiskFactorSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setMarketDate(LocalDate.of(2023, 6, 23));
        snapshot.getDetail().setCreateUpdateDateTime(LocalDateTime.of(2023, 7, 1, 11, 6));
        snapshot.getDetail().setValue(BigDecimal.ONE);
        priceRiskFactorService.save(
                priceDailyIndex.generateEntityId(),
                List.of(snapshot));
        flush();

        DefinedQuery query = new DefinedQuery("PriceRiskFactor");
        List<Integer> ids = priceRiskFactorRepository.findPriceRiskFactorIds(query);

        PriceRiskFactor factor = priceRiskFactorRepository.fetchByMarketDate(
                priceDailyIndex.generateEntityId(),
                LocalDate.of(2023, 6, 23));
        assertNotNull(factor);
        assertEquals(0, BigDecimal.ONE.compareTo(factor.getDetail().getValue()));
        assertEquals(LocalDateTime.of(2023, 7, 1, 11, 6), factor.getDetail().getCreateUpdateDateTime());
        PriceRiskFactorAudit audit = PriceRiskFactorAudit.findRecentHistory(factor);
        assertNotNull(audit);
        assertEquals(factor.getDetail().getMarketDate(), audit.getDetail().getMarketDate());
    }


    @Test
    public void createRiskFactorFailMissingMarketDate() {
        PriceRiskFactorSnapshot snapshot = new PriceRiskFactorSnapshot();
        snapshot.getDetail().setDefaults();
        snapshot.getDetail().setValue(BigDecimal.ONE);

        try {
            priceRiskFactorService.save(
                    priceDailyIndex.generateEntityId(),
                    List.of(snapshot));
            flush();
            fail("Should have thrown ob exception");
        } catch (OBValidationException e) {
            assertEquals(RiskFactorErrorCode.MISSING_RISK_FACTOR_DATE.getCode(), e.getErrorCode());
            return;
        }
        fail("Should have thrown ob exception");
    }


    @Test
    public void testFindByMarketDate() {
        PriceRiskFactorSnapshot snapshot = priceRiskFactorService.findByMarketDate(
                aceeDailyPriceIndex.generateEntityId(),
                LocalDate.of(2023, 2, 4));

        assertNotNull(snapshot.getPriceIndexId());
        assertEquals(LocalDate.of(2023, 2, 4),snapshot.getDetail().getMarketDate());

    }

    @Test
    public void valueRiskFactors() {
        priceRiskFactorService.valueRiskFactors(aceeDailyPriceIndex.generateEntityId());
        PriceRiskFactorSnapshot snapshot = priceRiskFactorService.findByMarketDate(
                aceeDailyPriceIndex.generateEntityId(),
                LocalDate.of(2023, 2, 4));

        assertNotNull(snapshot.getPriceIndexId());
        assertEquals(0, BigDecimal.valueOf(1.23).compareTo(snapshot.getDetail().getValue()));
    }

}
