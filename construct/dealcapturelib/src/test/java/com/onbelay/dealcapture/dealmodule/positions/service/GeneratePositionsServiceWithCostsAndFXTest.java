package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.PhysicalDealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratePositionsServiceWithCostsAndFXTest extends PhysicalDealServiceTestCase {

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private GeneratePositionsService generatePositionsService;

    private LocalDateTime createdDateTime = LocalDateTime.of(2024, 1, 1, 10, 1);

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
        cost.getDetail().setCurrencyCode(CurrencyCode.USD);
        fixedPriceBuyDeal.saveDealCosts(List.of(cost));
        flush();


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
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getDetail().getErrorCode());

        assertEquals(0,
                fixedPriceBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());
        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        List<Integer> costPositionIds = dealPositionService.findCostPositionIdsByDeal(deal.generateEntityId());
        List<CostPositionSnapshot> costSnapshots = dealPositionService.findCostPositionsByIds(new QuerySelectedPage(costPositionIds));
        assertEquals(31, costSnapshots.size());
        List<CostPositionSnapshot> costsPerPosition = costSnapshots
                .stream()
                .filter( c->c.getDetail().getStartDate().isEqual(positionSnapshot.getDetail().getStartDate()))
                .toList();

        assertEquals(1, costsPerPosition.size());
        CostPositionSnapshot facilityPerUnitFee = costsPerPosition
                .stream()
                .filter(c-> c.getDetail().getCostNameCode() == CostNameCode.BROKERAGE_DAILY_FEE)
                .findFirst()
                .get();

        assertNotNull(facilityPerUnitFee.getCostFxRiskFactorId());
        assertFalse(facilityPerUnitFee.getDetail().getIsFixedValued());
    }


    @Test
    public void generateWithMultipleCosts() {
        ArrayList<DealCostSnapshot> costs = new ArrayList<>();
        DealCostSnapshot cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.BROKERAGE_DAILY_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(2.0));
        cost.getDetail().setCurrencyCode(CurrencyCode.CAD);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.FACILITY_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(1.0));
        cost.getDetail().setCurrencyCode(CurrencyCode.CAD);
        cost.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.GJ);
        costs.add(cost);

        fixedPriceBuyDeal.saveDealCosts(costs);
        flush();


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
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getDetail().getErrorCode());
        assertEquals(0,
                fixedPriceBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        List<Integer> costPositionIds = dealPositionService.findCostPositionIdsByDeal(deal.generateEntityId());
        List<CostPositionSnapshot> costSnapshots = dealPositionService.findCostPositionsByIds(new QuerySelectedPage(costPositionIds));
        assertEquals(62, costSnapshots.size());
        List<CostPositionSnapshot> costsPerPosition = costSnapshots
                .stream()
                .filter( c->c.getDetail().getStartDate().isEqual(positionSnapshot.getDetail().getStartDate()))
                .toList();

        assertEquals(2, costsPerPosition.size());
        CostPositionSnapshot facilityPerUnitFee = costsPerPosition
                .stream()
                .filter(c-> c.getDetail().getCostNameCode() == CostNameCode.FACILITY_PER_UNIT_FEE)
                .findFirst()
                .get();
        assertEquals(0, BigDecimal.TEN.compareTo(facilityPerUnitFee.getDetail().getCostAmount()));
        assertEquals(positionSnapshot.getDetail().getCreatedDateTime(), facilityPerUnitFee.getDetail().getCreatedDateTime());

        List<TotalCostPositionSummary> summaries = dealPositionService.calculateTotalCostPositionSummaries(
                fixedPriceBuyDeal.getId(),
                CurrencyCode.CAD,
                createdDateTime);
        assertEquals(31, summaries.size());
        TotalCostPositionSummary summary = summaries.get(0);
        assertEquals(0, BigDecimal.valueOf(12).compareTo(summary.getTotalCostAmount()));

    }


    @Test
    public void generateWithMultipleCostsDifferentUoM() {
        ArrayList<DealCostSnapshot> costs = new ArrayList<>();
        DealCostSnapshot cost = new DealCostSnapshot();
        BigDecimal totalFixed = BigDecimal.ZERO;
        BigDecimal totalPerUnit = BigDecimal.ZERO;

        cost.getDetail().setCostName(CostNameCode.BROKERAGE_DAILY_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-.50));
        cost.getDetail().setCurrencyCode(CurrencyCode.CAD);
        cost.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.MMBTU);
        totalFixed = totalFixed.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.FACILITY_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(1.00));
        cost.getDetail().setCurrencyCode(CurrencyCode.CAD);
        cost.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.MMBTU);
        totalPerUnit = totalPerUnit.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.FACILITY_FLAT_FEE);
        cost.getDetail().setCurrencyCode(CurrencyCode.CAD);
        cost.getDetail().setCostValue(BigDecimal.valueOf(1.00));
        totalFixed = totalFixed.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.BROKERAGE_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        cost.getDetail().setCurrencyCode(CurrencyCode.CAD);
        cost.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.MMBTU);
        totalPerUnit = totalPerUnit.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);

        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.TRANSPORTATION_FLAT_FEE);
        cost.getDetail().setCurrencyCode(CurrencyCode.CAD);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        totalFixed = totalFixed.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);


        cost = new DealCostSnapshot();
        cost.getDetail().setCostName(CostNameCode.TRANSPORTATION_PER_UNIT_FEE);
        cost.getDetail().setCostValue(BigDecimal.valueOf(-1.00));
        cost.getDetail().setCurrencyCode(CurrencyCode.CAD);
        cost.getDetail().setUnitOfMeasureCode(UnitOfMeasureCode.MMBTU);
        totalPerUnit = totalPerUnit.add(cost.getDetail().getCostValue(), MathContext.DECIMAL128);
        costs.add(cost);


        fixedPriceBuyDeal.saveDealCosts(costs);
        flush();


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
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDetail().getStartDate());
        assertEquals(fixedPriceBuyDeal.getDealDetail().getStartDate(), positionSnapshot.getDetail().getEndDate());

        assertEquals(FrequencyCode.DAILY, positionSnapshot.getDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getDetail().getErrorCode());

        assertEquals(0,
                fixedPriceBuyDeal.getDealDetail().getFixedPrice().getValue().compareTo(
                        positionSnapshot.getDetail().getFixedPriceValue()));

        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertNotNull(positionSnapshot.getMarketPriceRiskFactorId());

        List<Integer> costPositionIds = dealPositionService.findCostPositionIdsByDeal(deal.generateEntityId());
        List<CostPositionSnapshot> costSnapshots = dealPositionService.findCostPositionsByIds(new QuerySelectedPage(costPositionIds));
        assertEquals(186, costSnapshots.size());
        List<CostPositionSnapshot> costsPerPosition = costSnapshots
                .stream()
                .filter( c->c.getDetail().getStartDate().isEqual(positionSnapshot.getDetail().getStartDate()))
                .collect(Collectors.toList());

        assertEquals(6, costsPerPosition.size());
        CostPositionSnapshot facilityPerUnitFee = costsPerPosition
                .stream()
                .filter(c-> c.getDetail().getCostNameCode() == CostNameCode.FACILITY_PER_UNIT_FEE)
                .findFirst()
                .get();
        Conversion conversion = UnitOfMeasureConverter.findConversion(UnitOfMeasureCode.GJ, UnitOfMeasureCode.MMBTU);
        Price costPrice = new Price(
                facilityPerUnitFee.getDetail().getCostValue(),
                CurrencyCode.CAD,
                UnitOfMeasureCode.MMBTU);

        costPrice = costPrice.apply(conversion);
        Amount amount = costPrice.multiply(facilityPerUnitFee.getQuantity());
        amount = amount.round();
        assertEquals(0, amount.getValue().compareTo(facilityPerUnitFee.getDetail().getCostAmount()));
        assertEquals(positionSnapshot.getDetail().getCreatedDateTime(), facilityPerUnitFee.getDetail().getCreatedDateTime());
    }


}
