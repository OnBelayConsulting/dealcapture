package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
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

public class ValuePositionsServiceWithPowerProfileAndBasisTest extends PositionsServiceWithPowerProfileWithBasisTestCase {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    @Autowired
    private ValuePositionsService valuePositionsService;

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Autowired
    private ValuePowerProfilePositionsService valuePowerProfilePositionsService;


    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    private LocalDateTime observedDateTime = LocalDateTime.of(2024, 1, 1, 2, 43);

    @Override
    public void setUp() {
        super.setUp();


        PriceIndexFixture.generateHourlyPriceCurves(
                settledHourlyIndex,
                startDate,
                endDate,
                BigDecimal.valueOf(4),
                observedDateTime);

        PriceIndexFixture.generateHourlyPriceCurves(
                forwardHourlyIndex,
                startDate,
                endDate,
                BigDecimal.valueOf(4),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                hubIndex,
                startDate,
                endDate,
                BigDecimal.valueOf(3),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                offPeakDailyIndex,
                startDate,
                endDate,
                BigDecimal.valueOf(3),
                observedDateTime);


        PriceIndexFixture.generateDailyPriceCurves(
                onPeakDailyIndex,
                startDate,
                endDate,
                BigDecimal.valueOf(2),
                observedDateTime);
    }

    @Test
    public void valuePhysicalPositionsWithBuyFixedPrice() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceMarketPowerProfileDeal.getId()));

        powerProfileService.updatePositionGenerationStatusToPending(List.of(powerProfileWithBasis.getId()));

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

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        fxRiskFactorService.valueRiskFactors(
                new DefinedQuery("FxRiskFactor"),
                LocalDateTime.now());


        valuePowerProfilePositionsService.valuePositions(
                powerProfileWithBasis.generateEntityId(),
                CurrencyCode.CAD,
                createdDateTime,
                LocalDateTime.now());
        flush();


        valuePositionsService.valuePositions(
                fixedPriceMarketPowerProfileDeal.generateEntityId(),
                CurrencyCode.CAD,
                startDate,
                endDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();

        PhysicalDeal deal = (PhysicalDeal) dealRepository.load(fixedPriceMarketPowerProfileDeal.generateEntityId());
        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixedPriceMarketPowerProfileDeal.generateEntityId());

        assertEquals(107, positionSnapshots.size());
        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(-30).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(10).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());


        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixedPriceMarketPowerProfileDeal.generateEntityId());
        assertEquals(129, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot hourlyPositionSnapshot = hourlyPositionSnapshots.get(0);
        assertNotNull(hourlyPositionSnapshot.getPowerProfilePositionId());
        assertNull(positionSnapshot.getMarketPriceRiskFactorId());
        assertEquals(PowerFlowCode.SETTLED, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        assertEquals(PriceTypeCode.MARKET_PRICE, hourlyPositionSnapshot.getDetail().getPriceTypeCode());
        boolean priceFound = false;
        for (int i=1; i< 25; i++) {
            if (hourlyPositionSnapshot.getHourFixedValueDetail().getHourFixedValue(i) != null)
                priceFound = true;
        }
        assertTrue(priceFound);
    }


}
