package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.*;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class DealPositionEvaluator implements PositionEvaluator {
    private static final Logger logger = LogManager.getLogger();

    protected DealPositionView dealPositionView;
    protected ValuationIndexManager valuationIndexManager;
    protected LocalDateTime currentDateTime;

    protected List<DealHourlyPositionView> hourlyPositionViews = new ArrayList<>();
    protected List<PowerProfilePositionView> powerProfilePositionViews = new ArrayList<>();
    protected TotalCostPositionSummary totalCostPositionSummary;

    public DealPositionEvaluator(
            DealPositionView dealPositionView,
            ValuationIndexManager valuationIndexManager) {
        this.valuationIndexManager = valuationIndexManager;
        this.dealPositionView = dealPositionView;
    }

    public LocalDateTime getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(LocalDateTime currentDateTime) {
        this.currentDateTime = currentDateTime;
    }


    public DealPositionEvaluator withCosts(TotalCostPositionSummary totalCostPositionSummary) {
        this.totalCostPositionSummary = totalCostPositionSummary;
        return this;
    }

    public DealPositionEvaluator withHourlyPositions(List<DealHourlyPositionView> hourlyPositionViews) {
        this.hourlyPositionViews = hourlyPositionViews;
        return this;
    }


    public DealPositionEvaluator withPowerProfilePositions(List<PowerProfilePositionView> viewList) {
        this.powerProfilePositionViews = viewList;
        return this;
    }


    protected void setMarkToMarket(
            Price netPrice,
            Amount totalCostAmount,
            PositionValuationResult valuationResult) {

        if (netPrice.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MISSING_MARKET_PRICE);
            return;
        }

        Amount amount = netPrice.multiply(dealPositionView.getViewDetail().getQuantity());

        if (amount.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_CALCULATION);
        } else {
            amount = amount.add(totalCostAmount);
            if (amount.isInError()) {
                valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_CALCULATION);
                valuationResult.addErrorMessage(PositionErrorCode.ERROR_MISSING_COST_FX_RATE_CONVERSION);
            } else {
                amount = amount.round();
                valuationResult.getSettlementDetail().setMarkToMarketValuation(amount.getValue());
            }
        }

    }

    protected void setSettlementAmounts(
            Price settlementPrice,
            Amount totalCostAmount,
            PositionValuationResult valuationResult) {

        if (settlementPrice.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_SET_DEAL_PRICE);
            return;
        }

        Amount settlementAmount = settlementPrice.multiply(dealPositionView.getViewDetail().getQuantity());


        settlementAmount = settlementAmount.round();
        if (settlementAmount.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_INVALID_SETTLE_AMOUNT);
        } else {
            valuationResult.getSettlementDetail().setSettlementAmount(settlementAmount.getValue());
        }

        if (settlementAmount.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_INVALID_TOTAL_SETTLE_MISSING_COST);
        } else {
            Amount totalSettlementAmount = settlementAmount.add(totalCostAmount);

            if (totalSettlementAmount.isInError()) {
                valuationResult.addErrorMessage(PositionErrorCode.ERROR_INVALID_TOTAL_SETTLE_MISSING_COST);
            } else {
                totalSettlementAmount = totalSettlementAmount.round();
                valuationResult.getSettlementDetail().setTotalSettlementAmount(totalSettlementAmount.getValue());
            }
        }

    }

    protected Price calculateIndexPriceWithPowerProfile(PositionValuationResult valuationResult) {

        if (powerProfilePositionViews.isEmpty())
            throw new OBRuntimeException(PositionErrorCode.MISSING_POWER_PROFILE_POSITIONS.getCode());

        Price totalPrice = new Price(
                BigDecimal.ZERO,
                dealPositionView.getViewDetail().getCurrencyCode(),
                dealPositionView.getViewDetail().getVolumeUnitOfMeasure());

        int totalPriceCount = 0;

        Amount totalAmount = new Amount(
                BigDecimal.ZERO,
                dealPositionView.getViewDetail().getCurrencyCode());

        Quantity totalQuantiy = new Quantity(
                BigDecimal.ZERO,
                dealPositionView.getViewDetail().getVolumeUnitOfMeasure());

        boolean hasVariableQuantity = false;
        DealHourlyPositionView quantitiesView = null;
        if (getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY) != null) {
            hasVariableQuantity = true;
            quantitiesView = getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY);
        }


            // Fetch current prices for this hourly position via the associated powerProfilePosition.
        for (DealHourlyPositionView hourlyPositionView : hourlyPositionViews) {

            PowerProfilePositionView powerProfilePositionView = findPowerProfilePositionViewById(hourlyPositionView.getPowerProfilePositionId());
            PriceIndexSnapshot indexSnapshot = valuationIndexManager.getPriceIndex(powerProfilePositionView.getPriceIndexId());

            FxRate fxRate = null;
            if (indexSnapshot.getDetail().getCurrencyCode() != hourlyPositionView.getDetail().getCurrencyCode()) {
                fxRate = hourlyPositionView.getFxRate(valuationIndexManager);
            }

            Conversion conversion = null;
            if (indexSnapshot.getDetail().getUnitOfMeasureCode()  != hourlyPositionView.getDetail().getUnitOfMeasure()) {
                conversion = UnitOfMeasureConverter.findConversion(
                        hourlyPositionView.getDetail().getUnitOfMeasure(),
                        indexSnapshot.getDetail().getUnitOfMeasureCode());
            }

            for (int i=1; i <25; i++) {
                Integer priceRiskFactorId = powerProfilePositionView.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i);
                if (priceRiskFactorId != null) {
                    totalPriceCount++;

                    PriceRiskFactorSnapshot factorSnapshot = valuationIndexManager.getPriceRiskFactor(priceRiskFactorId);
                    Price unconvertedPrice = new Price(
                            factorSnapshot.getDetail().getValue(),
                            indexSnapshot.getDetail().getCurrencyCode(),
                            indexSnapshot.getDetail().getUnitOfMeasureCode());
                    if (fxRate != null)
                        unconvertedPrice = unconvertedPrice.apply(fxRate);

                    if (conversion != null)
                        unconvertedPrice = unconvertedPrice.apply(conversion);

                    if (hasVariableQuantity) {
                        BigDecimal value = quantitiesView.getHourFixedValueDayDetail().getHourFixedValue(i);
                        Quantity quantity = new Quantity(
                                value,
                                dealPositionView.getViewDetail().getVolumeUnitOfMeasure());
                        totalQuantiy = totalQuantiy.add(quantity);
                        Amount amount = quantity.multiply(unconvertedPrice);
                        totalAmount = totalAmount.add(amount);
                    } else {
                        totalPrice = totalPrice.add(unconvertedPrice);
                    }

                    hourlyPositionView.getHourFixedValueDayDetail().setHourFixedValue(i, unconvertedPrice.roundPrice().getValue());
                }
            }

            valuationResult.addHourlyPositionResult(
                    new HourlyPositionValuationResult(
                            hourlyPositionView.getId(),
                            hourlyPositionView.getHourFixedValueDayDetail(),
                            currentDateTime));
        }

        Price indexPrice;
        if (totalPrice.isInError())
            logger.error("market price is in error.");

        if (totalPriceCount == 0)
            logger.error("count is 0.");

        if (hasVariableQuantity) {
            indexPrice = totalAmount.divide(totalQuantiy);
        } else {
            indexPrice = totalPrice.divide(BigDecimal.valueOf(totalPriceCount));
        }
        return indexPrice.roundPrice();
    }

    protected HourFixedValueDayDetail calculateHourlyIndexPrices(
            FxRate fxRate,
            DealHourlyPositionView hourlyPositionView) {

        CurrencyCode targetCurrencyCode = dealPositionView.getViewDetail().getCurrencyCode();
        UnitOfMeasureCode targetUnitOfMeasure = dealPositionView.getViewDetail().getVolumeUnitOfMeasure();

        if (hourlyPositionView == null)
            return null;

        HourFixedValueDayDetail dealPriceHourlyDetail = new HourFixedValueDayDetail();
        for (int i = 1; i < 25;i++) {
            Price price = hourlyPositionView.getPrice(i, valuationIndexManager);
            if (price != null) {
                if (price.getCurrency() != targetCurrencyCode) {
                    price = price.apply(fxRate);
                }

                if (price.getUnitOfMeasure() != targetUnitOfMeasure) {
                    Conversion conversion = UnitOfMeasureConverter.findConversion(
                            targetUnitOfMeasure,
                            price.getUnitOfMeasure());
                    price = price.apply(conversion);
                }

                dealPriceHourlyDetail.setHourFixedValue(i, price.roundPrice().getValue());
            }
        }

        return dealPriceHourlyDetail;
    }

    protected Price getFixedPrice() {
        Price fixedPrice = dealPositionView.getFixedPrice();

        if (fixedPrice.getCurrency() != dealPositionView.getViewDetail().getCurrencyCode()) {
            FxRate fxRate = dealPositionView.getFixedFxRate(valuationIndexManager);

            if (fxRate == null) {
                logger.error("Fixed Price Fx rate is missing and is required.");
                logger.error("Fixed Price Currency: " + fixedPrice.getCurrency().getCode()
                        + " vs position view: " + dealPositionView.getViewDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            fixedPrice = fixedPrice.apply(fxRate);
        }

        if (fixedPrice.isInError())
            return fixedPrice;

        if (dealPositionView.getViewDetail().getVolumeUnitOfMeasure() != fixedPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    dealPositionView.getViewDetail().getVolumeUnitOfMeasure(),
                    fixedPrice.getUnitOfMeasure());
            fixedPrice = fixedPrice.apply(conversion);
        }
        return fixedPrice;
    }

    protected Price calculateWeightedAveragePrice(
            HourFixedValueDayDetail quantities,
            HourFixedValueDayDetail prices) {


        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                dealPositionView.getViewDetail().getVolumeUnitOfMeasure());

        Amount totalAmount = new Amount(
                BigDecimal.ZERO,
                dealPositionView.getViewDetail().getCurrencyCode());

        for (int i=1; i < 25; i++) {
            BigDecimal value = quantities.getHourFixedValue(i);
            if (value == null)
                continue;
            Quantity quantity = new Quantity(
                    value,
                    dealPositionView.getViewDetail().getVolumeUnitOfMeasure());

            totalQuantity = totalQuantity.add(quantity);

            Price price = new Price(
                    prices.getHourFixedValue(i),
                    dealPositionView.getViewDetail().getCurrencyCode(),
                    dealPositionView.getViewDetail().getVolumeUnitOfMeasure());

            Amount amount = quantity.multiply(price);
            totalAmount = totalAmount.add(amount);
        }

        // convert unit of measure

        if (totalQuantity.isZero() == false) {
            Price averagePrice = totalAmount.divide(totalQuantity);
            return averagePrice.roundPrice();
        } else {
            return new Price(
                    BigDecimal.ZERO,
                    dealPositionView.getViewDetail().getCurrencyCode(),
                    dealPositionView.getViewDetail().getVolumeUnitOfMeasure());
        }

    }

    protected Price calculateSimpleAveragePrice(HourFixedValueDayDetail prices) {

        Price totalPrice = new Price(
                BigDecimal.ZERO,
                dealPositionView.getViewDetail().getCurrencyCode(),
                dealPositionView.getViewDetail().getVolumeUnitOfMeasure());

        int count = 0;
        for (int i=1; i < 25; i++) {

            BigDecimal value = prices.getHourFixedValue(i);
            if (value == null)
                continue;
            count++;

            Price price = new Price(
                    value,
                    dealPositionView.getViewDetail().getCurrencyCode(),
                    dealPositionView.getViewDetail().getVolumeUnitOfMeasure());
            totalPrice = totalPrice.add(price);
        }

        if (count != 0) {
            Price averagePrice = totalPrice.divide(BigDecimal.valueOf(count));
            return averagePrice.roundPrice();
        } else {
            return new Price(
                    BigDecimal.ZERO,
                    dealPositionView.getViewDetail().getCurrencyCode(),
                    dealPositionView.getViewDetail().getVolumeUnitOfMeasure());
        }

    }

    protected DealHourlyPositionView getDealHourlyPositionView(PriceTypeCode priceTypeCode) {
        return  hourlyPositionViews
                .stream()
                .filter(c-> c.getDetail().getPriceTypeCode() == priceTypeCode)
                .findFirst()
                .orElse(null);
    }


    public PowerProfilePositionView findPowerProfilePositionViewById(Integer powerProfilePositionId) {
        return powerProfilePositionViews
                .stream()
                .filter(c -> c.getId().equals(powerProfilePositionId))
                .findFirst().orElse(null);
    }


}
