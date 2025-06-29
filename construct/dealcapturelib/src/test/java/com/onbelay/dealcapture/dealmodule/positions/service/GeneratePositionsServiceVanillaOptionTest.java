package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.VanillaOptionDealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GeneratePositionsServiceVanillaOptionTest extends VanillaOptionDealServiceTestCase {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;


    @Test
    public void generateVanillaOptionPositions() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(buyPutOptionDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                buyPutOptionDeal.getId());

        flush();
        clearCache();

        VanillaOptionDeal deal = (VanillaOptionDeal) dealRepository.load(buyPutOptionDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                buyPutOptionDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        VanillaOptionPositionSnapshot positionSnapshot = (VanillaOptionPositionSnapshot) positionSnapshots.get(0);
        assertEquals(buyPutOptionDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(buyPutOptionDeal.getDealDetail().getEndDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.MONTHLY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());


        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(monthlyOptionIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getUnderlyingPriceRiskFactorId().getId());
    }



}
