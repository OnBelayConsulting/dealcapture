package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.service.PhysicalDealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionsFixture;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.shared.enums.CurrencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhysicalDealPositionServiceTest extends PhysicalDealServiceTestCase {
    private static final Logger logger = LogManager.getLogger();


    @Autowired
    private DealPositionRepository dealPositionRepository;

    @Autowired
    private DealPositionService dealPositionService;

    private PriceRiskFactor priceRiskFactor;

    private FxRiskFactor fxRiskFactor;

    private LocalDateTime createdDateTime = LocalDateTime.of(2024, 1, 1, 10, 1);

    private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

    @Override
    public void setUp() {
        super.setUp();

        fxRiskFactor = FxRiskFactorFixture.createFxRiskFactor(fxIndex, fromMarketDate);

        priceRiskFactor = PriceRiskFactorFixture.createPriceRiskFactor(
                marketIndex,
                fromMarketDate);

    }

    @Test
    public void createPositionsForFixedBuyDeal() {
        List<DealPositionSnapshot> snapshots = PhysicalPositionsFixture.createPositions(
                fixedPriceBuyDeal,
                CurrencyCode.CAD,
                createdDateTime,
                priceRiskFactor,
                fxRiskFactor);

        dealPositionService.saveDealPositions(
                "test",
                snapshots);

        flush();

        List<DealPositionSnapshot> snapshots1 = dealPositionService.findPositionsByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertTrue(snapshots1.size() > 0);

    }



    @Test
    public void fetchDealPositionViews() {
        List<DealPositionSnapshot> snapshots = PhysicalPositionsFixture.createPositions(
                fixedPriceBuyDeal,
                CurrencyCode.CAD,
                createdDateTime,
                priceRiskFactor,
                fxRiskFactor);

        dealPositionService.saveDealPositions(
                "test",
                snapshots);

        flush();

        List<DealPositionView> reports = dealPositionService.fetchDealPositionViews(
                List.of(fixedPriceBuyDeal.getId()),
                CurrencyCode.CAD,
                createdDateTime);

        assertTrue(reports.size() > 0);

    }

}
