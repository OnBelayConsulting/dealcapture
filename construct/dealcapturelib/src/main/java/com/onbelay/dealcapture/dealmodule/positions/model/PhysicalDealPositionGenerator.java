package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.PriceIndexPositionDateContainer;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.shared.enums.FrequencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;

public class PhysicalDealPositionGenerator extends BaseDealPositionGenerator {
    private static final Logger logger = LogManager.getLogger();

    public static DealPositionGenerator newGenerator(
            DealSummary dealSummary,
            RiskFactorManager riskFactorManager) {
        
        return new PhysicalDealPositionGenerator(
                dealSummary,
                riskFactorManager);
    }

    private PhysicalDealPositionGenerator(
            DealSummary dealSummary,
            RiskFactorManager riskFactorManager) {
        super(
                dealSummary,
                riskFactorManager);
    }

    @Override
    public void generatePositionHolders() {
        if (dealSummary.hasPowerProfile()) {
            if (powerProfilePositionMap.isEmpty())
                throw new OBRuntimeException(PositionErrorCode.MISSING_POWER_PROFILE_POSITIONS.getCode());

            generatePositionHoldersFromPowerProfile();
        } else {
            generatePositionHoldersDaily();
        }
   }

   public PhysicalDealSummary getPhysicalDealSummary() {
        return (PhysicalDealSummary) dealSummary;
   }

    private void generatePositionHoldersFromPowerProfile() {

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) dealSummary;

