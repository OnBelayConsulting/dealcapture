package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RiskFactorManagerTest extends DealCaptureSpringTestCase {

    private PricingLocation location;
    private PriceIndex dailyCADPriceIndex;

    private PriceIndex mthlyCADPriceIndex;

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 10, 31);

    private FxIndex fxMonthlyIndex;

    private FxIndex fxDailyIndex;

    @Autowired
    private PriceIndexService priceIndexService;

    @Autowired
    private FxIndexService fxIndexService;

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    @Autowired
    private FxRiskFactorRepository fxRiskFactorRepository;


    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;

    @Override
    public void setUp() {
        super.setUp();

        location = PricingLocationFixture.createPricingLocation("West");
        dailyCADPriceIndex = PriceIndexFixture.createPriceIndex(
                "ACEE",
                FrequencyCode.DAILY,
                CurrencyCode.CAD,
                location);

        mthlyCADPriceIndex = PriceIndexFixture.createPriceIndex(
                "ADDLY",
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                location);

        PriceRiskFactorFixture.createPriceRiskFactors(
                dailyCADPriceIndex,
                fromMarketDate,
                toMarketDate);

        PriceRiskFactorFixture.createPriceRiskFactors(
                mthlyCADPriceIndex,
                fromMarketDate,
                toMarketDate);


        fxMonthlyIndex = FxIndexFixture.createFxIndex(
                FrequencyCode.MONTHLY,
                CurrencyCode.CAD,
                CurrencyCode.USD);

        fxDailyIndex = FxIndexFixture.createDailyFxIndex(
                CurrencyCode.CAD,
                CurrencyCode.USD);


        FxRiskFactorFixture.createFxRiskFactors(
                fxDailyIndex,
                fromMarketDate,
                toMarketDate);

        flush();
    }

    @Test
    public void determineRiskFactorsFound() {
        ConcurrentRiskFactorManager riskFactorManager = createConcurrentRiskFactorManager();

        PriceRiskFactorHolder holder = riskFactorManager.determinePriceRiskFactor(
                dailyCADPriceIndex.getId(),
                LocalDate.of(2023, 2, 4));

        assertNotNull(holder);
        assertNotNull(holder.getRiskFactor());
    }


    @Test
    public void determineRiskFactorsNotFound() {
        ConcurrentRiskFactorManager riskFactorManager = createConcurrentRiskFactorManager();

        PriceRiskFactorHolder holder = riskFactorManager.determinePriceRiskFactor(
                dailyCADPriceIndex.getId(),
                LocalDate.of(2024, 2, 4));

        assertNotNull(holder);
        assertNull(holder.getRiskFactor());
        assertNotNull(holder.getPriceIndex());
        assertEquals(1, riskFactorManager.getPriceRiskFactorHolderQueue().size());
    }

    @Test
    public void determineFxRiskFactorsFound() {
        ConcurrentRiskFactorManager riskFactorManager = createConcurrentRiskFactorManager();

        FxRiskFactorHolder holder = riskFactorManager.determineFxRiskFactor(
                CurrencyCode.CAD,
                CurrencyCode.USD,
                LocalDate.of(2023, 2, 4));

        assertNotNull(holder);
        assertNotNull(holder.getRiskFactor());
    }


    @Test
    public void determineFxRiskFactorsNotFound() {
        ConcurrentRiskFactorManager riskFactorManager = createConcurrentRiskFactorManager();

        FxRiskFactorHolder holder = riskFactorManager.determineFxRiskFactor(
                CurrencyCode.CAD,
                CurrencyCode.USD,
                LocalDate.of(2024, 2, 4));

        assertNotNull(holder);
        assertNotNull(holder.getFxIndex());
        assertEquals(1, riskFactorManager.getFxRiskFactorHolderQueue().size());
    }


    @Test
    public void findFxRiskFactorsReversed() {
        ConcurrentRiskFactorManager riskFactorManager = createConcurrentRiskFactorManager();

        FxRiskFactorHolder holder = riskFactorManager.determineFxRiskFactor(
                CurrencyCode.USD,
                CurrencyCode.CAD,
                LocalDate.of(2023, 2, 4));

        assertNotNull(holder);

    }


    private ConcurrentRiskFactorManager createConcurrentRiskFactorManager() {
        QuerySelectedPage selectedPage = priceIndexService.findPriceIndexIds(new DefinedQuery("PriceIndex"));
        QuerySelectedPage fxSelectedPage =fxIndexService.findFxIndexIds(new DefinedQuery("FxIndex"));

        ConcurrentRiskFactorManager riskFactorManager = new ConcurrentRiskFactorManager(
                priceIndexService.findByIds(selectedPage),
                fxIndexService.findActiveFxIndices(),
                priceRiskFactorService.findByPriceIndexIds(
                        selectedPage.getIds(),
                        fromMarketDate,
                        toMarketDate),
                fxRiskFactorService.findByFxIndexIds(
                        fxSelectedPage.getIds(),
                        fromMarketDate,
                        toMarketDate));
        return riskFactorManager;
    }

}
