package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateHourlyPositionsServiceTest extends PositionsServiceWithHourlyTestCase {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    private LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2024, 1, 2);

    @Test
    public void generatePhysicalPositionsWithFixedDealPrice() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceSellDeal.getId()));

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceSellDeal.getId());

        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceSellDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceSellDeal.generateEntityId());

        assertEquals(2, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());

        assertEquals(FrequencyCode.HOURLY, positionSnapshot.getDealPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDealPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDealPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDealPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getDealPositionDetail().getErrorCode());

        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                fixedPriceSellDeal.getDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getFixedPriceCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getFixedPriceUnitOfMeasure());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());

        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixedPriceSellDeal.generateEntityId());
        assertEquals(2, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot hourlyPositionSnapshot = (DealHourlyPositionSnapshot) hourlyPositionSnapshots.get(0);

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(marketHourlyIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), hourlyPositionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(1));
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());
        assertEquals(PowerFlowCode.HOURLY, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        assertEquals(PriceTypeCode.MARKET_PRICE, hourlyPositionSnapshot.getDetail().getPriceTypeCode());
    }

    @Test
    public void generatePhysicalPositionsWithFixedDealPriceWithHourlyQuantities() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceSellDeal.getId()));

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceSellDeal.getId());

        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceSellDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceSellDeal.generateEntityId());

        assertEquals(2, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());

        assertEquals(FrequencyCode.HOURLY, positionSnapshot.getDealPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDealPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDealPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDealPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getDealPositionDetail().getErrorCode());

        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                fixedPriceSellDeal.getDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getFixedPriceCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getFixedPriceUnitOfMeasure());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());

        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixedPriceSellDeal.generateEntityId());
        assertEquals(2, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot hourlyPositionSnapshot = (DealHourlyPositionSnapshot) hourlyPositionSnapshots.get(0);

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(marketHourlyIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), hourlyPositionSnapshot.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(1));
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());
        assertEquals(PowerFlowCode.HOURLY, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        assertEquals(PriceTypeCode.MARKET_PRICE, hourlyPositionSnapshot.getDetail().getPriceTypeCode());
    }


    @Test
    public void generatePhysicalPositionsWithIndexDealPrice() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(indexSellDeal.getId()));
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                "test",
                context,
                indexSellDeal.getId());

        flush();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexSellDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexSellDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());


        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(indexSellDeal.generateEntityId());
        assertEquals(4, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot dealPriceHourlyPositionSnapshot =  hourlyPositionSnapshots
                .stream()
                .filter( c-> c.getDetail().getPriceTypeCode() == PriceTypeCode.DEAL_PRICE)
                .filter( c-> c.getDetail().getStartDate().isEqual(startDate))
                .findFirst().get();

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
