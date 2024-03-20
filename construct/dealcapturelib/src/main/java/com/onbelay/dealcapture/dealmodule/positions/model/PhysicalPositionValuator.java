package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.BuySellCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

public class PhysicalPositionValuator implements PositionValuator {
    private static final Logger logger = LogManager.getLogger();
    private DealPositionView positionView;
    private ValuationIndexManager valuationIndexManager;

    public PhysicalPositionValuator(
            ValuationIndexManager valuationIndexManager,
            DealPositionView positionView) {

        this.valuationIndexManager = valuationIndexManager;
        this.positionView = positionView;
    }

    public PositionValuationResult valuePosition(LocalDateTime currentDateTime) {
        PositionValuationResult valuationResult = new PositionValuationResult(
                positionView.getId(),
                currentDateTime);

        Price dealPrice = switch (positionView.getDetail().getDealPriceValuationCode()) {

            case FIXED -> getFixedPrice();

            case INDEX -> getDealIndexPrice();

            case INDEX_PLUS -> getDealIndexPrice().add(getFixedPrice());

        };

        if (dealPrice.isInError())
            valuationResult.addErrorCode(PositionErrorCode.ERROR_VALUE_MTM_DEAL_PRICE);

        final Amount totalCostAmount;
        if (positionView.hasCosts())
            totalCostAmount = calculateCosts();
        else
            totalCostAmount = new Amount(
                    BigDecimal.ZERO,
                    positionView.getDetail().getCurrencyCode());

        valuationResult.getSettlementDetail().setCostSettlementAmount(totalCostAmount.getValue());

        setMarkToMarket(
                dealPrice,
                totalCostAmount,
                valuationResult);

        if (positionView.getDetail().getIsSettlementPosition() == true)
            setSettlementAmounts(
                dealPrice,
                totalCostAmount,
                valuationResult);

        return valuationResult;
    }

    private void setMarkToMarket(
            Price dealPrice,
            Amount totalCostAmount,
            PositionValuationResult valuationResult) {

        Price marketPrice = getMarketIndexPrice();
        if (marketPrice.isInError()) {
            valuationResult.addErrorCode(PositionErrorCode.ERROR_VALUE_MISSING_MARKET_PRICE);
        }

        if (dealPrice.isInError() == false && marketPrice.isInError() == false) {
            dealPrice = dealPrice.roundPrice();
            marketPrice = marketPrice.roundPrice();
            Price netPrice;
            if (positionView.getDetail().getBuySellCode() == BuySellCode.BUY)
                netPrice = marketPrice.subtract(dealPrice);
            else
                netPrice = dealPrice.subtract(marketPrice);
            Amount amount = netPrice.multiply(positionView.getDetail().getQuantity());

            if (amount.isInError()) {
                valuationResult.addErrorCode(PositionErrorCode.ERROR_VALUE_MTM_CALCULATION);
            } else {
                amount = amount.add(totalCostAmount);
                amount = amount.round();
                valuationResult.getSettlementDetail().setMarkToMarketValuation(amount.getValue());
            }

        }
    }

    private Amount calculateCosts() {
        BigDecimal totalCosts = BigDecimal.ZERO;

        FxRate fxRate = positionView.getCostFxRate(valuationIndexManager);

        if (positionView.getDetail().getCurrencyCode() != positionView.getDetail().getCostCurrencyCode()) {

            if (fxRate == null) {
                logger.error("No fx Rate for costs.");
                return new Amount(CalculatedErrorType.ERROR);
            }

        }

        for (int i = 1; i < 6; i++) {
            BigDecimal cost = positionView.getCostPositionDetail().getCostAmount(i);
            if (cost != null) {
                CostTypeCode costTypeCode = positionView.getCostPositionDetail().getCostTypeCode(i);

                if (positionView.getDetail().getCurrencyCode() != positionView.getDetail().getSettlementCurrencyCode()) {
                    cost.multiply(positionView.getDetail().getCostFxRateValue(), MathContext.DECIMAL128);

                }
                if (costTypeCode == CostTypeCode.PER_UNIT) {

                    if (positionView.getDetail().getVolumeUnitOfMeasure() != positionView.getDetail().getDealUnitOfMeasureCode()) {
                        Conversion conversion = UnitOfMeasureConverter.findConversion(
                                positionView.getDetail().getVolumeUnitOfMeasure(),
                                positionView.getDetail().getDealUnitOfMeasureCode());
                        cost = cost.multiply(conversion.getValue(), MathContext.DECIMAL128);
                        cost = cost.setScale(3, RoundingMode.HALF_UP);

                        cost = cost.multiply(positionView.getDetail().getVolumeQuantityValue(), MathContext.DECIMAL128);
                    }
                }
                totalCosts = totalCosts.add(cost, MathContext.DECIMAL128);

            }
        }
        Amount totalAmount = new Amount(
                totalCosts,
                positionView.getDetail().getCostCurrencyCode());

        if (fxRate != null && fxRate.isInError() == false)
            totalAmount = totalAmount.apply(fxRate);

       return totalAmount;
    }

