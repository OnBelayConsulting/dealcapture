package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.optionvaluation.OptionEvaluator;
import com.onbelay.dealcapture.dealmodule.positions.optionvaluation.OptionEvaluatorFactory;
import com.onbelay.dealcapture.dealmodule.positions.optionvaluation.OptionResult;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.BuySellCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class VanillaOptionsPositionEvaluator extends DealPositionEvaluator implements PositionEvaluator {
    private static final Logger logger = LogManager.getLogger();

    public VanillaOptionsPositionEvaluator(
            DealPositionView view,
            ValuationIndexManager valuationIndexManager) {
        super(
                view,
                valuationIndexManager);

    }

    private VanillaOptionPositionView getVanillaOptionPositionView() {
        return (VanillaOptionPositionView) dealPositionView;
    }

    public PositionValuationResult valuePosition() {
        VanillaOptionPositionValuationResult valuationResult = new VanillaOptionPositionValuationResult(
                dealPositionView.getId(),
                currentDateTime);

        // compare underlying and strike in original currency.
        Price underlyingPrice = getVanillaOptionPositionView().getUnderlyingPrice(valuationIndexManager);

        Price strikePrice = getVanillaOptionPositionView().getDealStrikePrice(underlyingPrice);


        if (underlyingPrice.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_DEAL_PRICE);
        } else {
            valuationResult.getPriceDetail().setUnderlyingPriceValue(underlyingPrice.roundPrice().getValue());
        }

        if (strikePrice.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_DEAL_PRICE);
        } else {
            valuationResult.getPriceDetail().setStrikePriceValue(strikePrice.roundPrice().getValue());
        }

        Amount totalCostAmount;
        if (totalCostPositionSummary != null)
            totalCostAmount = new Amount(
                    totalCostPositionSummary.getTotalCostAmount(),
                    dealPositionView.getViewDetail().getCurrencyCode());
        else
            totalCostAmount = new Amount(
                    BigDecimal.ZERO,
                    dealPositionView.getViewDetail().getCurrencyCode());

        valuationResult.getSettlementDetail().setCostSettlementAmount(totalCostAmount.getValue());

        Price netPrice = Price.zeroUsing(underlyingPrice);

        if (underlyingPrice.isInError() == false && strikePrice.isInError() == false) {
            underlyingPrice = underlyingPrice.roundPrice();
            strikePrice = strikePrice.roundPrice();

            if (getCurrentDateTime().toLocalDate()
                    .isBefore(getVanillaOptionPositionView().getPriceDetail().getOptionExpiryDate())) {
                netPrice = calculateNetPriceBeforeExpiry(
                        underlyingPrice,
                        strikePrice);
            } else {
                netPrice = calculateNetPriceAtExpiry(
                        underlyingPrice,
                        strikePrice);
            }

            // convert result to position currency

            if (getVanillaOptionPositionView().getViewDetail().getCurrencyCode() != underlyingPrice.getCurrency()) {
                FxRate rate = getVanillaOptionPositionView().getUnderlyingPriceFxRate(valuationIndexManager);
                netPrice = netPrice.apply(rate);
            }

            setMarkToMarket(
                    netPrice,
                    totalCostAmount,
                    valuationResult);
        }

        if (dealPositionView.getViewDetail().getIsSettlementPosition()) {

            setSettlementAmounts(
                    netPrice,
                    totalCostAmount,
                    valuationResult);
        }


        return valuationResult;
    }

    private Price calculateNetPriceAtExpiry(
            Price underlyingPrice,
            Price strikePrice) {

        Price netPrice;

        if (getVanillaOptionPositionView().getViewDetail().getBuySellCode() == BuySellCode.BUY) {
            if (getVanillaOptionPositionView().getOptionPositionDetail().getOptionTypeCode() == OptionTypeCode.CALL) {
                if (underlyingPrice.isGreaterThan(strikePrice)) {
                    netPrice = underlyingPrice.subtract(strikePrice);
                } else {
                    netPrice = Price.zeroUsing(underlyingPrice);
                }
            } else {
                if (strikePrice.isGreaterThan(underlyingPrice)) {
                    netPrice = strikePrice.subtract(underlyingPrice);
                } else {
                    netPrice = Price.zeroUsing(underlyingPrice);
                }
            }
        } else {
            if (getVanillaOptionPositionView().getOptionPositionDetail().getOptionTypeCode() == OptionTypeCode.CALL) {
                if (underlyingPrice.isGreaterThan(strikePrice)) {
                    netPrice = underlyingPrice.subtract(strikePrice);
                } else {
                    netPrice = Price.zeroUsing(underlyingPrice);
                }
            } else {
                if (strikePrice.isGreaterThan(underlyingPrice)) {
                    netPrice = strikePrice.subtract(underlyingPrice);
                } else {
                    netPrice = Price.zeroUsing(underlyingPrice);
                }
            }
            // If you sell an option then you lose if it's in the money
            netPrice = netPrice.negate();
        }
        return netPrice;
    }


    private Price calculateNetPriceBeforeExpiry(
            Price underlyingPrice,
            Price strikePrice) {

        Double volatility = getVanillaOptionPositionView().getOptionPositionDetail().getVolatilityValue();

        OptionEvaluator optionEvaluator = OptionEvaluatorFactory.createOptionEvaluator();

        LocalDate expiryDate = getVanillaOptionPositionView().getPriceDetail().getOptionExpiryDate();
        LocalDate currentDate =  getCurrentDateTime().toLocalDate();
        int expiryDays = expiryDate.getDayOfYear();
        int currentDays = currentDate.getDayOfYear();
        int netDays = expiryDays - currentDays;
        BigDecimal yearInDays = BigDecimal.valueOf(netDays).divide(BigDecimal.valueOf(365.00), MathContext.DECIMAL128);
        yearInDays = yearInDays.setScale(4, RoundingMode.HALF_UP);
        
        OptionResult result = optionEvaluator.evaluate(
                getVanillaOptionPositionView().getOptionPositionDetail().getOptionTypeCode(),
                underlyingPrice.toDouble(),
                strikePrice.toDouble(),
                yearInDays.doubleValue(),
                valuationIndexManager.getCurrentRiskFreeRate().toDouble(),
                volatility);

        Price price;
        if (result.getValuation() != null) {
            price =  new Price(
                    result.getValuationAsBigDecimal(),
                    underlyingPrice.getCurrency(),
                    underlyingPrice.getUnitOfMeasure());

            if (getVanillaOptionPositionView().getViewDetail().getBuySellCode() == BuySellCode.SELL)
                price = price.negate();

        } else {
            price = new Price(CalculatedErrorType.ERROR);
        }

        return price;
    }

}
