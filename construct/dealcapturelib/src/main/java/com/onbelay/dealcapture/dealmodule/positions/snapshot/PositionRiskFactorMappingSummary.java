package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.math.MathContext;

public class PositionRiskFactorMappingSummary {

    private Integer id;
    private CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode;
    private BigDecimal priceRiskFactorValue;
    private BigDecimal fxRiskFactorValue;
    private CurrencyCode toCurrencyCode;
    private CurrencyCode fromCurrencyCode;
    private BigDecimal unitOfMeasureValue;

    public PositionRiskFactorMappingSummary(
            Integer id,
            String currencyCodeValue,
            String unitOfMeasureCodeValue,
            BigDecimal priceRiskFactorValue,
            BigDecimal fxRiskFactorValue,
            String toCurrencyCodeValue,
            String fromCurrencyCodeValue,
            BigDecimal unitOfMeasureValue) {

        this.id = id;
        this.currencyCode = CurrencyCode.lookUp(currencyCodeValue);
        this.unitOfMeasureCode = UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
        this.priceRiskFactorValue = priceRiskFactorValue;
        this.fxRiskFactorValue = fxRiskFactorValue;
        this.toCurrencyCode = CurrencyCode.lookUp(toCurrencyCodeValue);
        this.fromCurrencyCode = CurrencyCode.lookUp(fromCurrencyCodeValue);
        this.unitOfMeasureValue = unitOfMeasureValue;
    }

    public Integer getId() {
        return id;
    }

    public Price calculateConvertedPrice(
            CurrencyCode targetCurrencyCode,
            UnitOfMeasureCode unitOfMeasureCode) {

        Price price = new Price(
                priceRiskFactorValue,
                currencyCode,
                unitOfMeasureCode);

        price = price.multiply(unitOfMeasureValue);
        if (fxRiskFactorValue != null) {
            FxRate rate = new FxRate(fxRiskFactorValue, this.toCurrencyCode, this.fromCurrencyCode);
            if (currencyCode != toCurrencyCode)
                rate = rate.getInversion();
            price.apply(rate);
        }

        return price;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public UnitOfMeasureCode getUnitOfMeasureCode() {
        return unitOfMeasureCode;
    }

    public BigDecimal getPriceRiskFactorValue() {
        return priceRiskFactorValue;
    }

    public BigDecimal getFxRiskFactorValue() {
        return fxRiskFactorValue;
    }

    public BigDecimal getUnitOfMeasureValue() {
        return unitOfMeasureValue;
    }
}
