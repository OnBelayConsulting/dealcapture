package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.PriceIndexPositionDateContainer;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.FrequencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhysicalDealPositionGenerator extends BaseDealPositionGenerator {


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

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) dealSummary;

        if (context.getEndPositionDate().isBefore(context.getStartPositionDate()))
            return;

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            PhysicalPositionHolder physicalPositionHolder = new PhysicalPositionHolder(physicalDealSummary);

            setBasePositionHolderAttributes(
                    physicalPositionHolder,
                    currentDate);

            physicalPositionHolder.getDetail().setDealMarketValuationCode(physicalDealSummary.getMarketValuationCode());
            physicalPositionHolder.getDetail().setDealPriceValuationCode(physicalDealSummary.getDealPriceValuationCode());


            if (dealSummary.hasPowerProfile()) {
                if (powerProfilePositionMap.isEmpty())
                    throw new OBRuntimeException(PositionErrorCode.MISSING_POWER_PROFILE_POSITIONS.getCode());

                determinePositionQuantityFromPowerProfile(physicalPositionHolder);
            } else {
                determinePositionQuantity(physicalPositionHolder);
            }

            generateCostPositionHolders(physicalPositionHolder);

            // if deal price valuation is fixed or INDEX Plus
            if (physicalPositionHolder.getDetail().getDealPriceValuationCode() == ValuationCode.FIXED ||
                    physicalPositionHolder.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {

                determineFixedPrice(physicalPositionHolder);

                determineFixedPriceRiskFactors(physicalPositionHolder);
            }

            if (physicalPositionHolder.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX ||
                    physicalPositionHolder.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
                determineDealPriceRiskFactors(physicalPositionHolder);
            }

            // Market
            physicalPositionHolder.getDetail().setDealMarketValuationCode(physicalDealSummary.getMarketValuationCode());
            if (dealSummary.hasPowerProfile())
                generateDealHourlyPositionHoldersFromPowerProfile(
                        physicalPositionHolder,
                        PriceTypeCode.MARKET_PRICE);
            else
                determineMarketPriceRiskFactors(physicalPositionHolder);


            positionHolders.add(physicalPositionHolder);
            currentDate = currentDate.plusDays(1);
        }
    }

    private void
    determineFixedPrice(PhysicalPositionHolder physicalPositionHolder) {
        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) physicalPositionHolder.getDealSummary();

        LocalDate currentDate = physicalPositionHolder.getDealPositionDetail().getStartDate();

        BigDecimal fixedPriceValue;
        if (hasDealDayByMonthPrices(currentDate))
            fixedPriceValue = getDayPrice(currentDate);
        else
            fixedPriceValue = physicalDealSummary.getFixedPriceValue();

        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                context.getUnitOfMeasureCode());

        Amount totalAmount = new Amount(
                BigDecimal.ZERO,
                physicalDealSummary.getFixedPriceCurrencyCode());

        Price totalPrice = new Price(
                BigDecimal.ZERO,
                physicalDealSummary.getFixedPriceCurrencyCode(),
                context.getUnitOfMeasureCode());

        boolean needWeightedPrice = physicalPositionHolder.getDealHourByDayQuantity().isNotEmpty();

        boolean calculateFixedPrice = false;
        if (hasDealHourByDayPrices(currentDate)) {
            calculateFixedPrice = true;
            for (int i=1; i < 25; i++) {
                BigDecimal priceValue = getHourPrice(currentDate, i);
                if (priceValue != null) {
                    physicalPositionHolder.getDealHourByDayPrice().setHourValue(
                            i,
                            priceValue);
                } else {
                    physicalPositionHolder.getDealHourByDayPrice().setHourValue(
                            i,
                            fixedPriceValue);
                }
                Price hourlyPrice = new Price(
                        physicalPositionHolder.getDealHourByDayPrice().getHourValue(i),
                        physicalDealSummary.getFixedPriceCurrencyCode(),
                        physicalDealSummary.getFixedPriceUnitOfMeasureCode());

                if (needWeightedPrice) {
                    Quantity hourlyQuantity = new Quantity(
                            physicalPositionHolder.getDealHourByDayQuantity().getHourValue(i),
                            context.getUnitOfMeasureCode());

                    if (physicalDealSummary.getFixedPriceUnitOfMeasureCode() != context.getUnitOfMeasureCode()) {
                        Conversion conversion = UnitOfMeasureConverter.findConversion(
                                context.getUnitOfMeasureCode(),
                                physicalDealSummary.getFixedPriceUnitOfMeasureCode());
                        hourlyPrice = hourlyPrice.apply(conversion);
                    }
                    totalQuantity = totalQuantity.add(hourlyQuantity);


                    Amount hourlyAmount = hourlyQuantity.multiply(hourlyPrice);
                    totalAmount = totalAmount.add(hourlyAmount);
                } else {
                    totalPrice = totalPrice.add(hourlyPrice);
                }
            }

        }

        if (calculateFixedPrice) {

            if (needWeightedPrice) {
                Price weightedAvgPrice = totalAmount.divide(totalQuantity);
                physicalPositionHolder.getDetail().setFixedPriceValue(weightedAvgPrice.roundPrice().getValue());
            } else {
                Price avgPrice = totalPrice.divide(BigDecimal.valueOf(24));
                physicalPositionHolder.getDetail().setFixedPriceValue(avgPrice.roundPrice().getValue());
            }

        } else {
            physicalPositionHolder.getDetail().setFixedPriceValue(fixedPriceValue);
        }

        physicalPositionHolder.getDetail().setFixedPriceUnitOfMeasure(context.getUnitOfMeasureCode());
        physicalPositionHolder.getDetail().setFixedPriceCurrencyCode(physicalDealSummary.getFixedPriceCurrencyCode());
    }

    private void determineFixedPriceRiskFactors(PhysicalPositionHolder physicalPositionHolder) {

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) physicalPositionHolder.getDealSummary();

        if (physicalDealSummary.getFixedPriceCurrencyCode() != context.getCurrencyCode()) {
            physicalPositionHolder.setFixedPriceFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            physicalDealSummary.getFixedPriceCurrencyCode(),
                            context.getCurrencyCode(),
                            physicalPositionHolder.getDealPositionDetail().getStartDate()));
        }
    }


    private void determineDealPriceRiskFactors(PhysicalPositionHolder physicalPositionHolder) {

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) physicalPositionHolder.getDealSummary();

        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(physicalDealSummary.getDealPriceIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            generateHourlyPositionHolder(
                    physicalPositionHolder,
                    PriceTypeCode.DEAL_PRICE,
                    physicalDealSummary.getDealPriceIndexId());
        } else {
            physicalPositionHolder.setDealPriceRiskFactorHolder(
                    riskFactorManager.determinePriceRiskFactor(
                            physicalDealSummary.getDealPriceIndexId(),
                            physicalPositionHolder.getDealPositionDetail().getStartDate()));
        }

        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            physicalPositionHolder.setDealPriceFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            physicalPositionHolder.getDealPositionDetail().getStartDate()));
        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(physicalDealSummary.getDealPriceIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            physicalPositionHolder.setBasisDealPriceHolders(
                    determineBasisPriceRiskFactors(
                            physicalPositionHolder.getDealPositionDetail().getStartDate(),
                            physicalDealSummary.getDealPriceIndexId()));
        }
    }

    private void generateHourlyPositionHolder(
            BasePositionHolder basePositionHolder,
            PriceTypeCode priceTypeCode,
            Integer priceIndexId) {

        DealHourlyPositionHolder dealHourlyPositionHolder = new DealHourlyPositionHolder();
        dealHourlyPositionHolder.setDealId(new EntityId(dealSummary.getDealId()));
        dealHourlyPositionHolder.setPriceIndexId(new EntityId(priceIndexId));
        dealHourlyPositionHolder.getDetail().setStartDate(basePositionHolder.getDealPositionDetail().getStartDate());
        dealHourlyPositionHolder.getDetail().setEndDate(basePositionHolder.getDealPositionDetail().getEndDate());
        dealHourlyPositionHolder.getDetail().setCreatedDateTime(basePositionHolder.getDealPositionDetail().getCreatedDateTime());
        dealHourlyPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
        dealHourlyPositionHolder.getDetail().setPriceTypeCode(priceTypeCode);
        dealHourlyPositionHolder.getDetail().setCurrencyCode(context.getCurrencyCode());
        dealHourlyPositionHolder.getDetail().setUnitOfMeasure(context.getUnitOfMeasureCode());
        dealHourlyPositionHolder.getDetail().setIsSettlementPosition(basePositionHolder.getSettlementDetail().getIsSettlementPosition());

        for (int i=1; i < 25; i++) {
            PriceRiskFactorHolder holder = riskFactorManager.determinePriceRiskFactor(
                    priceIndexId,
                    basePositionHolder.getDealPositionDetail().getStartDate(),
                    i);
            dealHourlyPositionHolder.getPriceHourHolderMap().setHourPriceHolder(
                    i,
                    holder);
        }
        basePositionHolder.getHourlyPositionHolders().add(dealHourlyPositionHolder);
    }


    private List<PriceRiskFactorHolder> determineBasisPriceRiskFactors(
            LocalDate  factorDate,
            Integer priceIndexId) {

        ArrayList<PriceRiskFactorHolder> priceRiskFactorHolders = new ArrayList<>();

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(priceIndexId);

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            PriceIndexPositionDateContainer currentContainer = priceIndexContainer;

            while (currentContainer.isBasis()) {
                PriceIndexPositionDateContainer nextContainer = currentContainer.getBasisToHubContainer();

                PriceRiskFactorHolder nextPriceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                        nextContainer,
                        factorDate);

                if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != context.getCurrencyCode()) {
                    nextPriceRiskFactorHolder.setFxRiskFactorHolder(
                            riskFactorManager.determineFxRiskFactor(
                                    nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                                    context.getCurrencyCode(),
                                    factorDate));
                }

                priceRiskFactorHolders.add(nextPriceRiskFactorHolder);
                currentContainer = nextContainer;

            }
        }
        return priceRiskFactorHolders;
    }

    private void determineMarketPriceRiskFactors(PhysicalPositionHolder physicalPositionHolder) {

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) physicalPositionHolder.getDealSummary();

        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(physicalDealSummary.getMarketIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            generateHourlyPositionHolder(
                    physicalPositionHolder,
                    PriceTypeCode.MARKET_PRICE,
                    physicalDealSummary.getMarketIndexId());
        } else {

            PriceRiskFactorHolder marketRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                    physicalDealSummary.getMarketIndexId(),
                    physicalPositionHolder.getDealPositionDetail().getStartDate());

            physicalPositionHolder.setMarketRiskFactorHolder(marketRiskFactorHolder);
        }


        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            physicalPositionHolder.setMarketFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            physicalPositionHolder.getDealPositionDetail().getStartDate()));

        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(
                        physicalDealSummary.getMarketIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            physicalPositionHolder.setBasisMarketHolders(
                    determineBasisPriceRiskFactors(
                        physicalPositionHolder.getDealPositionDetail().getStartDate(),
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

            positionSnapshot.setDealId(new EntityId(positionHolder.getDealSummary().getDealId()));
            positionSnapshot.getDealPositionDetail().copyFrom(positionHolder.getDealPositionDetail());
            positionSnapshot.getSettlementDetail().copyFrom(positionHolder.getSettlementDetail());
            positionSnapshot.getDetail().copyFrom(physicalPositionHolder.getDetail());
            positionSnapshot.getDealPositionDetail().setErrorCode("0");


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
                hourlyPositionSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
                hourlyPositionSnapshot.getDetail().setPriceTypeCode(PriceTypeCode.FIXED_QUANTITY);
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getDealPositionDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getDealPositionDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(positionSnapshot.getDealPositionDetail().getCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setUnitOfMeasure(positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
                hourlyPositionSnapshot.getDetail().setCreatedDateTime(context.getCreatedDateTime());
                hourlyPositionSnapshot.setDealId(new EntityId(dealSummary.getDealId()));
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
                hourlyPositionSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
                hourlyPositionSnapshot.getDetail().setPriceTypeCode(PriceTypeCode.FIXED_PRICE);
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getDealPositionDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getDealPositionDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(physicalDealSummary.getFixedPriceCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setUnitOfMeasure(physicalDealSummary.getFixedPriceUnitOfMeasureCode());
                hourlyPositionSnapshot.getDetail().setCreatedDateTime(context.getCreatedDateTime());
                hourlyPositionSnapshot.setDealId(new EntityId(dealSummary.getDealId()));
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
