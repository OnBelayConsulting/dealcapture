package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.FinancialSwapDealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.FinancialSwapPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionView;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionSnapshot;
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

public class ValueFinancialSwapPositionsServiceTest extends FinancialSwapDealServiceTestCase {

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
                paysIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.34),
                observedDateTime);

        PriceIndexFixture.generateDailyPriceCurves(
                receivesIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(2.78),
                observedDateTime);


    }

    @Test
    /*
    Sell Swap: company receives fixed (1.00) and pays index (2.78) - loss of 1.78
     */
    public void valueFixed4FloatSwapSellPosition() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixed4FloatSellDeal.getId()));

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixed4FloatSellDeal.getId());

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
                fixed4FloatSellDeal.generateEntityId(),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        FinancialSwapDeal deal = (FinancialSwapDeal) dealRepository.load(fixed4FloatSellDeal.generateEntityId());
        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixed4FloatSellDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixed4FloatSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixed4FloatSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());
        assertEquals(0,
                fixed4FloatSellDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));
        assertNotNull(positionSnapshot.getReceivesPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(-17.80).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-17.8).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(-17.8).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                fixed4FloatSellDeal.getId(),
                CurrencyCode.CAD,
                createdDateTime);
        FinancialSwapPositionView view = (FinancialSwapPositionView) views.get(0);
        assertNull(view.getPriceDetail().getPaysIndexPriceValue());

        assertEquals(0, BigDecimal.ONE.compareTo(view.getPriceDetail().getPaysPriceValue()));
        assertEquals(0, BigDecimal.valueOf(2.78).compareTo(view.getPriceDetail().getReceivesPriceValue()));
    }

    @Test
    /*
        company pays 1.00 fixed and receives float price (2.78) from counterparty
        net gain is 2.78 - 1 = 1.78 net settlement.
     */
    public void valueFixed4FloatSwapBuyPosition() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixed4FloatBuyDeal.getId()));

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixed4FloatBuyDeal.getId());

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
                fixed4FloatBuyDeal.generateEntityId(),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        FinancialSwapDeal deal = (FinancialSwapDeal) dealRepository.load(fixed4FloatBuyDeal.generateEntityId());
        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixed4FloatBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixed4FloatBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixed4FloatBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());
        assertEquals(0,
                fixed4FloatBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));
        assertNotNull(positionSnapshot.getReceivesPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(17.80).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(17.80).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(17.80).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());
    }

    @Test
    /*
        company pays float index price (1.34)  and receives float price (2.78) from counterparty
        net gain is 2.78 - 1.34 = 1.44 net settlement.
     */
    public void valueFloat4FloatSwapBuyPosition() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(float4FloatBuyDeal.getId()));
        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                float4FloatBuyDeal.getId());

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
                float4FloatBuyDeal.generateEntityId(),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                float4FloatBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);
        assertEquals(float4FloatBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertNotNull(positionSnapshot.getPaysPriceRiskFactorId());
        assertNotNull(positionSnapshot.getReceivesPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(14.40).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(14.40).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(14.40).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                float4FloatBuyDeal.getId(),
                CurrencyCode.CAD,
                createdDateTime);
        FinancialSwapPositionView  view = (FinancialSwapPositionView) views.get(0);

        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(view.getPriceDetail().getPaysIndexPriceValue()));
        assertNull(view.getPriceDetail().getPaysPriceValue());
        assertEquals(0, view.getPriceDetail().getPaysIndexPriceValue().compareTo(view.getPriceDetail().getTotalPaysPriceValue()));
        assertEquals(0, BigDecimal.valueOf(2.78).compareTo(view.getPriceDetail().getReceivesPriceValue()));
    }

    @Test
    /*
        company pays float index price (1.34 + 1.00)  and receives float price (2.78) from counterparty
        net gain is 2.78 - 2.34 = 0.44 net settlement.
     */
    public void valueFloat4FloatPlusSwapBuyPosition() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(float4FloatPlusBuyDeal.getId()));
        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                float4FloatPlusBuyDeal.getId());

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
                float4FloatPlusBuyDeal.generateEntityId(),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                float4FloatPlusBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);
        assertEquals(float4FloatPlusBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertNotNull(positionSnapshot.getPaysPriceRiskFactorId());
        assertNotNull(positionSnapshot.getReceivesPriceRiskFactorId());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(4.40).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(4.40).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(4.40).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(
                float4FloatPlusBuyDeal.getId(),
                CurrencyCode.CAD,
                createdDateTime);
        FinancialSwapPositionView  view = (FinancialSwapPositionView) views.get(0);

        assertEquals(0, BigDecimal.valueOf(1.34).compareTo(view.getPriceDetail().getPaysIndexPriceValue()));
        assertNotNull(view.getPriceDetail().getPaysPriceValue());
        assertEquals(0, BigDecimal.valueOf(2.34).compareTo(view.getPriceDetail().getTotalPaysPriceValue()));
        assertEquals(0, BigDecimal.valueOf(2.78).compareTo(view.getPriceDetail().getReceivesPriceValue()));
    }
}
