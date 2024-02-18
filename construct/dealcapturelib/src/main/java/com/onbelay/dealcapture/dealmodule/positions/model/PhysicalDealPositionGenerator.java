package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.riskfactor.components.PriceIndexPositionDateContainer;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.math.MathContext;
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
    public void generatePositionHolders(EvaluationContext context) {
        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) dealSummary;

        final LocalDate startDate;
        final LocalDate endDate;
        if (context.getStartPositionDate() != null) {
            if (context.getStartPositionDate().isAfter(physicalDealSummary.getStartDate()))
                startDate = context.getStartPositionDate();
            else
                startDate = physicalDealSummary.getStartDate();
        } else {
            startDate = physicalDealSummary.getStartDate();
        }
        if (context.getEndPositionDate() != null) {
            if (context.getEndPositionDate().isBefore(physicalDealSummary.getEndDate()))
                endDate = context.getEndPositionDate();
            else
                endDate = physicalDealSummary.getEndDate();
        } else {
            endDate = physicalDealSummary.getEndDate();
        }

        LocalDate currentDate = startDate;
        while (currentDate.isAfter(endDate) == false) {
            PhysicalPositionHolder physicalPositionHolder = new PhysicalPositionHolder(new PhysicalPositionSnapshot());
            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();
            positionSnapshot.getDealPositionDetail().setFrequencyCode(FrequencyCode.DAILY);

            CurrencyCode targetCurrencyCode;
            if (context.getCurrencyCode() != null)
                targetCurrencyCode = context.getCurrencyCode();
            else
                targetCurrencyCode = physicalDealSummary.getReportingCurrencyCode();

            UnitOfMeasureCode targetUnitOfMeasureCode;
            if (context.getUnitOfMeasureCode() != null)
                targetUnitOfMeasureCode = context.getUnitOfMeasureCode();
            else
                targetUnitOfMeasureCode = physicalDealSummary.getVolumeUnitOfMeasureCode();

            if (targetUnitOfMeasureCode == physicalDealSummary.getVolumeUnitOfMeasureCode()) {
                positionSnapshot.getDealPositionDetail().setVolumeQuantityValue(physicalDealSummary.getVolumeQuantity());
            } else {
                BigDecimal quantity = physicalDealSummary.getVolumeQuantity();
                Conversion conversion = UnitOfMeasureConverter.findConversion(
                        targetUnitOfMeasureCode, 
                        physicalDealSummary.getVolumeUnitOfMeasureCode());
                
                quantity = quantity.multiply(conversion.getValue(), MathContext.DECIMAL128);
                positionSnapshot.getDealPositionDetail().setVolumeQuantityValue(quantity);
            }
            positionSnapshot.getDealPositionDetail().setVolumeUnitOfMeasure(targetUnitOfMeasureCode);

            positionSnapshot.getDealPositionDetail().setStartDate(currentDate);
            positionSnapshot.getDealPositionDetail().setEndDate(currentDate);
            positionSnapshot.getDealPositionDetail().setCreateUpdateDateTime(context.getObservedDateTime());

            positionSnapshot.getDealPositionDetail().setCurrencyCode(targetCurrencyCode);

            // Deal Price
            positionSnapshot.getDetail().setDealPriceValuationCode(physicalDealSummary.getDealPriceValuationCode());

            determineDealPriceRiskFactors(
                    physicalPositionHolder,
                    physicalDealSummary,
                    targetUnitOfMeasureCode,
                    targetCurrencyCode,
                    currentDate);

            // Market
            positionSnapshot.getDetail().setDealMarketValuationCode(physicalDealSummary.getMarketValuationCode());
            determineMarketPriceRiskFactors(
                    physicalPositionHolder,
                    physicalDealSummary,
                    targetUnitOfMeasureCode,
                    targetCurrencyCode,
                    currentDate);


                positionHolders.add(physicalPositionHolder);
                currentDate = currentDate.plusDays(1);
        }
    }

    private void determineDealPriceRiskFactors(
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary,
            UnitOfMeasureCode targetUnitOfMeasureCode,
            CurrencyCode targetCurrencyCode,
            LocalDate currentDate) {

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();

        // if deal price valuation is fixed or INDEX Plus
        if (positionSnapshot.getDetail().getDealPriceValuationCode() != ValuationCode.INDEX) {

            // Set fixed deal price
            positionSnapshot.getDetail().setFixedPriceValue(
                    physicalDealSummary.getFixedPriceValue());
            positionSnapshot.getDetail().setDealPriceUnitOfMeasure(
                    physicalDealSummary.getFixedPriceUnitOfMeasureCode());
            positionSnapshot.getDetail().setDealPriceCurrencyCode(
                    physicalDealSummary.getFixedPriceCurrencyCode());


            if (physicalDealSummary.getFixedPriceCurrencyCode() != targetCurrencyCode) {
                physicalPositionHolder.setFixedDealPriceFxHolder(
                        riskFactorManager.determineFxRiskFactor(
                                physicalDealSummary.getFixedPriceCurrencyCode(),
                                targetCurrencyCode,
                                currentDate));
            }
        } else {   // Deal price is index based
            PriceRiskFactorHolder priceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                    physicalDealSummary.getDealPriceIndexId(),
                    currentDate);


            if (priceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode() != targetUnitOfMeasureCode) {
                Conversion conversion = UnitOfMeasureConverter.findConversion(
                        targetUnitOfMeasureCode,
                        priceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
                priceRiskFactorHolder.setConversion(conversion);
            }

            physicalPositionHolder.setDealPriceRiskFactorHolder(priceRiskFactorHolder);
            if (priceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != targetCurrencyCode) {
                priceRiskFactorHolder.setFxRiskFactorHolder(
                        riskFactorManager.determineFxRiskFactor(
                                priceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                                targetCurrencyCode,
                                currentDate));
            }


            PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                    .findPriceIndexContainer(
                            physicalDealSummary.getDealPriceIndexId());

            // Collect basis containers
            if (priceIndexContainer.isBasis()) {
                PriceIndexPositionDateContainer currentContainer = priceIndexContainer;
                while (currentContainer.isBasis()) {
                    PriceIndexPositionDateContainer nextContainer = currentContainer.getBasisToHubContainer();

                    PriceRiskFactorHolder nextPriceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                            nextContainer,
                            currentDate);

                    if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode() != targetUnitOfMeasureCode) {
                        Conversion conversion = UnitOfMeasureConverter.findConversion(
                                targetUnitOfMeasureCode,
                                nextPriceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
                        nextPriceRiskFactorHolder.setConversion(conversion);
                    }

                    if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != targetCurrencyCode) {
                        nextPriceRiskFactorHolder.setFxRiskFactorHolder(
                                riskFactorManager.determineFxRiskFactor(
                                        nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                                        targetCurrencyCode,
                                        currentDate));
                    }

                    physicalPositionHolder.addBasisToHubDealPriceRiskFactorHolder(nextPriceRiskFactorHolder);
                    currentContainer = nextContainer;
                }
            }
        }

    }

    private void determineMarketPriceRiskFactors(
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary,
            UnitOfMeasureCode targetUnitOfMeasureCode,
            CurrencyCode targetCurrencyCode,
            LocalDate currentDate) {

        PriceRiskFactorHolder marketRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                physicalDealSummary.getMarketIndexId(),
                currentDate);

        physicalPositionHolder.setMarketRiskFactorHolder(marketRiskFactorHolder);

        if (marketRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode() != targetUnitOfMeasureCode) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    targetUnitOfMeasureCode,
                    marketRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
            marketRiskFactorHolder.setConversion(conversion);
        }

        if (marketRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != targetCurrencyCode) {
            marketRiskFactorHolder.setFxRiskFactorHolder(
                    riskFactorManager.determineFxRiskFactor(
                            marketRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                            targetCurrencyCode,
                            currentDate));

        }

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(
                        physicalDealSummary.getMarketIndexId());

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            PriceIndexPositionDateContainer currentContainer = priceIndexContainer;
            while (currentContainer.isBasis()) {
                PriceIndexPositionDateContainer nextContainer = currentContainer.getBasisToHubContainer();

                PriceRiskFactorHolder nextPriceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                        nextContainer,
                        currentDate);

                if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode() != targetUnitOfMeasureCode) {
                    Conversion conversion = UnitOfMeasureConverter.findConversion(
                            targetUnitOfMeasureCode,
                            nextPriceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
                    nextPriceRiskFactorHolder.setConversion(conversion);
                }


                if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != targetCurrencyCode) {
                    nextPriceRiskFactorHolder.setFxRiskFactorHolder(
                            riskFactorManager.determineFxRiskFactor(
                                    nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                                    targetCurrencyCode,
                                    currentDate));
                }

                physicalPositionHolder.addBasisToHubMarketRiskFactorHolder(nextPriceRiskFactorHolder);
                currentContainer = nextContainer;
            }
        }
    }

    public List<DealPositionSnapshot> generateDealPositionSnapshots(LocalDateTime observedDateTime) {

        List<DealPositionSnapshot> positions = new ArrayList<>();

        for (PositionHolder holder : positionHolders) {
            PhysicalPositionHolder physicalPositionHolder = (PhysicalPositionHolder) holder;
            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) holder.getDealPositionSnapshot();
            positionSnapshot.getDealPositionDetail().setCreateUpdateDateTime(observedDateTime);
            positionSnapshot.setDealId(dealSummary.getDealId());
            positionSnapshot.setDealTypeValue(DealTypeCode.PHYSICAL_DEAL.getCode());
            positionSnapshot.getDealPositionDetail().setErrorCode("0");
            // Market Price
            positionSnapshot.setMarketPriceRiskFactorId(physicalPositionHolder.getMarketRiskFactorHolder().getRiskFactor().getEntityId());

            if (physicalPositionHolder.getMarketFxHolder() != null) {
                positionSnapshot.setMarketPriceFxRiskFactorId(
                        physicalPositionHolder.getMarketFxHolder().getRiskFactor().getFxIndexId());
            }

            for (PriceRiskFactorHolder factorHolder : physicalPositionHolder.getBasisToHubMarketHolders()) {
                PositionRiskFactorMappingSnapshot mapping = new PositionRiskFactorMappingSnapshot();
                mapping.getDetail().setPriceTypeCode(PriceTypeCode.MARKET_PRICE);
                mapping.setPriceRiskFactorId(factorHolder.getRiskFactor().getEntityId());

                if (factorHolder.getFxRiskFactorHolder() != null)
                    mapping.setFxRiskFactorId(factorHolder.getFxRiskFactorHolder().getRiskFactor().getEntityId());
                positionSnapshot.addRiskFactorMappingSnapshot(mapping);
            }

            // Deal Price

            if (physicalPositionHolder.getFixedDealPriceFxHolder() != null) {
                positionSnapshot.setFixedPriceFxRiskFactorId(physicalPositionHolder.getFixedDealPriceFxHolder().getRiskFactor().getEntityId());
            }

            if (physicalPositionHolder.getDealPriceRiskFactorHolder() != null) {
                positionSnapshot.setDealPriceRiskFactorId(physicalPositionHolder.getDealPriceRiskFactorHolder().getRiskFactor().getEntityId());

                if (physicalPositionHolder.getDealPriceFxHolder() != null) {
                    positionSnapshot.setDealPriceFxRiskFactorId(physicalPositionHolder.getDealPriceFxHolder().getRiskFactor().getEntityId());
                }
            }

            for (PriceRiskFactorHolder factorHolder : physicalPositionHolder.getBasisToHubDealPriceHolders()) {
                PositionRiskFactorMappingSnapshot mapping = new PositionRiskFactorMappingSnapshot();
                mapping.getDetail().setPriceTypeCode(PriceTypeCode.DEAL_PRICE);

                mapping.setPriceRiskFactorId(factorHolder.getRiskFactor().getEntityId());
                if (factorHolder.getFxRiskFactorHolder() != null)
                    mapping.setFxRiskFactorId(factorHolder.getFxRiskFactorHolder().getRiskFactor().getEntityId());
                positionSnapshot.addRiskFactorMappingSnapshot(mapping);
            }

            positions.add(positionSnapshot);
        }
        return positions;
    }
}
