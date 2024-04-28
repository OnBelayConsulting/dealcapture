package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsServiceWithBasisAndFXTest extends PositionsServiceWithBasisAndFxTestCase {

    @Autowired
    protected PriceRiskFactorService priceRiskFactorService;

    @Test
    public void generateWithFixedDealPrice() {

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

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

       PositionRiskFactorMappingSnapshot marketMapping = positionSnapshot.getMarketPriceMappings()
                .stream()
                .filter(c -> c.getPriceRiskFactorId().getId().equals(marketRiskFactor.getEntityId().getId()))
                .findFirst().get();


        PositionRiskFactorMappingSnapshot basisMapping = positionSnapshot.getMarketPriceMappings()
                .stream()
                .filter(c -> c.getPriceRiskFactorId().getId().equals(basisRiskFactor.getEntityId().getId()))
                .findFirst().get();

        assertNotNull(basisMapping.getFxRiskFactorId());


    }


    @Test
    public void generateInUSDWithBasisIndexDealPrice() {

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.USD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        dealService.updateDealPositionGenerationStatusToPending(List.of(indexSellDeal.getId()));


        generatePositionsService.generatePositions(
                "test",
                context,
                indexSellDeal.getId());

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
                indexSellDeal.generateEntityId());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getMarketPriceRiskFactorId().getId());
        assertNotNull(positionSnapshot.getMarketPriceFxRiskFactorId());

        assertEquals(basisToBasisRiskFactor.getEntityId().getId(),positionSnapshot.getDealPriceRiskFactorId().getId());
        assertNotNull(positionSnapshot.getDealPriceFxRiskFactorId());

        assertEquals(2, positionSnapshot.getDealPriceMappings().size());

        PositionRiskFactorMappingSnapshot marketMapping = positionSnapshot.getDealPriceMappings()
                .stream()
                .filter(c -> c.getPriceRiskFactorId().getId().equals(marketRiskFactor.getEntityId().getId()))
                .findFirst().get();
        assertNotNull(marketMapping.getFxRiskFactorId());

        PositionRiskFactorMappingSnapshot basisMapping = positionSnapshot.getDealPriceMappings()
                .stream()
                .filter(c -> c.getPriceRiskFactorId().getId().equals(basisRiskFactor.getEntityId().getId()))
                .findFirst().get();

        assertNull(basisMapping.getFxRiskFactorId());

    }



}
