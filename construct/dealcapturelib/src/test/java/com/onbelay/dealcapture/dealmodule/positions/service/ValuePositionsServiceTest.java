package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.PhysicalDealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionView;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
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

public class ValuePositionsServiceTest extends PhysicalDealServiceTestCase {

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
    public void valuePhysicalPositionsWithBuyFixedPrice() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceBuyDeal.getId()));

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceBuyDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        valuePositionsService.valuePositions(
                List.of(fixedPriceBuyDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceBuyDeal.generateEntityId());
        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());
        assertEquals(0,
                fixedPriceBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(3.4).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-10).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-10).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                fixedPriceBuyDeal.getId(),
                CurrencyCode.CAD,
                createdDateTime);
        PhysicalPositionView view = (PhysicalPositionView) views.get(0);
        assertNull(view.getPriceDetail().getDealIndexPriceValue());

        assertEquals(0, BigDecimal.ONE.compareTo(view.getPriceDetail().getDealPriceValue()));
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(view.getPriceDetail().getMarketPriceValue()));
    }

    @Test
    public void valuePhysicalPositionsWithSellFixedPrice() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceSellDeal.getId()));

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceSellDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        valuePositionsService.valuePositions(
                List.of(fixedPriceSellDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceSellDeal.generateEntityId());
        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceSellDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());
        assertEquals(0,
                fixedPriceSellDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(-3.4).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());
    }

    @Test
    public void valuePhysicalPositionsWithBuyDealIndexPrice() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(indexBuyDeal.getId()));
        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                indexBuyDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        valuePositionsService.valuePositions(
                List.of(indexBuyDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(-6.6).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-20).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-20).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                indexBuyDeal.getId(),
                CurrencyCode.CAD,
                createdDateTime);
        PhysicalPositionView view = (PhysicalPositionView) views.get(0);

        assertEquals(0, BigDecimal.valueOf(2).compareTo(view.getPriceDetail().getDealIndexPriceValue()));
        assertNull(view.getPriceDetail().getDealPriceValue());
        assertEquals(0, view.getPriceDetail().getDealIndexPriceValue().compareTo(view.getPriceDetail().getTotalDealPriceValue()));
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(view.getPriceDetail().getMarketPriceValue()));
    }

    @Test
    public void valuePhysicalPositionsWithSellDealIndexPrice() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(indexSellDeal.getId()));
        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                indexSellDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        valuePositionsService.valuePositions(
                List.of(indexSellDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexSellDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(6.6).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(20).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(20).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());
    }

    @Test
    public void valuePhysicalPositionsWithBuyDealIndexPricePlus() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(indexPlusBuyDeal.getId()));
        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                indexPlusBuyDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        valuePositionsService.valuePositions(
                List.of(indexPlusBuyDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexPlusBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexPlusBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(-16.60).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-30).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-30).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                indexPlusBuyDeal.getId(),
                CurrencyCode.CAD,
                createdDateTime);
        PhysicalPositionView view = (PhysicalPositionView) views.get(0);

        assertEquals(0, BigDecimal.valueOf(2).compareTo(view.getPriceDetail().getDealIndexPriceValue()));
        assertNotNull(view.getPriceDetail().getDealPriceValue());
        assertEquals(0, BigDecimal.valueOf(3).compareTo(view.getPriceDetail().getTotalDealPriceValue()));
        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(view.getPriceDetail().getMarketPriceValue()));

    }

    @Test
    public void valuePhysicalPositionsWithSellDealIndexPricePlus() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(indexPlusSellDeal.getId()));
        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                indexPlusSellDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());

        valuePositionsService.valuePositions(
                List.of(indexPlusSellDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexPlusSellDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexPlusSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(16.60).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(30).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(30).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());
    }

}
