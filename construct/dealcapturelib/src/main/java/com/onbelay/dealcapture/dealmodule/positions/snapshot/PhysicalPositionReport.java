package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhysicalPositionReport {

    private Integer dealId;
    private String buySellCodeValue;
    private Integer dealPositionId;
    private BigDecimal volumeQuantityValue;
    private String volumeUnitOfMeasure;
    private String currencyCodeValue;

    private String dealPriceValuationValue;
    private String marketPriceValuationValue;

    private BigDecimal fixedPriceValue;
    private String fixedPriceFromCurrencyCodeValue;
    private BigDecimal fixedPriceFxValue;

    private String fixedPriceUnitOfMeasureCodeValue;

    private BigDecimal dealPriceIndexValue;
    private String dealPriceFromCurrencyCodeValue;
    private String dealPriceUnitOfMeasureCodeValue;

    private BigDecimal dealPriceIndexFxValue;

    private BigDecimal marketPriceIndexValue;
    private String marketPriceFromCurrencyCodeValue;
    private String marketPriceUnitOfMeasureCodeValue;

    private BigDecimal marketPriceIndexFxValue;

    private List<PositionRiskFactorMappingSummary> mappingSummaries = new ArrayList<>();

    public PhysicalPositionReport(
            Integer dealId,
            String buySellCodeValue,
            Integer dealPositionId,
            BigDecimal volumeQuantityValue,
            String volumeUnitOfMeasure,
            String currencyCodeValue,
            String dealPriceValuationValue,
            BigDecimal fixedPriceValue,
            String fixedPriceFromCurrencyCodeValue,
            BigDecimal fixedPriceFxValue,
            String fixedPriceUnitOfMeasureCodeValue,
            BigDecimal dealPriceIndexValue,
            String dealPriceFromCurrencyCodeValue,
            String dealPriceUnitOfMeasureCodeValue,
            BigDecimal dealPriceIndexFxValue,
            String marketPriceValuationValue,
            BigDecimal marketPriceIndexValue,
            String marketPriceFromCurrencyCodeValue,
            String marketPriceUnitOfMeasureCodeValue,
            BigDecimal marketPriceIndexFxValue) {

        this.dealId = dealId;
        this.buySellCodeValue = buySellCodeValue;
        this.dealPriceValuationValue = dealPriceValuationValue;
        this.marketPriceValuationValue = marketPriceValuationValue;
        this.dealPositionId = dealPositionId;
        this.volumeQuantityValue = volumeQuantityValue;
        this.volumeUnitOfMeasure = volumeUnitOfMeasure;
        this.currencyCodeValue = currencyCodeValue;
        this.fixedPriceValue = fixedPriceValue;
        this.fixedPriceFromCurrencyCodeValue = fixedPriceFromCurrencyCodeValue;
        this.fixedPriceFxValue = fixedPriceFxValue;
        this.fixedPriceUnitOfMeasureCodeValue = fixedPriceUnitOfMeasureCodeValue;
        this.dealPriceIndexValue = dealPriceIndexValue;
        this.dealPriceFromCurrencyCodeValue = dealPriceFromCurrencyCodeValue;
        this.dealPriceUnitOfMeasureCodeValue = dealPriceUnitOfMeasureCodeValue;
        this.dealPriceIndexFxValue = dealPriceIndexFxValue;
        this.marketPriceIndexValue = marketPriceIndexValue;
        this.marketPriceFromCurrencyCodeValue = marketPriceFromCurrencyCodeValue;
        this.marketPriceUnitOfMeasureCodeValue = marketPriceUnitOfMeasureCodeValue;
        this.marketPriceIndexFxValue = marketPriceIndexFxValue;
    }

    public Integer getDealId() {
        return dealId;
    }

    public Integer getDealPositionId() {
        return dealPositionId;
    }

    public Quantity getVolumeQuantity() {
        return new Quantity(this.volumeQuantityValue, getVolumeUnitOfMeasureCode());
    }

    public BigDecimal getVolumeQuantityValue() {
        return volumeQuantityValue;
    }

    public UnitOfMeasureCode getVolumeUnitOfMeasureCode() {
        return UnitOfMeasureCode.lookUp(volumeUnitOfMeasure);
    }

    public String getVolumeUnitOfMeasure() {
        return volumeUnitOfMeasure;
    }

    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.lookUp(currencyCodeValue);
    }

    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public BigDecimal getFixedPriceValue() {
        return fixedPriceValue;
    }

    public Price getFixedPrice() {
        return new Price(
                fixedPriceValue,
                CurrencyCode.lookUp(fixedPriceFromCurrencyCodeValue),
                UnitOfMeasureCode.lookUp(fixedPriceUnitOfMeasureCodeValue));
    }

    public BigDecimal getFixedPriceFxValue() {
        return fixedPriceFxValue;
    }

    public String getFixedPriceUnitOfMeasureCodeValue() {
        return fixedPriceUnitOfMeasureCodeValue;
    }


    public Price getDealIndexPrice() {
        return new Price(
                dealPriceIndexValue,
                CurrencyCode.lookUp(dealPriceFromCurrencyCodeValue),
                UnitOfMeasureCode.lookUp(dealPriceUnitOfMeasureCodeValue));
    }


    public BigDecimal getDealPriceIndexValue() {
        return dealPriceIndexValue;
    }

    public String getDealPriceUnitOfMeasureCodeValue() {
        return dealPriceUnitOfMeasureCodeValue;
    }

    public BigDecimal getDealPriceIndexFxValue() {
        return dealPriceIndexFxValue;
    }


    public Price getMarketIndexPrice() {
        return new Price(
                marketPriceIndexValue,
                CurrencyCode.lookUp(marketPriceFromCurrencyCodeValue),
                UnitOfMeasureCode.lookUp(marketPriceUnitOfMeasureCodeValue));
    }


    public BigDecimal getMarketPriceIndexValue() {
        return marketPriceIndexValue;
    }

    public String getMarketPriceUnitOfMeasureCodeValue() {
        return marketPriceUnitOfMeasureCodeValue;
    }

    public BigDecimal getMarketPriceIndexFxValue() {
        return marketPriceIndexFxValue;
    }

    public BuySellCode getBuySellCode() {
        return BuySellCode.lookUp(buySellCodeValue);
    }

    public String getBuySellCodeValue() {
        return buySellCodeValue;
    }

    public String getFixedPriceFromCurrencyCodeValue() {
        return fixedPriceFromCurrencyCodeValue;
    }

    public String getDealPriceFromCurrencyCodeValue() {
        return dealPriceFromCurrencyCodeValue;
    }

    public String getMarketPriceFromCurrencyCodeValue() {
        return marketPriceFromCurrencyCodeValue;
    }

    public ValuationCode getDealPriceValuationCode() {
        return ValuationCode.lookUp(dealPriceValuationValue);
    }

    public ValuationCode getMarketPriceValuationCode() {
        return ValuationCode.lookUp(marketPriceValuationValue);
    }

    public List<PositionRiskFactorMappingSummary> getMappingSummaries() {
        return mappingSummaries;
    }

    public void addMappingSummary(PositionRiskFactorMappingSummary summary) {
        mappingSummaries.add(summary);
    }

    public void setMappingSummaries(List<PositionRiskFactorMappingSummary> mappingSummaries) {
        this.mappingSummaries = mappingSummaries;
    }

    public List<PositionRiskFactorMappingSummary> findMappingSummaries(PriceTypeCode priceTypeCode) {
        return mappingSummaries
                .stream()
                .filter( c-> c.getPriceTypeCode() == priceTypeCode)
                .collect(Collectors.toList());
    }
}
