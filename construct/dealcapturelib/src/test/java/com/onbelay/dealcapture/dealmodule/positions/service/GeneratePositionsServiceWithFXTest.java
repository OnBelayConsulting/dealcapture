package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsServiceWithFXTest extends PositionsServiceWithFxTestCase {

    @Test
    public void generatePhysicalPositionsWithFixedDealPrice() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceBuyDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceBuyDeal.getId());

        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceBuyDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());

        assertEquals(0,
                fixedPriceBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));

        assertEquals(false, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());


        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(marketIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getMarketPriceRiskFactorId().getId());
        assertNotNull(positionSnapshot.getMarketPriceFxRiskFactorId());
    }


    @Test
    public void generatePhysicalPositionsWithIndexDealPrice() {
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

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());

        assertNull(positionSnapshot.getDealPriceFxRiskFactorId());

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(marketIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getMarketPriceRiskFactorId().getId());
        assertNotNull(positionSnapshot.getMarketPriceFxRiskFactorId());

    }



    @Test
    public void generatePhysicalPositionsWithIndexPlus() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(indexPlusBuyDeal.getId()));

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.USD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                indexPlusBuyDeal.getId());

        flush();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexPlusBuyDeal.generateEntityId());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);

        assertNotNull(positionSnapshot.getFixedPriceFxRiskFactorId());
    }

}
