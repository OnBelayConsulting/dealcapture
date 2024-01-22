package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;
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
import java.util.ArrayList;
import java.util.List;

public class PhysicalDealPositionGenerator implements DealPositionGenerator {

    private BaseDealSnapshot dealSnapshot;

    private RiskFactorManager riskFactorManager;

    private List<PositionHolder> positionHolders = new ArrayList<>();

    public static DealPositionGenerator newGenerator(
            BaseDealSnapshot dealSnapshot,
            RiskFactorManager riskFactorManager) {
        return new PhysicalDealPositionGenerator(
                dealSnapshot,
                riskFactorManager);
    }

    private PhysicalDealPositionGenerator(
            BaseDealSnapshot dealSnapshot,
            RiskFactorManager riskFactorManager) {
        this.dealSnapshot = dealSnapshot;
        this.riskFactorManager = riskFactorManager;
    }

    @Override
    public void generatePositionHolders(EvaluationContext context) {

        PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) dealSnapshot;

        LocalDate currentDate = physicalDealSnapshot.getDealDetail().getStartDate();
        while (currentDate.isAfter(physicalDealSnapshot.getDealDetail().getEndDate()) == false) {
            PhysicalPositionHolder physicalPositionHolder = new PhysicalPositionHolder(new PhysicalPositionSnapshot());
            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();
            positionSnapshot.getDealPositionDetail().setFrequencyCode(FrequencyCode.DAILY);

            CurrencyCode targetCurrencyCode;
            if (context.getCurrencyCode() != null)
                targetCurrencyCode = context.getCurrencyCode();
            else
                targetCurrencyCode = physicalDealSnapshot.getDealDetail().getReportingCurrencyCode();

            UnitOfMeasureCode targetUnitOfMeasureCode;
            if (context.getUnitOfMeasureCode() != null)
                targetUnitOfMeasureCode = context.getUnitOfMeasureCode();
            else
                targetUnitOfMeasureCode = physicalDealSnapshot.getDealDetail().getVolumeUnitOfMeasure();

            if (targetUnitOfMeasureCode == physicalDealSnapshot.getDealDetail().getVolumeUnitOfMeasure()) {
                positionSnapshot.getDealPositionDetail().setVolumeQuantityValue(physicalDealSnapshot.getDealDetail().getVolumeQuantity());
            } else {
                BigDecimal quantity = physicalDealSnapshot.getDealDetail().getVolumeQuantity();
                Conversion conversion = UnitOfMeasureConverter.findConversion(targetUnitOfMeasureCode, physicalDealSnapshot.getDealDetail().getVolumeUnitOfMeasure());
                quantity = quantity.multiply(conversion.getValue(), MathContext.DECIMAL128);
                positionSnapshot.getDealPositionDetail().setVolumeQuantityValue(quantity);
            }
            positionSnapshot.getDealPositionDetail().setVolumeUnitOfMeasure(targetUnitOfMeasureCode);

            positionSnapshot.getDealPositionDetail().setStartDate(currentDate);
            positionSnapshot.getDealPositionDetail().setEndDate(currentDate);
            positionSnapshot.getDealPositionDetail().setCreateUpdateDateTime(context.getObservedDateTime());

            positionSnapshot.getDealPositionDetail().setCurrencyCode(targetCurrencyCode);

            // Deal Price
            positionSnapshot.getDetail().setDealPriceValuationCode(physicalDealSnapshot.getDetail().getDealPriceValuationCode());

            determineDealPriceRiskFactors(
                    physicalPositionHolder,
                    physicalDealSnapshot,
                    targetUnitOfMeasureCode,
                    targetCurrencyCode,
                    currentDate);

            // Market
            positionSnapshot.getDetail().setDealMarketValuationCode(physicalDealSnapshot.getDetail().getMarketValuationCode());
            determineMarketPriceRiskFactors(
                    physicalPositionHolder,
                    physicalDealSnapshot,
                    targetUnitOfMeasureCode,
                    targetCurrencyCode,
                    currentDate);


                positionHolders.add(physicalPositionHolder);
                currentDate = currentDate.plusDays(1);
        }
    }

    private void determineDealPriceRiskFactors(
            PhysicalPositionHolder physicalPositionHolder,
            PhysicalDealSnapshot physicalDealSnapshot,
            UnitOfMeasureCode targetUnitOfMeasureCode,
            CurrencyCode targetCurrencyCode,
            LocalDate currentDate) {

        PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) physicalPositionHolder.getDealPositionSnapshot();

        // if deal price valuation is fixed or INDEX Plus
        if (positionSnapshot.getDetail().getDealPriceValuationCode() != ValuationCode.INDEX) {


            if (physicalDealSnapshot.getDetail().getDealPriceUnitOfMeasure() != targetUnitOfMeasureCode) {
                Conversion conversion = UnitOfMeasureConverter.findConversion(
                        targetUnitOfMeasureCode,
                        physicalDealSnapshot.getDetail().getDealPriceUnitOfMeasure());
                positionSnapshot.getDetail().setDealPriceUOMConversion(conversion.getValue());
            } else {
                positionSnapshot.getDetail().setDealPriceUOMConversion(BigDecimal.ONE);
            }

            // Set fixed deal price
            positionSnapshot.getDetail().setDealPriceValue(
                    physicalDealSnapshot.getDetail().getDealPriceValue());
            positionSnapshot.getDetail().setDealPriceUnitOfMeasure(
                    physicalDealSnapshot.getDetail().getDealPriceUnitOfMeasure());
            positionSnapshot.getDetail().setDealPriceCurrencyCode(
                    physicalDealSnapshot.getDetail().getDealPriceCurrency());


            if (physicalDealSnapshot.getDetail().getDealPriceCurrency() != targetCurrencyCode) {
                physicalPositionHolder.setFixedDealPriceFxHolder(
                        riskFactorManager.determineFxRiskFactor(
                                physicalDealSnapshot.getDetail().getDealPriceCurrency(),
                                targetCurrencyCode,
                                currentDate));
            }
        } else {   // Deal price is index based
            PriceRiskFactorHolder priceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                    physicalDealSnapshot.getDealPriceIndexId().getCode(),
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
                            physicalDealSnapshot.getDealPriceIndexId().getCode());

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
            PhysicalDealSnapshot physicalDealSnapshot,
            UnitOfMeasureCode targetUnitOfMeasureCode,
            CurrencyCode targetCurrencyCode,
            LocalDate currentDate) {

        PriceRiskFactorHolder marketRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                physicalDealSnapshot.getMarketPriceIndexId().getCode(),
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
                        physicalDealSnapshot.getMarketPriceIndexId().getCode());

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

    public List<DealPositionSnapshot> generateDealPositionSnapshots() {
        List<DealPositionSnapshot> positions = new ArrayList<>();

        for (PositionHolder holder : positionHolders) {
            PhysicalPositionHolder physicalPositionHolder = (PhysicalPositionHolder) holder;

            PhysicalPositionSnapshot positionSnapshot = (PhysicalPositionSnapshot) holder.getDealPositionSnapshot();

            // Market Price
            positionSnapshot.setMarketPriceRiskFactorId(physicalPositionHolder.getMarketRiskFactorHolder().getRiskFactor().getEntityId());

            if (physicalPositionHolder.getMarketRiskFactorHolder().hasUnitOfMeasureConversion())
                positionSnapshot.getDetail().setMarketPriceUOMConversion(
                        physicalPositionHolder.getMarketRiskFactorHolder().getConversion().getValue());
            else
                positionSnapshot.getDetail().setMarketPriceUOMConversion(BigDecimal.ONE);

            if (physicalPositionHolder.getMarketFxHolder() != null) {
                positionSnapshot.setMarketFxRiskFactorId(
                        physicalPositionHolder.getMarketFxHolder().getRiskFactor().getFxIndexId());
            }

            for (PriceRiskFactorHolder factorHolder : physicalPositionHolder.getBasisToHubMarketHolders()) {
                PositionRiskFactorMappingSnapshot mapping = new PositionRiskFactorMappingSnapshot();
                mapping.getDetail().setPriceTypeCode(PriceTypeCode.MARKET_PRICE);
                mapping.setPriceRiskFactorId(factorHolder.getRiskFactor().getEntityId());

                if (factorHolder.hasUnitOfMeasureConversion()) {
                    mapping.getDetail().setUnitOfMeasureConversion(factorHolder.getConversion().getValue());
                } else {
                    mapping.getDetail().setUnitOfMeasureConversion(BigDecimal.ONE);
                }

                if (factorHolder.getFxRiskFactorHolder() != null)
                    mapping.setFxRiskFactorId(factorHolder.getFxRiskFactorHolder().getRiskFactor().getEntityId());
                positionSnapshot.addRiskFactorMappingSnapshot(mapping);
            }

            // Deal Price

            if (physicalPositionHolder.getFixedDealPriceFxHolder() != null) {
                positionSnapshot.setFixedDealPriceFxRiskFactorId(physicalPositionHolder.getFixedDealPriceFxHolder().getRiskFactor().getEntityId());
            }

            if (physicalPositionHolder.getDealPriceRiskFactorHolder() != null) {
                positionSnapshot.setDealPriceRiskFactorId(physicalPositionHolder.getDealPriceRiskFactorHolder().getRiskFactor().getEntityId());


                if (physicalPositionHolder.getDealPriceRiskFactorHolder().hasUnitOfMeasureConversion())
                    positionSnapshot.getDetail().setDealPriceUOMConversion(
                            physicalPositionHolder.getDealPriceRiskFactorHolder().getConversion().getValue());
                else
                    positionSnapshot.getDetail().setDealPriceUOMConversion(BigDecimal.ONE);

                if (physicalPositionHolder.getDealPriceFxHolder() != null) {
                    positionSnapshot.setDealPriceFxRiskFactorId(physicalPositionHolder.getDealPriceFxHolder().getRiskFactor().getEntityId());
                }
            }

            for (PriceRiskFactorHolder factorHolder : physicalPositionHolder.getBasisToHubDealPriceHolders()) {
                PositionRiskFactorMappingSnapshot mapping = new PositionRiskFactorMappingSnapshot();
                mapping.getDetail().setPriceTypeCode(PriceTypeCode.DEAL_PRICE);

                if (factorHolder.hasUnitOfMeasureConversion()) {
                    mapping.getDetail().setUnitOfMeasureConversion(factorHolder.getConversion().getValue());
                } else {
                    mapping.getDetail().setUnitOfMeasureConversion(BigDecimal.ONE);
                }

                mapping.setPriceRiskFactorId(factorHolder.getRiskFactor().getEntityId());
                if (factorHolder.getFxRiskFactorHolder() != null)
                    mapping.setFxRiskFactorId(factorHolder.getFxRiskFactorHolder().getRiskFactor().getEntityId());
                positionSnapshot.addRiskFactorMappingSnapshot(mapping);
            }

            positions.add(positionSnapshot);
        }
        return positions;
    }

    @Override
    public BaseDealSnapshot getDealSnapshot() {
        return dealSnapshot;
    }

    @Override
    public List<PositionHolder> getPositionHolders() {
        return positionHolders;
    }
}
