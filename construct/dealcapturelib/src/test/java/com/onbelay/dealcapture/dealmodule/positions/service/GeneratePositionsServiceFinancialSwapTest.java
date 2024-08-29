package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.FinancialSwapDealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionSnapshot;
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

public class GeneratePositionsServiceFinancialSwapTest extends FinancialSwapDealServiceTestCase {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private PowerProfileService powerProfileService;
    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    private LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2024, 1, 31);


    @Test
    public void generateFixed4FloatSwapPositions() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(fixed4FloatSellDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                fixed4FloatSellDeal.getId());

        flush();
        clearCache();

        FinancialSwapDeal deal = (FinancialSwapDeal) dealRepository.load(fixed4FloatSellDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixed4FloatSellDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixed4FloatSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixed4FloatSellDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());

        assertEquals(0,
                fixed4FloatSellDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(receivesIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getReceivesPriceRiskFactorId().getId());
    }


    @Test
    public void generateFloat4FloatSwapPositions() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(float4FloatBuyDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                float4FloatBuyDeal.getId());

        flush();
        clearCache();

        FinancialSwapDeal deal = (FinancialSwapDeal) dealRepository.load(float4FloatBuyDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                float4FloatBuyDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);
        assertEquals(float4FloatBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(float4FloatBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());

        assertNull(positionSnapshot.getPositionDetail().getFixedPriceValue());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        PriceRiskFactorSnapshot paysRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(paysIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(paysRiskFactor.getEntityId().getId(), positionSnapshot.getPaysPriceRiskFactorId().getId());

        PriceRiskFactorSnapshot receivesRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(receivesIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(receivesRiskFactor.getEntityId().getId(), positionSnapshot.getReceivesPriceRiskFactorId().getId());
    }



    @Test
    public void generateFloat4FloatPlusSwapPositions() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(float4FloatPlusBuyDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                float4FloatPlusBuyDeal.getId());

        flush();
        clearCache();

        FinancialSwapDeal deal = (FinancialSwapDeal) dealRepository.load(float4FloatPlusBuyDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                float4FloatPlusBuyDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);
        assertEquals(float4FloatPlusBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(float4FloatPlusBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());

        assertEquals(0,
                float4FloatPlusBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        PriceRiskFactorSnapshot paysRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(paysIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(paysRiskFactor.getEntityId().getId(), positionSnapshot.getPaysPriceRiskFactorId().getId());

        PriceRiskFactorSnapshot receivesRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(receivesIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(receivesRiskFactor.getEntityId().getId(), positionSnapshot.getReceivesPriceRiskFactorId().getId());
    }


    @Test
    public void generateFixed4PowerProfileSwapPositions() {
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

        FinancialSwapDeal deal = (FinancialSwapDeal) dealRepository.load(fixed4PowerProfileBuyDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                fixed4PowerProfileBuyDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        FinancialSwapPositionSnapshot positionSnapshot = (FinancialSwapPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixed4PowerProfileBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(fixed4PowerProfileBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());

        assertEquals(0,
                fixed4PowerProfileBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getPositionDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());


        List<DealHourlyPositionSnapshot>  hourlyPositionSnapshots = dealPositionService.findHourlyPositionsByDeal(fixed4PowerProfileBuyDeal.generateEntityId());
        assertEquals(1, hourlyPositionSnapshots.size());

        DealHourlyPositionSnapshot hourlyPositionSnapshot = hourlyPositionSnapshots.get(0);
        assertNotNull(hourlyPositionSnapshot.getPowerProfilePositionId());
        assertNull(positionSnapshot.getReceivesPriceRiskFactorId());
        assertEquals(PowerFlowCode.SETTLED, hourlyPositionSnapshot.getDetail().getPowerFlowCode());
        assertEquals(CurrencyCode.CAD, hourlyPositionSnapshot.getDetail().getCurrencyCode());
        assertEquals(PriceTypeCode.RECEIVES_PRICE, hourlyPositionSnapshot.getDetail().getPriceTypeCode());

    }



}
