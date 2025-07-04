package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValueHourlyPositionsServiceTest extends PositionsServiceWithHourlyTestCase {

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
    private LocalDateTime observedDateTime = LocalDateTime.of(2023, 12, 31, 2, 43);


    private LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2024, 1, 2);

    @Override
    public void setUp() {
        super.setUp();

        PriceIndexFixture.generateHourlyPriceCurves(
                marketHourlyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.34),
                observedDateTime);


        PriceIndexFixture.generateHourlyPriceCurves(
                dealPriceHourlyIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(1.67),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                dealPriceIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(2),
                observedDateTime);


    }


    @Test
    public void generateAndValuePhysicalPositionsWithFixedDealPrice() {

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

        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceSellDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceSellDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());
        assertEquals(0,
                fixedPriceSellDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());
        assertEquals(0, BigDecimal.valueOf(-3.4).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));

        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixedPriceSellDeal.generateEntityId());
        assertEquals(1, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot hourlyPositionSnapshot = (DealHourlyPositionSnapshot) hourlyPositionSnapshots.get(0);

        List<PriceRiskFactorSnapshot> marketRiskFactors = priceRiskFactorService.findByMarketDate(
                marketHourlyIndex.generateEntityId(),
                startDate);
        assertEquals(24, marketRiskFactors.size());
        PriceRiskFactorSnapshot marketRiskFactor = (PriceRiskFactorSnapshot) marketRiskFactors.get(0);
        assertEquals(1, marketRiskFactor.getDetail().getHourEnding());

        assertEquals(marketRiskFactor.getEntityId().getId(), hourlyPositionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(1));
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());
        assertEquals(PowerFlowCode.HOURLY, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        assertEquals(PriceTypeCode.MARKET_PRICE, hourlyPositionSnapshot.getDetail().getPriceTypeCode());
    }

    @Test
    public void generatePhysicalPositionsWithFixedDealPriceWithHourlyQuantities() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceSellDeal.getId()));

        DealHourByDayFixture.createHourByDayQuantity(
                fixedPriceSellDeal,
                startDate,
                1,
                5,
                BigDecimal.valueOf(34.0));


        flush();

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

        assertEquals(1, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.valueOf(177.917).compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());

        assertEquals(0,
                fixedPriceSellDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());


        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixedPriceSellDeal.generateEntityId());
        assertEquals(2, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot hourlyPositionSnapshot = hourlyPositionSnapshots.stream().filter(c -> c.getDetail().getPriceTypeCode() == PriceTypeCode.FIXED_QUANTITY).findFirst().get();
        assertEquals(PowerFlowCode.HOURLY, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());

        hourlyPositionSnapshot = hourlyPositionSnapshots.stream().filter(c -> c.getDetail().getPriceTypeCode() == PriceTypeCode.MARKET_PRICE).findFirst().get();
        assertEquals(PowerFlowCode.HOURLY, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        for (int i=1; i< 25; i++) {
            assertNotNull(hourlyPositionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i));
            assertNotNull(hourlyPositionSnapshot.getHourFixedValueDetail().getHourFixedValue(i));
        }
    }

    @Test
    public void generatePhysicalPositionsWithFixedDealPriceWithHourlyPrices() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceHourlySellDeal.getId()));

        DealHourByDayFixture.createHourByDayPrices(
                fixedPriceHourlySellDeal,
                startDate,
                1,
                5,
                BigDecimal.valueOf(2.0));


        flush();

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceHourlySellDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        List<PriceRiskFactorSnapshot> riskFactorSnapshots = priceRiskFactorService.findByPriceIndexIds(
                List.of(marketHourlyIndex.getId()),
                fromMarketDate,
                toMarketDate);


        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());
        flush();

        valuePositionsService.valuePositions(
                List.of(fixedPriceHourlySellDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();

        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceHourlySellDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceHourlySellDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceHourlySellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixedPriceHourlySellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.valueOf(48).compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());

        assertEquals(0,BigDecimal.valueOf(3.583).compareTo(positionSnapshot.getPositionDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixedPriceHourlySellDeal.generateEntityId());
        assertEquals(2, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot hourlyPositionSnapshot = hourlyPositionSnapshots.stream().filter(c -> c.getDetail().getPriceTypeCode() == PriceTypeCode.FIXED_PRICE).findFirst().get();
        assertEquals(PowerFlowCode.HOURLY, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        for (int i=1; i < 25; i++) {
            assertNotNull(hourlyPositionSnapshot.getHourFixedValueDetail().getHourFixedValue(i));
        }

        hourlyPositionSnapshot = hourlyPositionSnapshots.stream().filter(c -> c.getDetail().getPriceTypeCode() == PriceTypeCode.MARKET_PRICE).findFirst().get();
        assertEquals(PowerFlowCode.HOURLY, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        for (int i=1; i < 25; i++) {
            assertNotNull(hourlyPositionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i));
            assertNotNull(hourlyPositionSnapshot.getHourFixedValueDetail().getHourFixedValue(i));
        }

    }


    @Test
    public void generatePhysicalPositionsWithIndexDealPrice() {
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

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexSellDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());


        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(indexSellDeal.generateEntityId());
        assertEquals(2, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot dealPriceHourlyPositionSnapshot =  hourlyPositionSnapshots
                .stream()
                .filter( c-> c.getDetail().getPriceTypeCode() == PriceTypeCode.DEAL_PRICE)
                .filter( c-> c.getDetail().getStartDate().isEqual(startDate))
                .findFirst().get();

        for (int i=1; i < 25; i++) {
            assertNotNull(dealPriceHourlyPositionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i));
            assertNotNull(dealPriceHourlyPositionSnapshot.getHourFixedValueDetail().getHourFixedValue(i));
        }

        DealHourlyPositionSnapshot marketPriceHourlyPositionSnapshot =  hourlyPositionSnapshots
                .stream()
                .filter( c-> c.getDetail().getPriceTypeCode() == PriceTypeCode.MARKET_PRICE)
                .filter( c-> c.getDetail().getStartDate().isEqual(startDate))
                .findFirst().get();


        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(marketHourlyIndex.getId()),
                startDate,
                endDate).get(0);



        assertEquals(marketRiskFactor.getEntityId().getId(), marketPriceHourlyPositionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(1));
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());
        assertEquals(PowerFlowCode.HOURLY, marketPriceHourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, marketPriceHourlyPositionSnapshot.getDetail().getCurrencyCode());


        PriceRiskFactorSnapshot dealPriceRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(dealPriceHourlyIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(dealPriceRiskFactor.getEntityId().getId(), dealPriceHourlyPositionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(1));
        assertNull(positionSnapshot.getDealPriceRiskFactorId());
        assertEquals(PowerFlowCode.HOURLY, dealPriceHourlyPositionSnapshot.getDetail().getPowerFlowCode());

    }


}