        if (context.getEndPositionDate().isBefore(context.getStartPositionDate()))
            return;

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {

            if (powerProfilePositionMap.containsKey(currentDate) ) {

                List<PowerFlowCode> powerFlowCodes = findUniquePowerCodesByDate(currentDate);

                for (PowerFlowCode powerFlowCode : powerFlowCodes) {
                    List<PowerProfilePositionView> powerProfilePositionViews = getPowerProfilePositionViewsBy(
                        currentDate,
                        powerFlowCode);

                    logger.debug("generating position for : " + currentDate);
                    PhysicalPositionHolder physicalPositionHolder = new PhysicalPositionHolder(physicalDealSummary);

                    setBasePositionHolderAttributes(
                            physicalPositionHolder,
                            currentDate);

                    physicalPositionHolder.getDetail().setPowerFlowCode(powerFlowCode);

                    physicalPositionHolder.setHourSlotsForPowerProfile(calculateHourlyQuantitySlots(powerProfilePositionViews.get(0)));
                    determinePositionQuantityFromPowerProfile(physicalPositionHolder);

                    generateCostPositionHolders(physicalPositionHolder);

                    // if deal price valuation is fixed or INDEX Plus
                    if (getPhysicalDealSummary().getDetail().getDealPriceValuationCode() == ValuationCode.FIXED ||
                            getPhysicalDealSummary().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {

                        determineFixedPrice(physicalPositionHolder);

                        determineFixedPriceRiskFactors(physicalPositionHolder);
                    }

                    if (getPhysicalDealSummary().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX ||
                            getPhysicalDealSummary().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
                        determineDealPriceRiskFactors(physicalPositionHolder);
                    }

                    generateDealHourlyPositionHoldersFromPowerProfile(
                            physicalPositionHolder,
                            powerProfilePositionViews,
                            PriceTypeCode.MARKET_PRICE);


                    positionHolders.add(physicalPositionHolder);
                }
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    public void generatePositionHoldersDaily() {

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) dealSummary;

        if (context.getEndPositionDate().isBefore(context.getStartPositionDate()))
            return;

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            PhysicalPositionHolder physicalPositionHolder = new PhysicalPositionHolder(physicalDealSummary);

            setBasePositionHolderAttributes(
                    physicalPositionHolder,
                    currentDate);
            physicalPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.DAILY);

            physicalPositionHolder.setHourSlotsForPowerProfile(calculate24HourSlots());
            determinePositionQuantity(physicalPositionHolder);

            generateCostPositionHolders(physicalPositionHolder);

            // if deal price valuation is fixed or INDEX Plus
            if (getPhysicalDealSummary().getDetail().getDealPriceValuationCode() == ValuationCode.FIXED ||
                    getPhysicalDealSummary().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {

                determineFixedPrice(physicalPositionHolder);

                determineFixedPriceRiskFactors(physicalPositionHolder);
            }

            if (getPhysicalDealSummary().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX ||
                    getPhysicalDealSummary().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
                determineDealPriceRiskFactors(physicalPositionHolder);
            }

            // Market
            determineMarketPriceRiskFactors(physicalPositionHolder);


            positionHolders.add(physicalPositionHolder);
            currentDate = currentDate.plusDays(1);
        }
    }


    private void determineDealPriceRiskFactors(PhysicalPositionHolder physicalPositionHolder) {

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) physicalPositionHolder.getDealSummary();

        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(physicalDealSummary.getDealPriceIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            physicalPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
            generateHourlyPositionHolder(
                    physicalPositionHolder,
                    PriceTypeCode.DEAL_PRICE,
                    physicalDealSummary.getDealPriceIndexId());
        } else {
            physicalPositionHolder.setDealPriceRiskFactorHolder(
                    riskFactorManager.determinePriceRiskFactor(
                            physicalDealSummary.getDealPriceIndexId(),
                            physicalPositionHolder.getDetail().getStartDate()));
        }

        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            physicalPositionHolder.setDealPriceFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            physicalPositionHolder.getDetail().getStartDate()));
        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(physicalDealSummary.getDealPriceIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            physicalPositionHolder.setBasisDealPriceHolders(
                    determineBasisPriceRiskFactors(
                            physicalPositionHolder.getDetail().getStartDate(),
                            physicalDealSummary.getDealPriceIndexId()));
        }
    }


    private void determineMarketPriceRiskFactors(PhysicalPositionHolder physicalPositionHolder) {

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) physicalPositionHolder.getDealSummary();

        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(physicalDealSummary.getMarketIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            physicalPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
            generateHourlyPositionHolder(
                    physicalPositionHolder,
                    PriceTypeCode.MARKET_PRICE,
                    physicalDealSummary.getMarketIndexId());
        } else {

            PriceRiskFactorHolder marketRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                    physicalDealSummary.getMarketIndexId(),
                    physicalPositionHolder.getDetail().getStartDate());

            physicalPositionHolder.setMarketRiskFactorHolder(marketRiskFactorHolder);
        }


        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            physicalPositionHolder.setMarketFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            physicalPositionHolder.getDetail().getStartDate()));

        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(
                        physicalDealSummary.getMarketIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            physicalPositionHolder.setBasisMarketHolders(
                    determineBasisPriceRiskFactors(
                        physicalPositionHolder.getDetail().getStartDate(),
                        physicalDealSummary.getMarketIndexId()));
        }
    }

    @Override
    public PositionGenerationResult generatePositionSnapshots() {

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) dealSummary;
        PositionGenerationResult generationResult = new PositionGenerationResult();

        for (BasePositionHolder positionHolder : positionHolders) {
            PhysicalPositionHolder physicalPositionHolder = (PhysicalPositionHolder) positionHolder;
            PhysicalPositionSnapshot positionSnapshot = new PhysicalPositionSnapshot();

            positionSnapshot.setDealId(new EntityId(positionHolder.getDealSummary().getId()));
            positionSnapshot.getPositionDetail().copyFrom(positionHolder.getDetail());
            positionSnapshot.getSettlementDetail().copyFrom(positionHolder.getSettlementDetail());
            positionSnapshot.getPositionDetail().setErrorCode("0");


            for (DealHourlyPositionHolder hourlyPositionHolder : positionHolder.getHourlyPositionHolders()) {
                DealHourlyPositionSnapshot hourlyPositionSnapshot = new DealHourlyPositionSnapshot();
                hourlyPositionSnapshot.getDetail().copyFrom(hourlyPositionHolder.getDetail());
                hourlyPositionSnapshot.setPowerProfilePositionId(hourlyPositionHolder.getPowerProfilePositionId());
                hourlyPositionSnapshot.setDealId(hourlyPositionHolder.getDealId());
                hourlyPositionSnapshot.setPriceIndexId(hourlyPositionHolder.getPriceIndexId());

                if (hourlyPositionHolder.getFxRiskFactorHolder() != null)
                    hourlyPositionSnapshot.setFxRiskFactorId(hourlyPositionHolder.getFxRiskFactorHolder().getRiskFactor().getEntityId());

                hourlyPositionSnapshot.getDetail().setErrorCode("0");

                if (hourlyPositionHolder.getPriceHourHolderMap().isNotEmpty()) {
                    for (int i = 1; i < 25; i++) {
                        if (hourlyPositionHolder.getPriceHourHolderMap().getHourPriceHolder(i) != null) {
                            PriceRiskFactorHolder riskFactorHolder = hourlyPositionHolder.getPriceHourHolderMap().getHourPriceHolder(i);
                            hourlyPositionSnapshot.getDetail().setCurrencyCode(riskFactorHolder.getPriceIndex().getDetail().getCurrencyCode());
                            hourlyPositionSnapshot.getDetail().setUnitOfMeasure(riskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
                            hourlyPositionSnapshot.getHourPriceRiskFactorIdMap().setHourPriceRiskFactorId(
                                    i,
                                    riskFactorHolder.getRiskFactor().getEntityId().getId());
                        }
                    }
                }
                generationResult.addDealHourlyPositionSnapshot(hourlyPositionSnapshot);
            }

            if (positionHolder.getDealHourByDayQuantity().isNotEmpty()) {
                DealHourlyPositionSnapshot hourlyPositionSnapshot = new DealHourlyPositionSnapshot();
                hourlyPositionSnapshot.getDetail().setPowerFlowCode(positionHolder.getDetail().getPowerFlowCode());
                hourlyPositionSnapshot.getDetail().setPriceTypeCode(PriceTypeCode.FIXED_QUANTITY);
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getPositionDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getPositionDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(positionSnapshot.getPositionDetail().getCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setUnitOfMeasure(positionSnapshot.getPositionDetail().getVolumeUnitOfMeasure());
                hourlyPositionSnapshot.getDetail().setCreatedDateTime(context.getCreatedDateTime());
                hourlyPositionSnapshot.setDealId(new EntityId(dealSummary.getId()));
                hourlyPositionSnapshot.getDetail().setErrorCode("0");
                generationResult.addDealHourlyPositionSnapshot(hourlyPositionSnapshot);

                for (int i=1; i < 25; i++) {
                    if (positionHolder.getDealHourByDayQuantity().getHourValue(i) != null) {
                        hourlyPositionSnapshot.getHourFixedValueDetail().setHourFixedValue(
                                i,
                                positionHolder.getDealHourByDayQuantity().getHourValue(i));
                    }
                }
            }

            if (positionHolder.getDealHourByDayPrice().isNotEmpty()) {
                DealHourlyPositionSnapshot hourlyPositionSnapshot = new DealHourlyPositionSnapshot();
                hourlyPositionSnapshot.getDetail().setPowerFlowCode(positionHolder.getDetail().getPowerFlowCode());
                hourlyPositionSnapshot.getDetail().setPriceTypeCode(PriceTypeCode.FIXED_PRICE);
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getPositionDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getPositionDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(physicalDealSummary.getDealDetail().getFixedPriceCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setUnitOfMeasure(physicalDealSummary.getDealDetail().getFixedPriceUnitOfMeasureCode());
                hourlyPositionSnapshot.getDetail().setCreatedDateTime(context.getCreatedDateTime());
                hourlyPositionSnapshot.setDealId(new EntityId(dealSummary.getId()));
                hourlyPositionSnapshot.getDetail().setErrorCode("0");
                generationResult.addDealHourlyPositionSnapshot(hourlyPositionSnapshot);

                for (int i=1; i < 25; i++) {
                    if (positionHolder.getDealHourByDayPrice().getHourValue(i) != null) {
                        hourlyPositionSnapshot.getHourFixedValueDetail().setHourFixedValue(
                                i,
                                positionHolder.getDealHourByDayPrice().getHourValue(i));
                    }
                }
            }

            // Market Price
            if (physicalPositionHolder.getMarketRiskFactorHolder() != null)
                positionSnapshot.setMarketPriceRiskFactorId(physicalPositionHolder.getMarketRiskFactorHolder().getRiskFactor().getEntityId());

            if (physicalPositionHolder.getMarketFxHolder() != null) {
                positionSnapshot.setMarketPriceFxRiskFactorId(
                        physicalPositionHolder.getMarketFxHolder().getRiskFactor().getEntityId());
            }

            for (PriceRiskFactorHolder factorHolder : physicalPositionHolder.getBasisMarketHolders()) {
                PositionRiskFactorMappingSnapshot mapping = new PositionRiskFactorMappingSnapshot();
                mapping.getDetail().setPriceTypeCode(PriceTypeCode.MARKET_PRICE);
                mapping.setPriceRiskFactorId(factorHolder.getRiskFactor().getEntityId());

                if (factorHolder.getFxRiskFactorHolder() != null)
                    mapping.setFxRiskFactorId(factorHolder.getFxRiskFactorHolder().getRiskFactor().getEntityId());
                positionSnapshot.addRiskFactorMappingSnapshot(mapping);
            }

            // Deal Price

            if (physicalPositionHolder.getFixedPriceFxHolder() != null) {
                positionSnapshot.setFixedPriceFxRiskFactorId(physicalPositionHolder.getFixedPriceFxHolder().getRiskFactor().getEntityId());
            }

            if (physicalPositionHolder.getDealPriceRiskFactorHolder() != null) {
                positionSnapshot.setDealPriceRiskFactorId(physicalPositionHolder.getDealPriceRiskFactorHolder().getRiskFactor().getEntityId());
            }

            if (physicalPositionHolder.getDealPriceFxHolder() != null) {
                positionSnapshot.setDealPriceFxRiskFactorId(physicalPositionHolder.getDealPriceFxHolder().getRiskFactor().getEntityId());
            }


            for (PriceRiskFactorHolder factorHolder : physicalPositionHolder.getBasisDealPriceHolders()) {
                PositionRiskFactorMappingSnapshot mapping = new PositionRiskFactorMappingSnapshot();
                mapping.getDetail().setPriceTypeCode(PriceTypeCode.DEAL_PRICE);

                mapping.setPriceRiskFactorId(factorHolder.getRiskFactor().getEntityId());
                if (factorHolder.getFxRiskFactorHolder() != null)
                    mapping.setFxRiskFactorId(factorHolder.getFxRiskFactorHolder().getRiskFactor().getEntityId());
                positionSnapshot.addRiskFactorMappingSnapshot(mapping);
            }

            generationResult.addDealPositionSnapshot(positionSnapshot);
        }

        generationResult.setCostPositionSnapshots(generateCostPositionSnapshots());

        return generationResult;
    }
}
