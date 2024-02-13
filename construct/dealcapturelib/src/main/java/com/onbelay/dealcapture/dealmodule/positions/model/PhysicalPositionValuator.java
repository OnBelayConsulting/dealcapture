package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionReport;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.BuySellCode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;

public class PhysicalPositionValuator {

    private PhysicalPositionReport report;

    public PhysicalPositionValuator(PhysicalPositionReport report) {
        this.report = report;
    }
    public PositionValuationResult valuePosition(LocalDateTime currentDateTime) {
        Price dealPrice =  switch (getDealPriceValuationCode()) {

            case FIXED -> getFixedPrice();

            case INDEX -> getDealIndexPrice();

            case INDEX_PLUS -> getDealIndexPrice().add(getFixedPrice());

        };

        Price marketPrice = getMarketIndexPrice();
        if (dealPrice.isInError() == false && marketPrice.isInError() == false) {
            dealPrice = dealPrice.roundPrice();
            marketPrice = marketPrice.roundPrice();
            Price netPrice;
            if (report.getBuySellCode() == BuySellCode.BUY)
                netPrice = marketPrice.subtract(dealPrice);
            else
                netPrice = dealPrice.subtract(marketPrice);
            Amount amount = netPrice.multiply(report.getVolumeQuantity());

            if (amount.isInError()) {
                return new PositionValuationResult(
                        report.getDealPositionId(),
                        currentDateTime,
                        amount.getError().getCode());
            } else {
                return new PositionValuationResult(
                        report.getDealPositionId(),
                        currentDateTime,
                        amount.getValue());
            }
        } else {
            return new PositionValuationResult(
                    report.getDealPositionId(),
                    currentDateTime,
                    PositionErrorCode.ERROR_VALUE_POSITION_MISSING_PRICES.getCode());
        }
    }

    private Price getMarketIndexPrice() {

        Price marketPrice = report.getMarketIndexPrice();

        if (report.getMarketPriceIndexFxValue() != null) {
            if (marketPrice.getCurrency() != report.getCurrencyCode()) {
                marketPrice = marketPrice.multiply(report.getMarketPriceIndexFxValue());
            } else {
                BigDecimal inverted = BigDecimal.ONE.divide(report.getMarketPriceIndexFxValue(), MathContext.DECIMAL128);
                inverted = inverted.setScale(4);
                marketPrice = marketPrice.multiply(inverted);
            }
        }

        if (report.getVolumeUnitOfMeasureCode() != marketPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    report.getVolumeUnitOfMeasureCode(),
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
        Price dealPrice = report.getDealIndexPrice();
        if (report.getDealPriceIndexFxValue() != null) {
            if (dealPrice.getCurrency() != report.getCurrencyCode()) {
                dealPrice = dealPrice.multiply(report.getDealPriceIndexFxValue());
            } else {
                BigDecimal inverted = BigDecimal.ONE.divide(report.getDealPriceIndexFxValue(), MathContext.DECIMAL128);
                inverted = inverted.setScale(4);
                dealPrice = dealPrice.multiply(inverted);
            }
        }

        if (report.getVolumeUnitOfMeasureCode() != dealPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    report.getVolumeUnitOfMeasureCode(),
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
        Price fixedPrice = report.getFixedPrice();
        if (report.getFixedPriceFxValue() != null) {
            if (fixedPrice.getCurrency() != report.getCurrencyCode()) {
                fixedPrice = fixedPrice.multiply(report.getFixedPriceFxValue());
            } else {
                BigDecimal inverted = BigDecimal.ONE.divide(report.getFixedPriceFxValue(), MathContext.DECIMAL128);
                inverted = inverted.setScale(4);
                fixedPrice = fixedPrice.multiply(inverted);
            }
        }

        if (report.getVolumeUnitOfMeasureCode() != fixedPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    report.getVolumeUnitOfMeasureCode(),
                    fixedPrice.getUnitOfMeasure());
            fixedPrice = fixedPrice.apply(conversion);
        }
        return fixedPrice;
    }


    private ValuationCode getDealPriceValuationCode() {
        if (report.getFixedPriceValue() != null) {
            if (report.getDealPriceIndexValue() != null)
                return ValuationCode.INDEX_PLUS;
            else
                return ValuationCode.FIXED;
        } else {
            return ValuationCode.INDEX;
        }
    }

    private ValuationCode getMarketPriceValuationCode() {
        if (report.getFixedPriceValue() != null) {
            if (report.getMarketPriceIndexValue() != null)
                return ValuationCode.INDEX_PLUS;
            else
                return ValuationCode.FIXED;
        } else {
            return ValuationCode.INDEX;
        }
    }



}
