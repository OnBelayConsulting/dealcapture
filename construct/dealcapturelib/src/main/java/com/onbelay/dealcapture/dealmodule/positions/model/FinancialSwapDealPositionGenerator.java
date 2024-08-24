package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionSnapshot;
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

public class FinancialSwapDealPositionGenerator extends BaseDealPositionGenerator {
    private static final Logger logger = LogManager.getLogger();

    public static DealPositionGenerator newGenerator(
            DealSummary dealSummary,
            RiskFactorManager riskFactorManager) {

        return new FinancialSwapDealPositionGenerator(
                dealSummary,
                riskFactorManager);
    }

    private FinancialSwapDealPositionGenerator(
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

   public FinancialSwapDealSummary getFinancialSwapDealSummary() {
        return (FinancialSwapDealSummary) dealSummary;
   }

    private void generatePositionHoldersFromPowerProfile() {

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
                    FinancialSwapPositionHolder financialSwapPositionHolder = new FinancialSwapPositionHolder(getFinancialSwapDealSummary());

                    setBasePositionHolderAttributes(
                            financialSwapPositionHolder,
                            currentDate);

                    financialSwapPositionHolder.getDetail().setPowerFlowCode(powerFlowCode);

                    financialSwapPositionHolder.setHourSlotsForPowerProfile(calculateHourlyQuantitySlots(powerProfilePositionViews.get(0)));
                    determinePositionQuantityFromPowerProfile(financialSwapPositionHolder);

                    generateCostPositionHolders(financialSwapPositionHolder);

                    // if deal price valuation is fixed or INDEX Plus
                    if (getFinancialSwapDealSummary().getDetail().getPaysValuationCode() == ValuationCode.FIXED ||
                            getFinancialSwapDealSummary().getDetail().getPaysValuationCode() == ValuationCode.INDEX_PLUS) {

                        determineFixedPrice(financialSwapPositionHolder);

                        determineFixedPriceRiskFactors(financialSwapPositionHolder);
                    }

                    if (getFinancialSwapDealSummary().getDetail().getPaysValuationCode() == ValuationCode.INDEX ||
                            getFinancialSwapDealSummary().getDetail().getPaysValuationCode() == ValuationCode.INDEX_PLUS) {
                        determinePaysPriceRiskFactors(financialSwapPositionHolder);
                    }

                    generateDealHourlyPositionHoldersFromPowerProfile(
                            financialSwapPositionHolder,
                            powerProfilePositionViews,
                            PriceTypeCode.MARKET_PRICE);


                    positionHolders.add(financialSwapPositionHolder);
                }
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    public void generatePositionHoldersDaily() {

        if (context.getEndPositionDate().isBefore(context.getStartPositionDate()))
            return;

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            FinancialSwapPositionHolder financialSwapPositionHolder = new FinancialSwapPositionHolder(getFinancialSwapDealSummary());

            setBasePositionHolderAttributes(
                    financialSwapPositionHolder,
                    currentDate);
            financialSwapPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.DAILY);

            financialSwapPositionHolder.setHourSlotsForPowerProfile(calculate24HourSlots());
            determinePositionQuantity(financialSwapPositionHolder);

            generateCostPositionHolders(financialSwapPositionHolder);

            // if deal price valuation is fixed or INDEX Plus
            if (getFinancialSwapDealSummary().getDetail().getPaysValuationCode() == ValuationCode.FIXED ||
                    getFinancialSwapDealSummary().getDetail().getPaysValuationCode() == ValuationCode.INDEX_PLUS) {

                determineFixedPrice(financialSwapPositionHolder);

                determineFixedPriceRiskFactors(financialSwapPositionHolder);
            }

            if (getFinancialSwapDealSummary().getDetail().getPaysValuationCode() == ValuationCode.INDEX ||
                    getFinancialSwapDealSummary().getDetail().getPaysValuationCode() == ValuationCode.INDEX_PLUS) {
                determinePaysPriceRiskFactors(financialSwapPositionHolder);
            }

            // Market
            determineReceivesPriceRiskFactors(financialSwapPositionHolder);


            positionHolders.add(financialSwapPositionHolder);
            currentDate = currentDate.plusDays(1);
        }
    }



    private void determinePaysPriceRiskFactors(FinancialSwapPositionHolder financialSwapPositionHolder) {


        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(getFinancialSwapDealSummary().getPaysIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            financialSwapPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
            generateHourlyPositionHolder(
                    financialSwapPositionHolder,
                    PriceTypeCode.PAYS_PRICE,
                    getFinancialSwapDealSummary().getPaysIndexId());
        } else {
            financialSwapPositionHolder.setPaysPriceRiskFactorHolder(
                    riskFactorManager.determinePriceRiskFactor(
                            getFinancialSwapDealSummary().getPaysIndexId(),
                            financialSwapPositionHolder.getDetail().getStartDate()));
        }

        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            financialSwapPositionHolder.setPaysFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            financialSwapPositionHolder.getDetail().getStartDate()));
        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(getFinancialSwapDealSummary().getPaysIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            financialSwapPositionHolder.setBasisPaysPriceHolders(
                    determineBasisPriceRiskFactors(
                            financialSwapPositionHolder.getDetail().getStartDate(),
                            getFinancialSwapDealSummary().getPaysIndexId()));
        }
    }

    private void determineReceivesPriceRiskFactors(FinancialSwapPositionHolder financialSwapPositionHolder) {


        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(getFinancialSwapDealSummary().getReceivesIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            financialSwapPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
            generateHourlyPositionHolder(
                    financialSwapPositionHolder,
                    PriceTypeCode.RECEIVES_PRICE,
                    getFinancialSwapDealSummary().getReceivesIndexId());
        } else {

            PriceRiskFactorHolder receivesRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                    getFinancialSwapDealSummary().getReceivesIndexId(),
                    financialSwapPositionHolder.getDetail().getStartDate());

            financialSwapPositionHolder.setReceivesPriceRiskFactorHolder(receivesRiskFactorHolder);
        }


        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            financialSwapPositionHolder.setReceivesFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            financialSwapPositionHolder.getDetail().getStartDate()));

        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(
                        getFinancialSwapDealSummary().getReceivesIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            financialSwapPositionHolder.setBasisReceivesHolders(
                    determineBasisPriceRiskFactors(
                        financialSwapPositionHolder.getDetail().getStartDate(),
                            getFinancialSwapDealSummary().getReceivesIndexId()));
        }
    }

    @Override
    public PositionGenerationResult generatePositionSnapshots() {

        PositionGenerationResult generationResult = new PositionGenerationResult();

        for (BasePositionHolder positionHolder : positionHolders) {
            FinancialSwapPositionHolder financialSwapPositionHolder = (FinancialSwapPositionHolder) positionHolder;
            FinancialSwapPositionSnapshot positionSnapshot = new FinancialSwapPositionSnapshot();

            positionSnapshot.setDealId(new EntityId(positionHolder.getDealSummary().getId()));
            positionSnapshot.getDetail().copyFrom(positionHolder.getDetail());
            positionSnapshot.getSettlementDetail().copyFrom(positionHolder.getSettlementDetail());
            positionSnapshot.getDetail().setErrorCode("0");


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
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(positionSnapshot.getDetail().getCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setUnitOfMeasure(positionSnapshot.getDetail().getVolumeUnitOfMeasure());
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
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(getFinancialSwapDealSummary().getDealDetail().getFixedPriceCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setUnitOfMeasure(getFinancialSwapDealSummary().getDealDetail().getFixedPriceUnitOfMeasureCode());
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

            // Receives Price
            if (financialSwapPositionHolder.getReceivesPriceRiskFactorHolder() != null)
                positionSnapshot.setReceivesPriceRiskFactorId(financialSwapPositionHolder.getReceivesPriceRiskFactorHolder().getRiskFactor().getEntityId());

            if (financialSwapPositionHolder.getReceivesFxHolder() != null) {
                positionSnapshot.setReceivesFxRiskFactorId(
                        financialSwapPositionHolder.getReceivesFxHolder().getRiskFactor().getEntityId());
            }

            for (PriceRiskFactorHolder factorHolder : financialSwapPositionHolder.getBasisReceivesHolders()) {
                PositionRiskFactorMappingSnapshot mapping = new PositionRiskFactorMappingSnapshot();
                mapping.getDetail().setPriceTypeCode(PriceTypeCode.RECEIVES_PRICE);
                mapping.setPriceRiskFactorId(factorHolder.getRiskFactor().getEntityId());

                if (factorHolder.getFxRiskFactorHolder() != null)
                    mapping.setFxRiskFactorId(factorHolder.getFxRiskFactorHolder().getRiskFactor().getEntityId());
                positionSnapshot.addRiskFactorMappingSnapshot(mapping);
            }

            // Pays Price

            if (financialSwapPositionHolder.getFixedPriceFxHolder() != null) {
                positionSnapshot.setFixedPriceFxRiskFactorId(financialSwapPositionHolder.getFixedPriceFxHolder().getRiskFactor().getEntityId());
            }

            if (financialSwapPositionHolder.getPaysPriceRiskFactorHolder() != null) {
                positionSnapshot.setPaysPriceRiskFactorId(financialSwapPositionHolder.getPaysPriceRiskFactorHolder().getRiskFactor().getEntityId());
            }

            if (financialSwapPositionHolder.getPaysFxHolder() != null) {
                positionSnapshot.setPaysFxRiskFactorId(financialSwapPositionHolder.getPaysFxHolder().getRiskFactor().getEntityId());
            }


            for (PriceRiskFactorHolder factorHolder : financialSwapPositionHolder.getBasisPaysPriceHolders()) {
                PositionRiskFactorMappingSnapshot mapping = new PositionRiskFactorMappingSnapshot();
                mapping.getDetail().setPriceTypeCode(PriceTypeCode.PAYS_PRICE);

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