    private void setSettlementAmounts(
            Price dealPrice,
            Amount totalCostAmount,
            PositionValuationResult valuationResult) {

        if (dealPrice.isInError()) {
            valuationResult.addErrorCode(PositionErrorCode.ERROR_VALUE_SET_DEAL_PRICE);
            return;
        }

        Amount settlementAmount = dealPrice.multiply(positionView.getDetail().getQuantity());

        if (positionView.getDetail().getBuySellCode() == BuySellCode.BUY) {
            settlementAmount = settlementAmount.negate();
        }
        Amount totalSettlementAmount = settlementAmount.add(totalCostAmount);

        settlementAmount = settlementAmount.round();
        valuationResult.getSettlementDetail().setSettlementAmount(settlementAmount.getValue());

        totalSettlementAmount = totalSettlementAmount.round();
        valuationResult.getSettlementDetail().setTotalSettlementAmount(totalSettlementAmount.getValue());
    }

    private Price getMarketIndexPrice() {

        Price marketPrice = positionView.getMarketPrice(valuationIndexManager);

        if (marketPrice.getCurrency() != positionView.getDetail().getCurrencyCode()) {
            FxRate marketPriceFxRate = positionView.getMarketPriceFxRate(valuationIndexManager);

            if (marketPriceFxRate == null) {
                logger.error("Market Price Fx rate is missing and is required.");
                logger.error("Market Price Currency: " + marketPrice.getCurrency().getCode()
                        + " vs position view: " + positionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            marketPrice = marketPrice.apply(marketPriceFxRate);
        }

        if (positionView.getDetail().getVolumeUnitOfMeasure() != marketPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    positionView.getDetail().getVolumeUnitOfMeasure(),
                    marketPrice.getUnitOfMeasure());
            marketPrice = marketPrice.apply(conversion);
        }

        if (marketPrice.isInError())
            return marketPrice;

        List<PositionRiskFactorMappingSummary> summaries = positionView.findMappingSummaries(PriceTypeCode.MARKET_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                marketPrice = marketPrice.add(
                        summary.calculateConvertedPrice(
                            marketPrice.getCurrency(),
                            marketPrice.getUnitOfMeasure()));
            }
        }
        return marketPrice;
    }

    public Price getDealIndexPrice() {
        Price dealPrice = positionView.getDealPrice(valuationIndexManager);

        if (dealPrice.getCurrency() != positionView.getDetail().getCurrencyCode()) {
            FxRate  fxRate = positionView.getDealPriceFxRate(valuationIndexManager);

            if (fxRate == null) {
                logger.error("DealPrice Fx rate is missing and is required.");
                logger.error("DealPrice Currency: " + dealPrice.getCurrency().getCode()
                        + " vs position view: " + positionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            dealPrice = dealPrice.apply(fxRate);
        }

        if (dealPrice.isInError()) {
          return dealPrice;
        }

        if (positionView.getDetail().getVolumeUnitOfMeasure() != dealPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    positionView.getDetail().getVolumeUnitOfMeasure(),
                    dealPrice.getUnitOfMeasure());
            dealPrice = dealPrice.apply(conversion);
        }

        List<PositionRiskFactorMappingSummary> summaries = positionView.findMappingSummaries(PriceTypeCode.DEAL_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                dealPrice = dealPrice.add(summary.calculateConvertedPrice(
                        dealPrice.getCurrency(),
                        dealPrice.getUnitOfMeasure()));
            }
        }

        return dealPrice;
    }

    public Price getFixedPrice() {
        Price fixedPrice = positionView.getFixedPrice();

        if (fixedPrice.getCurrency() != positionView.getDetail().getCurrencyCode()) {
            FxRate fxRate = positionView.getFixedFxRate(valuationIndexManager);

            if (fxRate == null) {
                logger.error("Fixed Price Fx rate is missing and is required.");
                logger.error("Fixed Price Currency: " + fixedPrice.getCurrency().getCode()
                        + " vs position view: " + positionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            fixedPrice = fixedPrice.apply(fxRate);
        }

        if (fixedPrice.isInError())
            return fixedPrice;

        if (positionView.getDetail().getVolumeUnitOfMeasure() != fixedPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    positionView.getDetail().getVolumeUnitOfMeasure(),
                    fixedPrice.getUnitOfMeasure());
            fixedPrice = fixedPrice.apply(conversion);
        }
        return fixedPrice;
    }

}
