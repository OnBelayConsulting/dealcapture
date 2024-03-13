package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.BuySellCode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;

public class PhysicalPositionValuator {

    private DealPositionView report;

    public PhysicalPositionValuator(DealPositionView report) {
        this.report = report;
    }
    public PositionValuationResult valuePosition(LocalDateTime currentDateTime) {
        Price dealPrice =  switch (report.getDetail().getDealPriceValuationCode()) {

            case FIXED -> getFixedPrice();

            case INDEX -> getDealIndexPrice();

            case INDEX_PLUS -> getDealIndexPrice().add(getFixedPrice());

        };

        Price marketPrice = getMarketIndexPrice();
        if (dealPrice.isInError() == false && marketPrice.isInError() == false) {
            dealPrice = dealPrice.roundPrice();
            marketPrice = marketPrice.roundPrice();
            Price netPrice;
            if (report.getDetail().getBuySellCode() == BuySellCode.BUY)
                netPrice = marketPrice.subtract(dealPrice);
            else
                netPrice = dealPrice.subtract(marketPrice);
            Amount amount = netPrice.multiply(report.getDetail().getQuantity());

            if (amount.isInError()) {
                return new PositionValuationResult(
                        report.getId(),
                        currentDateTime,
                        amount.getError().getCode());
            } else {
                return new PositionValuationResult(
                        report.getId(),
                        currentDateTime,
                        amount.getValue());
            }
        } else {
            return new PositionValuationResult(
                    report.getId(),
                    currentDateTime,
                    PositionErrorCode.ERROR_VALUE_POSITION_MISSING_PRICES.getCode());
        }
    }

    private Price getMarketIndexPrice() {

        Price marketPrice = report.getDetail().getMarketPrice();

        if (report.getDetail().getMarketPriceFxValue() != null) {
            if (marketPrice.getCurrency() != report.getDetail().getCurrencyCode()) {
                marketPrice = marketPrice.multiply(report.getDetail().getMarketPriceFxValue());
            } else {
                BigDecimal inverted = BigDecimal.ONE.divide(report.getDetail().getMarketPriceFxValue(), MathContext.DECIMAL128);
                inverted = inverted.setScale(4);
                marketPrice = marketPrice.multiply(inverted);
            }
        }

        if (report.getDetail().getVolumeUnitOfMeasure() != marketPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    report.getDetail().getVolumeUnitOfMeasure(),
                    marketPrice.getUnitOfMeasure());
            marketPrice = marketPrice.apply(conversion);
        }


        List<PositionRiskFactorMappingSummary> summaries = report.findMappingSummaries(PriceTypeCode.MARKET_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                marketPrice = marketPrice.add(summary.calculateConvertedPrice(
                        marketPrice.getCurrency(),
                        marketPrice.getUnitOfMeasure()));
            }
        }
        return marketPrice;
    }

    public Price getDealIndexPrice() {
        Price dealPrice = report.getDetail().getDealPrice();
        if (report.getDetail().getDealPriceFxRateValue() != null) {
            if (dealPrice.getCurrency() != report.getDetail().getCurrencyCode()) {
                dealPrice = dealPrice.multiply(report.getDetail().getDealPriceFxRateValue());
            } else {
                BigDecimal inverted = BigDecimal.ONE.divide(report.getDetail().getDealPriceFxRateValue(), MathContext.DECIMAL128);
                inverted = inverted.setScale(4);
                dealPrice = dealPrice.multiply(inverted);
            }
        }

        if (report.getDetail().getVolumeUnitOfMeasure() != dealPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    report.getDetail().getVolumeUnitOfMeasure(),
                    dealPrice.getUnitOfMeasure());
            dealPrice = dealPrice.apply(conversion);
        }

        List<PositionRiskFactorMappingSummary> summaries = report.findMappingSummaries(PriceTypeCode.DEAL_PRICE);
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
        Price fixedPrice = report.getDetail().getFixedPrice();
        if (report.getDetail().getFixedFxRateValue() != null) {
            if (fixedPrice.getCurrency() != report.getDetail().getCurrencyCode()) {
                fixedPrice = fixedPrice.multiply(report.getDetail().getFixedFxRateValue());
            } else {
                BigDecimal inverted = BigDecimal.ONE.divide(report.getDetail().getFixedFxRateValue(), MathContext.DECIMAL128);
                inverted = inverted.setScale(4);
                fixedPrice = fixedPrice.multiply(inverted);
            }
        }

        if (report.getDetail().getVolumeUnitOfMeasure() != fixedPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    report.getDetail().getVolumeUnitOfMeasure(),
                    fixedPrice.getUnitOfMeasure());
            fixedPrice = fixedPrice.apply(conversion);
        }
        return fixedPrice;
    }

}
