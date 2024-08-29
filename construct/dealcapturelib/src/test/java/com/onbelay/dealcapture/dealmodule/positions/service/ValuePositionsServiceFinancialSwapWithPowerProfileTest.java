package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.FinancialSwapDealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
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

public class ValuePositionsServiceFinancialSwapWithPowerProfileTest extends FinancialSwapDealServiceTestCase {

    @Autowired
    private DealPositionService dealPositionService;
    @Autowired
    private PowerProfileService powerProfileService;
    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Autowired
    private ValuePowerProfilePositionsService valuePowerProfilePositionsService;

    private LocalDateTime observedDateTime = LocalDateTime.of(2024, 1, 1, 1, 43);


    @Autowired
    private GeneratePositionsService generatePositionsService;


    @Autowired
    private ValuePositionsService valuePositionsService;


    private LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2024, 1, 31);

    @Override
    public void setUp() {
        super.setUp();


        PriceIndexFixture.generateHourlyPriceCurves(
                settledHourlyIndex,
                startDate,
                endDate,
                BigDecimal.valueOf(4),
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
        flush();

    }




    @Test
    public void valueFixed4PowerProfileSwapPositions() {
        powerProfileService.updatePositionGenerationStatusToPending(List.of(powerProfile.getId()));

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixed4PowerProfileBuyDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                fromMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);

        generatePowerProfilePositionsService.generatePowerProfilePositions(
                "test",
                context,
                List.of(powerProfile.getId()));
        flush();



        generatePositionsService.generatePositions(
                "test",
                context,
                fixed4PowerProfileBuyDeal.getId());

        flush();
        clearCache();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        List<PriceRiskFactorSnapshot> riskFactorSnapshots = priceRiskFactorService.findByPriceIndexIds(
                List.of(settledHourlyIndex.getId()),
                fromMarketDate,
                toMarketDate);


        valuePowerProfilePositionsService.valuePositions(
                powerProfile.generateEntityId(),
                CurrencyCode.CAD,
                createdDateTime,
                LocalDateTime.now());
        flush();


        valuePositionsService.valuePositions(
                fixed4PowerProfileBuyDeal.generateEntityId(),
                CurrencyCode.CAD,
                fromMarketDate,
                fromMarketDate,
                createdDateTime,
                LocalDateTime.now());
        flush();
        clearCache();


        FinancialSwapDeal deal = (FinancialSwapDeal) dealRepository.load(fixed4PowerProfileBuyDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixed4PowerProfileBuyDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);


        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixed4PowerProfileBuyDeal.generateEntityId());
        assertEquals(1, hourlyPositionSnapshots.size());


        assertEquals(fixed4PowerProfileBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixed4PowerProfileBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(0, BigDecimal.valueOf(30).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));
        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getCostSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(30).compareTo(positionSnapshot.getSettlementDetail().getSettlementAmount()));
        assertEquals(0, BigDecimal.valueOf(30).compareTo(positionSnapshot.getSettlementDetail().getTotalSettlementAmount()));
        assertNotNull(positionSnapshot.getSettlementDetail().getSettlementCurrencyCodeValue());
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());

        assertEquals(0,
                fixed4PowerProfileBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());


        DealHourlyPositionSnapshot hourlyPositionSnapshot = hourlyPositionSnapshots.get(0);
        assertNotNull(hourlyPositionSnapshot.getPowerProfilePositionId());
        assertNull(positionSnapshot.getReceivesPriceRiskFactorId());
        assertEquals(PowerFlowCode.SETTLED, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        assertEquals(PriceTypeCode.RECEIVES_PRICE, hourlyPositionSnapshot.getDetail().getPriceTypeCode());
        for (int i=1; i< 25; i++) {
            assertNotNull(hourlyPositionSnapshot.getHourFixedValueDetail().getHourFixedValue(i));
        }

    }


}
