package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsWithPowerProfileWithBasisServiceTest extends PositionsServiceWithPowerProfileWithBasisTestCase {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    @Test
    public void generatePhysicalPositionsWithFixedDealPrice() {

        powerProfileService.updatePositionGenerationStatusToPending(List.of(powerProfileWithBasis.getId()));

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceMarketPowerProfileDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                startDate,
                endDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePowerProfilePositionsService.generatePowerProfilePositions(
                "test",
                context,
                List.of(powerProfileWithBasis.getId()));
        flush();

        generatePositionsService.generatePositions(
                "test",
                context,
                fixedPriceMarketPowerProfileDeal.getId());

        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceMarketPowerProfileDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceMarketPowerProfileDeal.generateEntityId());

        assertEquals(107, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceMarketPowerProfileDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixedPriceMarketPowerProfileDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());


        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixedPriceMarketPowerProfileDeal.generateEntityId());
        assertEquals(129, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot hourlyPositionSnapshot = hourlyPositionSnapshots.get(0);
        assertNotNull(hourlyPositionSnapshot.getPowerProfilePositionId());
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());
        assertEquals(PowerFlowCode.SETTLED, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        assertEquals(PriceTypeCode.MARKET_PRICE, hourlyPositionSnapshot.getDetail().getPriceTypeCode());
    }


    @Test
    public void generatePhysicalPositionsWithIndexDealPrice() {

        powerProfileService.updatePositionGenerationStatusToPending(List.of(powerProfile.getId()));
        dealService.updateDealPositionGenerationStatusToPending(List.of(indexSellMarketPowerProfileDeal.getId()));

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                startDate,
                endDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePowerProfilePositionsService.generatePowerProfilePositions(
                "test",
                context,
                List.of(powerProfile.getId()));
        flush();


        generatePositionsService.generatePositions(
                "test",
                context,
                indexSellMarketPowerProfileDeal.getId());

        flush();

        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                indexSellMarketPowerProfileDeal.generateEntityId());

        assertEquals(91, positionSnapshots.size());
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(indexSellMarketPowerProfileDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());


        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(indexSellMarketPowerProfileDeal.generateEntityId());
        assertEquals(182, hourlyPositionSnapshots.size());

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


        assertNull(positionSnapshot.getMarketPriceRiskFactorId());
        assertEquals(PowerFlowCode.SETTLED, marketPriceHourlyPositionSnapshot.getDetail().getPowerFlowCode());
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
