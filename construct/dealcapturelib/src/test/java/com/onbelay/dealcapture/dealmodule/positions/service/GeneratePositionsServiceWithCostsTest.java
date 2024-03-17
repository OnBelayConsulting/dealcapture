package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.DealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.BigDecimalConversion;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsServiceWithCostsTest extends DealServiceTestCase {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;


    private LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
    private LocalDate toMarketDate = LocalDate.of(2024, 1, 31);

    @Override
    public void setUp() {
        super.setUp();

    }

    @Test
    public void generateWithFixedCost() {
        DealCostSnapshot cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.BROKERAGE_DAILY_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-.50));
        fixedPriceBuyDeal.saveDealCosts(List.of(cost));
        flush();


        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceBuyDeal.getId()));

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
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
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getDealPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDealPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDealPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDealPositionDetail().getCreateUpdateDateTime());
        assertEquals("0", positionSnapshot.getDealPositionDetail().getErrorCode());

        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                fixedPriceBuyDeal.getDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getFixedPriceCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getFixedPriceUnitOfMeasure());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        assertEquals(CostNameCode.BROKERAGE_DAILY_FEE.getCode(), positionSnapshot.getCostDetail().getCost1Name());
        assertEquals(0, BigDecimal.valueOf(-.50).compareTo(positionSnapshot.getCostDetail().getCost1Amount()));

    }


    @Test
    public void generateWithMultipleCosts() {
        ArrayList<DealCostSnapshot> costs = new ArrayList<>();
        DealCostSnapshot cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.BROKERAGE_DAILY_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-.50));
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.FACILITY_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-.25));
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.FACILITY_FLAT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.BROKERAGE_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        costs.add(cost);


        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.TRANSPORTATION_FLAT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        costs.add(cost);


        fixedPriceBuyDeal.saveDealCosts(costs);
        flush();


        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceBuyDeal.getId()));

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
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
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getDealPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDealPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDealPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDealPositionDetail().getCreateUpdateDateTime());
        assertEquals("0", positionSnapshot.getDealPositionDetail().getErrorCode());

        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                fixedPriceBuyDeal.getDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getFixedPriceCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getFixedPriceUnitOfMeasure());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        for (int i =1; i < 6; i++) {
            assertNotNull(positionSnapshot.getCostDetail().getCostAmount(i));
            assertNotNull(positionSnapshot.getCostDetail().getCostName(i));
        }
        assertEquals(CostNameCode.BROKERAGE_DAILY_FEE.getCode(), positionSnapshot.getCostDetail().getCost1Name());
        assertEquals(0, BigDecimal.valueOf(-.50).compareTo(positionSnapshot.getCostDetail().getCost1Amount()));

    }


    @Test
    public void generateWithCostsOverFive() {
        ArrayList<DealCostSnapshot> costs = new ArrayList<>();
        DealCostSnapshot cost = new DealCostSnapshot();
        BigDecimal totalFixed = BigDecimal.ZERO;
        BigDecimal totalPerUnit = BigDecimal.ZERO;

        cost.getDetail().setCostName(CostNameCode.BROKERAGE_DAILY_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-.50));
        totalFixed = totalFixed.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.FACILITY_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-.25));
        totalPerUnit = totalPerUnit.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.FACILITY_FLAT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        totalFixed = totalFixed.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.BROKERAGE_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        totalPerUnit = totalPerUnit.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.TRANSPORTATION_FLAT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        totalFixed = totalFixed.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);


        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.TRANSPORTATION_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        totalPerUnit = totalPerUnit.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);


        fixedPriceBuyDeal.saveDealCosts(costs);
        flush();


        dealService.updateDealPositionGenerationStatusToPending(List.of(fixedPriceBuyDeal.getId()));

        EvaluationContext context = EvaluationContext
                .build()
                .withCurrency(CurrencyCode.CAD)
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
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findByDeal(
                fixedPriceBuyDeal.generateEntityId());

        assertEquals(31, positionSnapshots.size());

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) positionSnapshots.get(0);
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDealPositionDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getDealPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDealPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDealPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDealPositionDetail().getCreateUpdateDateTime());
        assertEquals("0", positionSnapshot.getDealPositionDetail().getErrorCode());

        assertEquals(ValuationCode.FIXED, positionSnapshot.getDetail().getDealPriceValuationCode());
        assertEquals(0,
                fixedPriceBuyDeal.getDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getFixedPriceCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getFixedPriceUnitOfMeasure());

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertEquals(ValuationCode.INDEX, positionSnapshot.getDetail().getMarketPriceValuationCode());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        assertEquals(CostNameCode.TOTAL_FIXED_FEE.getCode(), positionSnapshot.getCostDetail().getCost1Name());
        assertEquals(0, totalFixed.compareTo(positionSnapshot.getCostDetail().getCost1Amount()));

        assertEquals(CostNameCode.TOTAL_PER_UNIT_FEE.getCode(), positionSnapshot.getCostDetail().getCost2Name());
        assertEquals(0, totalPerUnit.compareTo(positionSnapshot.getCostDetail().getCost2Amount()));

    }


}
