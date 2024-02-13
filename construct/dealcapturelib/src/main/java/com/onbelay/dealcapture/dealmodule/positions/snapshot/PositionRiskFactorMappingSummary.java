package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;

public class PositionRiskFactorMappingSummary {

    private Integer id;
    private Integer dealPositionId;
    private PriceTypeCode priceTypeCode;
    private CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode;
    private BigDecimal priceRiskFactorValue;
    private BigDecimal fxRiskFactorValue;
    private CurrencyCode toCurrencyCode;
    private CurrencyCode fromCurrencyCode;
    private BigDecimal unitOfMeasureValue;

    public PositionRiskFactorMappingSummary(
            Integer id,
            Integer dealPositionId,
            String priceTypeCodeValue,
            String currencyCodeValue,
            String unitOfMeasureCodeValue,
            BigDecimal priceRiskFactorValue,
            BigDecimal fxRiskFactorValue,
            String toCurrencyCodeValue,
            String fromCurrencyCodeValue,
            BigDecimal unitOfMeasureValue) {

        this.id = id;
        this.dealPositionId =  dealPositionId;
        this.priceTypeCode = PriceTypeCode.lookUp(priceTypeCodeValue);
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

    public Integer getDealPositionId() {
        return dealPositionId;
    }

    public PriceTypeCode getPriceTypeCode() {
        return priceTypeCode;
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
