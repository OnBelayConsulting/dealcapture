package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
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

    public PositionRiskFactorMappingSummary(
            Integer id,
            Integer dealPositionId,
            String priceTypeCodeValue,
            String currencyCodeValue,
            String unitOfMeasureCodeValue,
            BigDecimal priceRiskFactorValue,
            BigDecimal fxRiskFactorValue,
            String toCurrencyCodeValue,
            String fromCurrencyCodeValue) {

        this.id = id;
        this.dealPositionId =  dealPositionId;
        this.priceTypeCode = PriceTypeCode.lookUp(priceTypeCodeValue);
        this.currencyCode = CurrencyCode.lookUp(currencyCodeValue);
        this.unitOfMeasureCode = UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
        this.priceRiskFactorValue = priceRiskFactorValue;
        this.fxRiskFactorValue = fxRiskFactorValue;
        this.toCurrencyCode = CurrencyCode.lookUp(toCurrencyCodeValue);
        this.fromCurrencyCode = CurrencyCode.lookUp(fromCurrencyCodeValue);
    }

    public Integer getId() {
        return id;
    }

    public Price calculateConvertedPrice(
            CurrencyCode targetCurrencyCode,
            UnitOfMeasureCode targetUnitOfMeasureCode) {

        Price price = new Price(
                priceRiskFactorValue,
                currencyCode,
                unitOfMeasureCode);

        if (this.unitOfMeasureCode != targetUnitOfMeasureCode) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    targetUnitOfMeasureCode,
                    this.unitOfMeasureCode);
            price = price.apply(conversion);
        }

        // Check to see that correct currency conversion is set up.
        if (targetCurrencyCode != this.currencyCode) {
            if (fxRiskFactorValue == null) {
                throw new OBRuntimeException(PositionErrorCode.ERROR_MISSING_FX_RATE_CONVERSION.getCode());
            } else {
                if (targetCurrencyCode != this.toCurrencyCode)
                    throw new OBRuntimeException(PositionErrorCode.ERROR_MISSING_FX_RATE_CONVERSION.getCode());
                FxRate rate = new FxRate(fxRiskFactorValue, this.toCurrencyCode, this.fromCurrencyCode);
                if (currencyCode != toCurrencyCode)
                    rate = rate.getInversion();
                price.apply(rate);
            }
        }

        if (fxRiskFactorValue != null) {
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
}
