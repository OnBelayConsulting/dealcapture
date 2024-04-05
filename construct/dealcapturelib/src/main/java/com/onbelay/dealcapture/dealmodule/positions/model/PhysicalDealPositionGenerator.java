package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
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
    public void generatePositionHolders(EvaluationContext contextIn) {
        EvaluationContext context = modifyEvaluationContextForDeal(contextIn);

        PhysicalDealSummary physicalDealSummary = (PhysicalDealSummary) dealSummary;

        LocalDate currentDate = context.getStartPositionDate();
        while (currentDate.isAfter(context.getEndPositionDate()) == false) {
            PhysicalPositionHolder physicalPositionHolder = new PhysicalPositionHolder(new PhysicalPositionSnapshot());
            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();

            positionSnapshot.getDealPositionDetail().setFrequencyCode(FrequencyCode.DAILY);
            positionSnapshot.getDealPositionDetail().setStartDate(currentDate);
            positionSnapshot.getDealPositionDetail().setEndDate(currentDate);

            setBasePositionHolderAttributes(
                    context,
                    physicalPositionHolder);


            // Deal Price
            positionSnapshot.getDetail().setDealPriceValuationCode(physicalDealSummary.getDealPriceValuationCode());

            determineDealPriceRiskFactors(
                    context,
                    physicalPositionHolder,
                    physicalDealSummary,
                    currentDate);

            // Market
            positionSnapshot.getDetail().setDealMarketValuationCode(physicalDealSummary.getMarketValuationCode());
            determineMarketPriceRiskFactors(
                    context,
                    physicalPositionHolder,
                    physicalDealSummary,
                    currentDate);


                positionHolders.add(physicalPositionHolder);
                currentDate = currentDate.plusDays(1);
        }
    }

    private void determineDealPriceRiskFactors(
            EvaluationContext context,
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary,
            LocalDate currentDate) {

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();

        // if deal price valuation is fixed or INDEX Plus
        if (positionSnapshot.getDetail().getDealPriceValuationCode() == ValuationCode.FIXED ||
                positionSnapshot.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {

            // Set fixed deal price
            BigDecimal fixedPrice = null;
            if (hasDealDaysContainerForPrice(positionSnapshot.getDealPositionDetail().getStartDate()))
                fixedPrice = getDayPrice(positionSnapshot.getDealPositionDetail().getStartDate());

            if (fixedPrice != null)
                positionSnapshot.getDetail().setFixedPriceValue(fixedPrice);
            else
                positionSnapshot.getDetail().setFixedPriceValue(
                    physicalDealSummary.getFixedPriceValue());

            positionSnapshot.getDetail().setFixedPriceUnitOfMeasure(
                    physicalDealSummary.getFixedPriceUnitOfMeasureCode());
            positionSnapshot.getDetail().setFixedPriceCurrencyCode(
                    physicalDealSummary.getFixedPriceCurrencyCode());


            if (physicalDealSummary.getFixedPriceCurrencyCode() != context.getCurrencyCode()) {
                physicalPositionHolder.setFixedDealPriceFxHolder(
                        riskFactorManager.determineFxRiskFactor(
                                physicalDealSummary.getFixedPriceCurrencyCode(),
                                context.getCurrencyCode(),
                                currentDate));
            }
        }

        // deal price is index or index plus
        if (positionSnapshot.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX ||
                positionSnapshot.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
            PriceRiskFactorHolder priceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                    physicalDealSummary.getDealPriceIndexId(),
                    currentDate);


            if (priceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode() != context.getUnitOfMeasureCode()) {
                Conversion conversion = UnitOfMeasureConverter.findConversion(
                        context.getUnitOfMeasureCode(),
                        priceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
                priceRiskFactorHolder.setConversion(conversion);
            }

            physicalPositionHolder.setDealPriceRiskFactorHolder(priceRiskFactorHolder);
            if (priceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != context.getCurrencyCode()) {
                priceRiskFactorHolder.setFxRiskFactorHolder(
                        riskFactorManager.determineFxRiskFactor(
                                priceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                                context.getCurrencyCode(),
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

                    if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode() != context.getUnitOfMeasureCode()) {
                        Conversion conversion = UnitOfMeasureConverter.findConversion(
                                context.getUnitOfMeasureCode(),
                                nextPriceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
                        nextPriceRiskFactorHolder.setConversion(conversion);
                    }

                    if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != context.getCurrencyCode()) {
                        nextPriceRiskFactorHolder.setFxRiskFactorHolder(
                                riskFactorManager.determineFxRiskFactor(
                                        nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                                        context.getCurrencyCode(),
                                        currentDate));
                    }

                    physicalPositionHolder.addBasisToHubDealPriceRiskFactorHolder(nextPriceRiskFactorHolder);
                    currentContainer = nextContainer;
                }
            }
        }

    }

    private void determineMarketPriceRiskFactors(
            EvaluationContext context,
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSummary physicalDealSummary,
            LocalDate currentDate) {

        PriceRiskFactorHolder marketRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                physicalDealSummary.getMarketIndexId(),
                currentDate);

        physicalPositionHolder.setMarketRiskFactorHolder(marketRiskFactorHolder);

        if (marketRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode() != context.getUnitOfMeasureCode()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    context.getUnitOfMeasureCode(),
                    marketRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
            marketRiskFactorHolder.setConversion(conversion);
        }

        if (marketRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            marketRiskFactorHolder.setFxRiskFactorHolder(
                    riskFactorManager.determineFxRiskFactor(
                            marketRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                            context.getCurrencyCode(),
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

                if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode() != context.getUnitOfMeasureCode()) {
                    Conversion conversion = UnitOfMeasureConverter.findConversion(
                            context.getUnitOfMeasureCode(),
                            nextPriceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
                    nextPriceRiskFactorHolder.setConversion(conversion);
                }


                if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != context.getCurrencyCode()) {
                    nextPriceRiskFactorHolder.setFxRiskFactorHolder(
                            riskFactorManager.determineFxRiskFactor(
                                    nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                                    context.getCurrencyCode(),
                                    currentDate));
                }

                physicalPositionHolder.addBasisToHubMarketRiskFactorHolder(nextPriceRiskFactorHolder);
                currentContainer = nextContainer;
            }
        }
    }

    public List<DealPositionSnapshot> generateDealPositionSnapshots(LocalDateTime createdDateTime) {

        List<DealPositionSnapshot> positions = new ArrayList<>();

        for (PositionHolder holder : positionHolders) {
            PhysicalPositionHolder physicalPositionHolder = (PhysicalPositionHolder) holder;
            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) holder.getDealPositionSnapshot();

            positionSnapshot.getDealPositionDetail().setCreateUpdateDateTime(createdDateTime);
            positionSnapshot.setDealId(dealSummary.getDealId());
            positionSnapshot.setDealTypeValue(DealTypeCode.PHYSICAL_DEAL.getCode());
            positionSnapshot.getDealPositionDetail().setErrorCode("0");


            // Market Price
            positionSnapshot.setMarketPriceRiskFactorId(physicalPositionHolder.getMarketRiskFactorHolder().getRiskFactor().getEntityId());

            if (physicalPositionHolder.getMarketFxHolder() != null) {
                positionSnapshot.setMarketPriceFxRiskFactorId(
                        physicalPositionHolder.getMarketFxHolder().getRiskFactor().getEntityId());
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
