package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.DealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValuePositionsServiceWithCostsAndFXTest extends DealServiceTestCase {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    @Autowired
    private ValuePositionsService valuePositionsService;

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    private PriceIndex secondPriceIndex;

    private LocalDateTime observedDateTime = LocalDateTime.of(2024, 1, 1, 2, 43);

    private LocalDateTime currentValuationDateTime = LocalDateTime.of(2024, 2, 1, 2, 43);


    private LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2024, 1, 31);

    @Override
    public void setUp() {
        super.setUp();


        PriceIndexFixture.generateDailyPriceCurves(
                marketIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.34),
                observedDateTime);

        PriceIndexFixture.generateDailyPriceCurves(
                dealPriceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(2),
                observedDateTime);


    }

    @Test
    public void valueWithBuyFixedPriceFixedCost() {

        FxIndexFixture.generateDailyFxCurves(
                fxIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(2),
                observedDateTime);



        DealCostSnapshot cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.BROKERAGE_DAILY_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(10));
        cost.getDetail().setCurrencyCode(CurrencyCode.USD);
        fixedPriceBuyDeal.saveDealCosts(List.of(cost));
        flush();

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceBuyDeal.getId()));

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceBuyDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                currentValuationDateTime);

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                currentValuationDateTime);

        valuePositionsService.valuePositions(
                fixedPriceBuyDeal.generateEntityId(),
                CurrencyCode.CAD,
                createdDateTime,
                currentValuationDateTime);
        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceBuyDeal.generateEntityId());
        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());
        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                fixedPriceBuyDeal.getDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        List<Integer> costPositionIds = dealPositionService.findCostPositionIdsByDeal(deal.generateEntityId());
        List<CostPositionSnapshot> costSnapshots = dealPositionService.findCostPositionsByIds(new QuerySelectedPage(costPositionIds));
        assertEquals(31, costSnapshots.size());
        List<CostPositionSnapshot> costsPerPosition = costSnapshots
                .stream()
                .filter( c->c.getDetail().getStartDate().isEqual(positionSnapshot.getDealPositionDetail().getStartDate()))
                .toList();

        assertEquals(1, costsPerPosition.size());
        CostPositionSnapshot facilityPerUnitFee = costsPerPosition
                .stream()
                .filter(c-> c.getDetail().getCostNameCode() == CostNameCode.BROKERAGE_DAILY_FEE)
                .findFirst()
                .get();

        assertNotNull(facilityPerUnitFee.getCostFxRiskFactorId());
        assertFalse(facilityPerUnitFee.getDetail().getIsFixedValued());



        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(23.4).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.valueOf(20).compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-10.00).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());
    }


    @Test
    public void valueWithBuyFixedPriceFixedCostNoFXCurves() {


        DealCostSnapshot cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.BROKERAGE_DAILY_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(10));
        cost.getDetail().setCurrencyCode(CurrencyCode.USD);
        fixedPriceBuyDeal.saveDealCosts(List.of(cost));
        flush();

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceBuyDeal.getId()));

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceBuyDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                currentValuationDateTime);

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                currentValuationDateTime);

        valuePositionsService.valuePositions(
                fixedPriceBuyDeal.generateEntityId(),
                CurrencyCode.CAD,
                createdDateTime,
                currentValuationDateTime);
        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceBuyDeal.generateEntityId());
        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());
        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                fixedPriceBuyDeal.getDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        List<Integer> costPositionIds = dealPositionService.findCostPositionIdsByDeal(deal.generateEntityId());
        List<CostPositionSnapshot> costSnapshots = dealPositionService.findCostPositionsByIds(new QuerySelectedPage(costPositionIds));
        assertEquals(31, costSnapshots.size());
        List<CostPositionSnapshot> costsPerPosition = costSnapshots
                .stream()
                .filter( c->c.getDetail().getStartDate().isEqual(positionSnapshot.getDealPositionDetail().getStartDate()))
                .toList();

        assertEquals(1, costsPerPosition.size());
        CostPositionSnapshot facilityPerUnitFee = costsPerPosition
                .stream()
                .filter(c-> c.getDetail().getCostNameCode() == CostNameCode.BROKERAGE_DAILY_FEE)
                .findFirst()
                .get();

        assertNotNull(facilityPerUnitFee.getCostFxRiskFactorId());
        assertFalse(facilityPerUnitFee.getDetail().getIsFixedValued());



        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertNull(positionSnapshot.getSettlementDetail().getMarkToMarketValuation());
        assertNull(positionSnapshot.getSettlementDetail().getCostSettlementAmount());
        assertEquals(0, BigDecimal.valueOf(-10.00).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertNull(positionSnapshot.getSettlementDetail().getTotalSettlementAmount());
        assertEquals(PositionErrorCode.ERROR_INVALID_POSITION_VALUATION.getCode(), positionSnapshot.getDealPositionDetail().getErrorCode());
        assertNotNull(positionSnapshot.getDealPositionDetail().getErrorMessage());
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());
    }


}
