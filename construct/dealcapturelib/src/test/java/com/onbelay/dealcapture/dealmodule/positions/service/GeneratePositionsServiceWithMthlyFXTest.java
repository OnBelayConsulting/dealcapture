package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsServiceWithMthlyFXTest extends PositionsServiceWithMthlyFxTestCase {

    @Test
    public void generatePhysicalPositionsWithFixedDealPrice() {

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
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceBuyDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getDealPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDealPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDealPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDealPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getDealPositionDetail().getErrorCode());

        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                fixedPriceBuyDeal.getDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getFixedPriceCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getFixedPriceUnitOfMeasure());

        assertEquals(false, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());

        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(marketIndex.getId()),
                startDate,
                endDate).get(0);

        List<FxRiskFactorSnapshot> fxRiskFactorSnapshots = fxRiskFactorService.findByFxIndexIds(
                List.of(fxIndex.getId()),
                startDate,
                endDate);
        assertEquals(1, fxRiskFactorSnapshots.size());
        FxRiskFactorSnapshot fxRiskFactorSnapshot = fxRiskFactorSnapshots.get(0);

        for (DealPositionSnapshot position : positionSnapshots) {
            PhysicalPositionSnapshot physicalPositionSnapshot = (PhysicalPositionSnapshot) position;
            assertEquals(fxRiskFactorSnapshot.getEntityId().getId(), physicalPositionSnapshot.getMarketPriceFxRiskFactorId().getId());
        }

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getMarketPriceRiskFactorId().getId());
    }


    @Test
    public void generatePhysicalPositionsWithIndexDealPrice() {
        dealService.updateDealPositionGenerationStatusToPending(List.of(indexBuyDeal.getId()));
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        generatePositionsService.generatePositions(
                "test",
                context,
                indexBuyDeal.getId());

        flush();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexBuyDeal.generateEntityId());

        assertTrue(positionSnapshots.size() > 0);
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());

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
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.USD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

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
