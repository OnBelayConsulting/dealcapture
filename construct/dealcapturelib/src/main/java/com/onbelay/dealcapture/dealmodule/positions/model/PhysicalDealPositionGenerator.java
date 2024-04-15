package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
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
import java.time.LocalDateTime;
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
    public void generatePositionHolders(EvaluationContext contextIn) {
        EvaluationContext context = modifyEvaluationContextForDeal(contextIn);

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) dealSummary;

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            PhysicalPositionHolder physicalPositionHolder = new PhysicalPositionHolder(new PhysicalPositionSnapshot());
            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();

            positionSnapshot.setDealTypeValue(DealTypeCode.PHYSICAL_DEAL.getCode());
            positionSnapshot.getDealPositionDetail().setErrorCode("0");

            setBasePositionHolderAttributes(
                    context,
                    physicalPositionHolder,
                    currentDate);


            // Deal Price
            positionSnapshot.getDetail().setDealPriceValuationCode(physicalDealSummary.getDealPriceValuationCode());

            determinePositionQuantity(
                    context,
                    physicalPositionHolder,
                    physicalDealSummary);

            generateCostPositionHolders(
                    context,
                    physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getVolumeQuantityValue(),
                    physicalPositionHolder.getDealPositionSnapshot().getSettlementDetail().getIsSettlementPosition(),
                    physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate());

            // if deal price valuation is fixed or INDEX Plus
            if (positionSnapshot.getDetail().getDealPriceValuationCode() == ValuationCode.FIXED ||
                    positionSnapshot.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {

                determineFixedPrice(
                        context,
                        physicalPositionHolder,
                        physicalDealSummary);

                determineFixedPriceRiskFactors(
                        context,
                        physicalPositionHolder,
                        physicalDealSummary);
            }

            if (positionSnapshot.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX ||
                    positionSnapshot.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
                determineDealPriceRiskFactors(
                        context,
                        physicalPositionHolder,
                        physicalDealSummary);
            }

            // Market
            positionSnapshot.getDetail().setDealMarketValuationCode(physicalDealSummary.getMarketValuationCode());
            determineMarketPriceRiskFactors(
                    context,
                    physicalPositionHolder,
                    physicalDealSummary);


                positionHolders.add(physicalPositionHolder);
                currentDate = currentDate.plusDays(1);
        }
    }

    private void determinePositionQuantity(
            EvaluationContext context,
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary) {

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();
        LocalDate currentDate = positionSnapshot.getDealPositionDetail().getStartDate();

        Quantity defaultDailyQuantity;
        Quantity defaultHourlyQuantity;

        if (dealSummary.getVolumeFrequencyCode() == FrequencyCode.HOURLY) {
            defaultHourlyQuantity = new Quantity(
                    dealSummary.getVolumeQuantity(),
                    dealSummary.getVolumeUnitOfMeasureCode());
            defaultDailyQuantity = defaultHourlyQuantity.multiply(24);
        } else {
            defaultDailyQuantity = new Quantity(
                    dealSummary.getVolumeQuantity(),
                    dealSummary.getVolumeUnitOfMeasureCode());
            defaultHourlyQuantity = defaultDailyQuantity.divide(24);
        }

        Quantity dailyQuantity = null;
        if (hasDealDaysContainerForQuantity(currentDate))
            dailyQuantity = new Quantity(
                    getDayQuantity(currentDate),
                    dealSummary.getVolumeUnitOfMeasureCode());

        if (dailyQuantity == null)
            dailyQuantity = defaultDailyQuantity;


        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                dealSummary.getVolumeUnitOfMeasureCode());

        boolean calculateDailyQuantity = false;
        if (hasDealHourByDayQuantity(currentDate)) {
            calculateDailyQuantity = true;
            for (int i=1; i < 25; i++) {
                BigDecimal quantityValue = getHourQuantity(currentDate, i);
                if (quantityValue != null) {

                    physicalPositionHolder.getDealHourByDayQuantity().setHourValue(
                            i,
                            quantityValue);
                } else {
                    physicalPositionHolder.getDealHourByDayQuantity().setHourValue(
                            i,
                            defaultHourlyQuantity.getValue());
                }
                Quantity hourlyQuantity = new Quantity(
                        physicalPositionHolder.getDealHourByDayQuantity().getHourValue(i),
                        dealSummary.getVolumeUnitOfMeasureCode());

                if (physicalDealSummary.getFixedPriceUnitOfMeasureCode() != physicalDealSummary.getVolumeUnitOfMeasureCode()) {
                    Conversion conversion = UnitOfMeasureConverter.findConversion(
                            physicalDealSummary.getFixedPriceUnitOfMeasureCode(),
                            physicalDealSummary.getVolumeUnitOfMeasureCode());
                    hourlyQuantity = hourlyQuantity.apply(conversion) ;
                }
                totalQuantity = totalQuantity.add(hourlyQuantity);
            }
        }


        Quantity unconvertedDailyQuantity;
        if (calculateDailyQuantity)
            unconvertedDailyQuantity = totalQuantity;
        else
            unconvertedDailyQuantity = dailyQuantity;

        if (context.getUnitOfMeasureCode() != dealSummary.getVolumeUnitOfMeasureCode()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    context.getUnitOfMeasureCode(),
                    dealSummary.getVolumeUnitOfMeasureCode());

            unconvertedDailyQuantity = unconvertedDailyQuantity.apply(conversion);
        }

        positionSnapshot.getDealPositionDetail().setVolumeQuantityValue(unconvertedDailyQuantity.round().getValue());
    }



    private void determineFixedPrice(
            EvaluationContext context,
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary) {

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();
        LocalDate currentDate = positionSnapshot.getDealPositionDetail().getStartDate();

        BigDecimal fixedPriceValue;
        if (hasDealDaysContainerForPrice(currentDate))
            fixedPriceValue = getDayPrice(currentDate);
        else
            fixedPriceValue = physicalDealSummary.getFixedPriceValue();

        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                dealSummary.getVolumeUnitOfMeasureCode());

        Amount totalAmount = new Amount(
                BigDecimal.ZERO,
                physicalDealSummary.getFixedPriceCurrencyCode());


        boolean calculateFixedPrice = false;
        if (hasDealHourByDayPrice(currentDate)) {
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
                Quantity hourlyQuantity = new Quantity(
                        physicalPositionHolder.getDealHourByDayQuantity().getHourValue(i),
                        dealSummary.getVolumeUnitOfMeasureCode());

                if (physicalDealSummary.getFixedPriceUnitOfMeasureCode() != physicalDealSummary.getVolumeUnitOfMeasureCode()) {
                    Conversion conversion = UnitOfMeasureConverter.findConversion(
                            physicalDealSummary.getFixedPriceUnitOfMeasureCode(),
                            physicalDealSummary.getVolumeUnitOfMeasureCode());
                    hourlyQuantity = hourlyQuantity.apply(conversion) ;
                }
                totalQuantity = totalQuantity.add(hourlyQuantity);

                Price hourlyPrice = new Price(
                        physicalPositionHolder.getDealHourByDayPrice().getHourValue(i),
                        physicalDealSummary.getFixedPriceCurrencyCode(),
                        physicalDealSummary.getFixedPriceUnitOfMeasureCode());

                Amount hourlyAmount = hourlyQuantity.multiply(hourlyPrice);
                totalAmount = totalAmount.add(hourlyAmount);
            }

        }

        if (calculateFixedPrice && totalQuantity.isNotZero()) {
            Price wieghtedAvgPrice = totalAmount.divide(totalQuantity);
            positionSnapshot.getDetail().setFixedPriceValue(wieghtedAvgPrice.roundPrice().getValue());
        } else {
            positionSnapshot.getDetail().setFixedPriceValue(fixedPriceValue);
        }

        positionSnapshot.getDetail().setFixedPriceUnitOfMeasure(
                physicalDealSummary.getFixedPriceUnitOfMeasureCode());
        positionSnapshot.getDetail().setFixedPriceCurrencyCode(
                physicalDealSummary.getFixedPriceCurrencyCode());
    }

    private void determineFixedPriceRiskFactors(
            EvaluationContext context,
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary) {


        if (physicalDealSummary.getFixedPriceCurrencyCode() != context.getCurrencyCode()) {
            physicalPositionHolder.setFixedPriceFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            physicalDealSummary.getFixedPriceCurrencyCode(),
                            context.getCurrencyCode(),
                            physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate()));
        }
    }


    private void determineDealPriceRiskFactors(
            EvaluationContext context,
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary) {

        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(physicalDealSummary.getDealPriceIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            determineHourlyPriceRiskFactors(
                    physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate(),
                    physicalPositionHolder.getDealPriceHourHolderMap(),
                    physicalDealSummary.getDealPriceIndexId());
        } else {
            physicalPositionHolder.setDealPriceRiskFactorHolder(
                    riskFactorManager.determinePriceRiskFactor(
                            physicalDealSummary.getDealPriceIndexId(),
                            physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate()));
        }

        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            physicalPositionHolder.setDealPriceFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate()));
        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(physicalDealSummary.getDealPriceIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            physicalPositionHolder.setBasisDealPriceHolders(
                    determineBasisPriceRiskFactors(
                            context,
                            physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate(),
                            physicalDealSummary.getDealPriceIndexId()));
        }
    }

    private void determineHourlyPriceRiskFactors(
            LocalDate  factorDate,
            ProfilePriceHourHolderMap profilePriceHourHolderMap,
            Integer priceIndexId) {

        for (int i=1; i < 25; i++) {
            PriceRiskFactorHolder holder = riskFactorManager.determinePriceRiskFactor(
                    priceIndexId,
                    factorDate,
                    i);
            profilePriceHourHolderMap.setHourPriceHolder(
                    i,
                    holder);
        }

    }


    private List<PriceRiskFactorHolder> determineBasisPriceRiskFactors(
            EvaluationContext context,
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

    private void determineMarketPriceRiskFactors(
            EvaluationContext context,
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary) {


        PriceIndexSnapshot priceIndexSnapshot = riskFactorManager.findPriceIndex(physicalDealSummary.getMarketIndexId());

        if (priceIndexSnapshot.getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
            determineHourlyPriceRiskFactors(
                    physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate(),
                    physicalPositionHolder.getMarketPriceHourHolderMap(),
                    physicalDealSummary.getMarketIndexId());
        } else {

            PriceRiskFactorHolder marketRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                    physicalDealSummary.getMarketIndexId(),
                    physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate());

            physicalPositionHolder.setMarketRiskFactorHolder(marketRiskFactorHolder);
        }


        if (priceIndexSnapshot.getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            physicalPositionHolder.setMarketFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            priceIndexSnapshot.getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
                            physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate()));

        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(
                        physicalDealSummary.getMarketIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            physicalPositionHolder.setBasisMarketHolders(
                    determineBasisPriceRiskFactors(
                        context,
                        physicalPositionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate(),
                        physicalDealSummary.getMarketIndexId()));
        }
    }

    public void generateDealPositionSnapshots(LocalDateTime createdDateTime) {


        for (BasePositionHolder holder : positionHolders) {
            PhysicalPositionHolder physicalPositionHolder = (PhysicalPositionHolder) holder;
            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) holder.getDealPositionSnapshot();

            if (holder.getDealHourByDayQuantity().isNotEmpty()) {
                positionSnapshot.getDealPositionDetail().setFrequencyCode(FrequencyCode.HOURLY);
                DealHourlyPositionSnapshot hourlyPositionSnapshot = new DealHourlyPositionSnapshot();
                hourlyPositionSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
                hourlyPositionSnapshot.getDetail().setPriceTypeCode(PriceTypeCode.FIXED_QUANTITY);
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getDealPositionDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getDealPositionDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(positionSnapshot.getDealPositionDetail().getCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setVolumeUnitOfMeasure(positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
                hourlyPositionSnapshot.getDetail().setCreatedDateTime(createdDateTime);
                hourlyPositionSnapshot.setDealId(dealSummary.getDealId());
                hourlyPositionSnapshot.getDetail().setErrorCode("0");
                dealHourlyPositionSnapshots.add(hourlyPositionSnapshot);

                for (int i=1; i < 25; i++) {
                    if (holder.getDealHourByDayQuantity().getHourValue(i) != null) {
                        hourlyPositionSnapshot.getHourFixedValueDetail().setHourFixedValue(
                                i,
                                holder.getDealHourByDayQuantity().getHourValue(i));
                    }
                }
            }

            if (holder.getDealHourByDayPrice().isNotEmpty()) {
                positionSnapshot.getDealPositionDetail().setFrequencyCode(FrequencyCode.HOURLY);
                DealHourlyPositionSnapshot hourlyPositionSnapshot = new DealHourlyPositionSnapshot();
                hourlyPositionSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
                hourlyPositionSnapshot.getDetail().setPriceTypeCode(PriceTypeCode.FIXED_PRICE);
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getDealPositionDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getDealPositionDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(positionSnapshot.getDealPositionDetail().getCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setVolumeUnitOfMeasure(positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
                hourlyPositionSnapshot.getDetail().setCreatedDateTime(createdDateTime);
                hourlyPositionSnapshot.setDealId(dealSummary.getDealId());
                hourlyPositionSnapshot.getDetail().setErrorCode("0");
                dealHourlyPositionSnapshots.add(hourlyPositionSnapshot);

                for (int i=1; i < 25; i++) {
                    if (holder.getDealHourByDayPrice().getHourValue(i) != null) {
                        hourlyPositionSnapshot.getHourFixedValueDetail().setHourFixedValue(
                                i,
                                holder.getDealHourByDayQuantity().getHourValue(i));
                    }
                }
            }

            // if Hourly then create an hourly position for deal price
            if (holder.getDealPriceHourHolderMap().isNotEmpty()) {
                positionSnapshot.getDealPositionDetail().setFrequencyCode(FrequencyCode.HOURLY);
                DealHourlyPositionSnapshot hourlyPositionSnapshot = new DealHourlyPositionSnapshot();
                hourlyPositionSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
                hourlyPositionSnapshot.getDetail().setPriceTypeCode(PriceTypeCode.DEAL_PRICE);
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getDealPositionDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getDealPositionDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(positionSnapshot.getDealPositionDetail().getCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setVolumeUnitOfMeasure(positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
                hourlyPositionSnapshot.getDetail().setCreatedDateTime(createdDateTime);
                hourlyPositionSnapshot.setDealId(dealSummary.getDealId());
                hourlyPositionSnapshot.getDetail().setErrorCode("0");
                dealHourlyPositionSnapshots.add(hourlyPositionSnapshot);

                for (int i=1; i < 25; i++) {
                    if (holder.getDealPriceHourHolderMap().getHourPriceHolder(i) != null) {
                        PriceRiskFactorHolder riskFactorHolder = holder.getDealPriceHourHolderMap().getHourPriceHolder(i);
                        hourlyPositionSnapshot.getHourPriceRiskFactorIdMap().setHourPriceRiskFactorId(
                                i,
                                riskFactorHolder.getRiskFactor().getEntityId().getId());
                    }
                }

            }

            // if Hourly then create an hourly position for market price
            if (holder.getMarketPriceHourHolderMap().isNotEmpty()) {
                positionSnapshot.getDealPositionDetail().setFrequencyCode(FrequencyCode.HOURLY);
                DealHourlyPositionSnapshot hourlyPositionSnapshot = new DealHourlyPositionSnapshot();
                hourlyPositionSnapshot.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
                hourlyPositionSnapshot.getDetail().setPriceTypeCode(PriceTypeCode.MARKET_PRICE);
                hourlyPositionSnapshot.getDetail().setStartDate(positionSnapshot.getDealPositionDetail().getStartDate());
                hourlyPositionSnapshot.getDetail().setEndDate(positionSnapshot.getDealPositionDetail().getEndDate());
                hourlyPositionSnapshot.getDetail().setCurrencyCode(positionSnapshot.getDealPositionDetail().getCurrencyCode());
                hourlyPositionSnapshot.getDetail().setIsSettlementPosition(positionSnapshot.getSettlementDetail().getIsSettlementPosition());
                hourlyPositionSnapshot.getDetail().setVolumeUnitOfMeasure(positionSnapshot.getDealPositionDetail().getVolumeUnitOfMeasure());
                hourlyPositionSnapshot.getDetail().setCreatedDateTime(createdDateTime);
                hourlyPositionSnapshot.setDealId(dealSummary.getDealId());
                hourlyPositionSnapshot.getDetail().setErrorCode("0");
                dealHourlyPositionSnapshots.add(hourlyPositionSnapshot);

                for (int i=1; i < 25; i++) {
                    if (holder.getMarketPriceHourHolderMap().getHourPriceHolder(i) != null) {
                        PriceRiskFactorHolder riskFactorHolder = holder.getMarketPriceHourHolderMap().getHourPriceHolder(i);
                        hourlyPositionSnapshot.getHourPriceRiskFactorIdMap().setHourPriceRiskFactorId(
                                i,
                                riskFactorHolder.getRiskFactor().getEntityId().getId());
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

            dealPositionSnapshots.add(positionSnapshot);
        }
    }
}
