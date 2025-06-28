package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDeal;
import com.onbelay.dealcapture.dealmodule.deal.service.VanillaOptionDealServiceTestCase;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ValuePositionsServiceVanillaOptionTest extends VanillaOptionDealServiceTestCase {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private DealPositionService dealPositionService;
    @Autowired
    private ValuePositionsService valuePositionsService;

    @Autowired
    private GeneratePositionsService generatePositionsService;


    @Override
    public void setUp() {
        super.setUp();

        PriceIndexFixture.generateMonthlyPriceCurves(
                monthlyOptionIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(11.34),
                observedDateTime);

        flush();


        PriceIndexFixture.generateMonthlyPriceCurves(
                monthlySecondOptionIndex,
                fromMarketDate,
                toMarketDate,
                BigDecimal.valueOf(9.50),
                observedDateTime);

        flush();


    }

    /**
     * Buy CAll option in the money before expiry
     * Strike Price: 10.00
     * Underlying Price: 11.34
     * MtM : 14.432 (greater than at expiry because the optionality is worth something.)
     */
    @Test
    public void valueBuyCallVanillaOptionPositionsBeforeExpiry() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(buyCallOptionDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                buyCallOptionDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        valuePositionsService.valuePositions(
                List.of(buyCallOptionDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                observedDateTime);

        flush();
        clearCache();



        VanillaOptionDeal deal = (VanillaOptionDeal) dealRepository.load(buyCallOptionDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                buyCallOptionDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        VanillaOptionPositionSnapshot positionSnapshot = (VanillaOptionPositionSnapshot) positionSnapshots.get(0);
        assertEquals(buyCallOptionDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(buyCallOptionDeal.getDealDetail().getEndDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.MONTHLY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());


        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());
        logger.debug(positionSnapshot.getSettlementDetail().getMarkToMarketValuation());
        assertEquals(0, BigDecimal.valueOf(14.432).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(monthlyOptionIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getUnderlyingPriceRiskFactorId().getId());
    }


    /**
     * Buy CAll option out of the money in the money before expiry
     * Strike Price: 10.00
     * Underlying Price: 9.50
     * MtM : Still has some value because the underlying is close to strike and the volatility is > .20
     */
    @Test
    public void valueBuyCallVanillaOptionOutOfMoneyPositionsBeforeExpiry() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(buyCallOptionOutOfMoneyDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                buyCallOptionOutOfMoneyDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        valuePositionsService.valuePositions(
                List.of(buyCallOptionOutOfMoneyDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                observedDateTime);

        flush();
        clearCache();



        VanillaOptionDeal deal = (VanillaOptionDeal) dealRepository.load(buyCallOptionOutOfMoneyDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                buyCallOptionOutOfMoneyDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        VanillaOptionPositionSnapshot positionSnapshot = (VanillaOptionPositionSnapshot) positionSnapshots.get(0);
        assertEquals(buyCallOptionOutOfMoneyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(buyCallOptionOutOfMoneyDeal.getDealDetail().getEndDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.MONTHLY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());


        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());
        logger.debug(positionSnapshot.getSettlementDetail().getMarkToMarketValuation());
        assertEquals(0, BigDecimal.valueOf(1.03).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(monthlySecondOptionIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getUnderlyingPriceRiskFactorId().getId());
    }



    /**
     * Sell CAll option in the money before expiry
     * Strike Price: 10.00
     * Underlying Price: 11.34
     * MtM : 10 * -1.34 = -13.40
     */
    @Test
    public void valueSellCallVanillaOptionPositionsBeforeExpiry() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(sellCallOptionDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                sellCallOptionDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        valuePositionsService.valuePositions(
                List.of(sellCallOptionDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                observedDateTime);

        flush();
        clearCache();



        VanillaOptionDeal deal = (VanillaOptionDeal) dealRepository.load(sellCallOptionDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                sellCallOptionDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        VanillaOptionPositionSnapshot positionSnapshot = (VanillaOptionPositionSnapshot) positionSnapshots.get(0);
        assertEquals(sellCallOptionDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(sellCallOptionDeal.getDealDetail().getEndDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.MONTHLY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());


        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());
        logger.debug(positionSnapshot.getSettlementDetail().getMarkToMarketValuation());
        assertEquals(0, BigDecimal.valueOf(-14.432).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(monthlyOptionIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getUnderlyingPriceRiskFactorId().getId());
    }


    /**
     * Buy CAll option in the money at expiry
     * Strike Price: 10.00
     * Underlying Price: 11.34
     * MtM : 10 * 1.34 = 13.40
     */
    @Test
    public void valueBuyCallInMoneyVanillaOptionPositionsAtExpiry() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(buyCallOptionDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                buyCallOptionDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        valuePositionsService.valuePositions(
                List.of(buyCallOptionDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.of(2024, 1, 31, 1, 1));

        flush();
        clearCache();



        VanillaOptionDeal deal = (VanillaOptionDeal) dealRepository.load(buyCallOptionDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                buyCallOptionDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        VanillaOptionPositionSnapshot positionSnapshot = (VanillaOptionPositionSnapshot) positionSnapshots.get(0);
        assertEquals(buyCallOptionDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(buyCallOptionDeal.getDealDetail().getEndDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.MONTHLY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());


        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertEquals(0, BigDecimal.valueOf(13.40).compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(monthlyOptionIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getUnderlyingPriceRiskFactorId().getId());
    }


    /**
     * Buy CAll option out of the money at expiry
     * Strike Price: 10.00
     * Underlying Price: 2.00
     * MtM : 0
     */
    @Test
    public void valueBuyCallOutOfMoneyVanillaOptionPositionsAtExpiry() {

        dealService.updateDealPositionGenerationStatusToPending(List.of(buyCallOptionOutOfMoneyDeal.getId()));


        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                CurrencyCode.CAD,
                createdDateTime,
                fromMarketDate,
                toMarketDate)
                .withUnitOfMeasureCode(UnitOfMeasureCode.GJ);


        generatePositionsService.generatePositions(
                "test",
                context,
                buyCallOptionOutOfMoneyDeal.getId());

        flush();

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        valuePositionsService.valuePositions(
                List.of(buyCallOptionOutOfMoneyDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.of(2024, 1, 31, 1, 1));

        flush();
        clearCache();



        VanillaOptionDeal deal = (VanillaOptionDeal) dealRepository.load(buyCallOptionOutOfMoneyDeal.generateEntityId());

        assertEquals(PositionGenerationStatusCode.COMPLETE, deal.getDealDetail().getPositionGenerationStatusCode());
        assertNotNull(deal.getDealDetail().getPositionGenerationDateTime());
        List<DealPositionSnapshot> positionSnapshots = dealPositionService.findPositionsByDeal(
                buyCallOptionOutOfMoneyDeal.generateEntityId());

        assertEquals(1, positionSnapshots.size());

        VanillaOptionPositionSnapshot positionSnapshot = (VanillaOptionPositionSnapshot) positionSnapshots.get(0);
        assertEquals(buyCallOptionOutOfMoneyDeal.getDealDetail().getStartDate(), positionSnapshot.getPositionDetail().getStartDate());
        assertEquals(buyCallOptionOutOfMoneyDeal.getDealDetail().getEndDate(), positionSnapshot.getPositionDetail().getEndDate());

        assertEquals(FrequencyCode.MONTHLY, positionSnapshot.getPositionDetail().getFrequencyCode());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getPositionDetail().getCurrencyCode());
        assertEquals(UnitOfMeasureCode.GJ, positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
        assertEquals(0, BigDecimal.TEN.compareTo(positionSnapshot.getPositionDetail().getVolumeQuantityValue()));
        assertNotNull(positionSnapshot.getPositionDetail().getCreatedDateTime());
        assertEquals("0", positionSnapshot.getPositionDetail().getErrorCode());


        assertEquals(true, positionSnapshot.getSettlementDetail().getIsSettlementPosition().booleanValue());
        assertEquals(CurrencyCode.CAD, positionSnapshot.getSettlementDetail().getSettlementCurrencyCode());

        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(monthlySecondOptionIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getUnderlyingPriceRiskFactorId().getId());
    }


    /**
     * Buy Put option out of the money at expiry
     * Strike Price: 10.00
     * Underlying Price: 11.34
     * MtM : 0.0
     */
    @Test
    public void valueBuyPutInMoneyVanillaOptionPositionsAtExpiry() {

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

        priceRiskFactorService.valueRiskFactors(
                new DefinedQuery("PriceRiskFactor"),
                LocalDateTime.now());
        flush();
        clearCache();

        valuePositionsService.valuePositions(
                List.of(buyPutOptionDeal.getId()),
                CurrencyCode.CAD,
                fromMarketDate,
                toMarketDate,
                createdDateTime,
                LocalDateTime.of(2024, 1, 31, 1, 1));

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

        assertEquals(0, BigDecimal.ZERO.compareTo(positionSnapshot.getSettlementDetail().getMarkToMarketValuation()));

        PriceRiskFactorSnapshot marketRiskFactor = priceRiskFactorService.findByPriceIndexIds(
                List.of(monthlyOptionIndex.getId()),
                startDate,
                endDate).get(0);

        assertEquals(marketRiskFactor.getEntityId().getId(), positionSnapshot.getUnderlyingPriceRiskFactorId().getId());
    }



}
