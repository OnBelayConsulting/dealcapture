package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsServiceWithBasisTest extends PositionsServiceWithBasisTestCase {

    @Autowired
    protected PriceRiskFactorService priceRiskFactorService;

    @Test
    public void generateWithFixedDealPrice() {
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);
        flush();
        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceSellDeal.getId()));

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceSellDeal.getId());

        flush();

        PriceRiskFactorSnapshot basisRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(basisPriceIndex.getId()),
                startDate,
                endDate).get(0);

        PriceRiskFactorSnapshot basisToBasisRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(basisToBasisPriceIndex.getId()),
                startDate,
                endDate).get(0);

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(marketIndex.getId()),
                startDate,
                endDate).get(0);


        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceSellDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertNull(positionSnapshot.getDealPriceRiskFactorId());
        assertEquals(basisToBasisRiskFactor.getEntityId().getId(), positionSnapshot.getMarketPriceRiskFactorId().getId());
        assertEquals(2, positionSnapshot.getMarketPriceMappings().size());

        assertNotNull(positionSnapshot.getMarketPriceMappings()
                .stream()
                .filter(c -> c.getPriceRiskFactorId().getId().equals(marketRiskFactor.getEntityId().getId()))
                .findFirst().get());
        assertNotNull(positionSnapshot.getMarketPriceMappings()
                .stream()
                .filter(c -> c.getPriceRiskFactorId().getId().equals(basisRiskFactor.getEntityId().getId()))
                .findFirst().get());
    }


    @Test
    public void generateWithBasisIndexDealPrice() {
        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
                .withCreatedDateTime(createdDateTime)
                .withUnitOfMeasure(UnitOfMeasureCode.GJ)
                .withStartPositionDate(fromMarketDate);

        dealService.updateDealPositionGenerationStatusToPending(List.of(indexSellDeal.getId()));


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
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());
        assertNotNull(positionSnapshot.getDealPriceRiskFactorId());
        assertEquals(2, positionSnapshot.getDealPriceMappings().size());

    }

}
