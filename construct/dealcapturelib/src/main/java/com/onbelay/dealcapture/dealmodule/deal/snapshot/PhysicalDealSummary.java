package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PhysicalDealSummary extends DealSummary {

    private ValuationCode DealPriceValuationCode;
    Integer dealPriceIndexId;
    private BigDecimal fixedPriceValue;
    private UnitOfMeasureCode fixedPriceUnitOfMeasureCode;
    private CurrencyCode fixedPriceCurrencyCode;

    private ValuationCode marketValuationCode;
    Integer marketIndexId;


    public PhysicalDealSummary(
            Integer dealId,
            String ticketNo,
            LocalDate startDate,
            LocalDate endDate,
            String dealTypeCodeValue,
            String buySellCodeValue,
            String reportingCurrencyCodeValue,
            BigDecimal volumeQuantity,
            String volumeUnitOfMeasureCodeValue,
            String volumeFrequencyCodeValue,
            String settlementCurrencyCodeValue,
            String dealPriceValuationCodeValue,
            Integer dealPriceIndexId,
            BigDecimal fixedPriceValue,
            String fixedPriceUnitOfMeasureCodeValue,
            String fixedPriceCurrencyCodeValue,
            String marketValuationCodeValue,
            Integer marketIndexId) {

        super(
                dealId,
                ticketNo,
                startDate,
                endDate,
                dealTypeCodeValue,
                buySellCodeValue,
                reportingCurrencyCodeValue,
                volumeQuantity,
                volumeUnitOfMeasureCodeValue,
                volumeFrequencyCodeValue,
                settlementCurrencyCodeValue);

        this.DealPriceValuationCode = ValuationCode.lookUp(dealPriceValuationCodeValue);
        this.dealPriceIndexId = dealPriceIndexId;
        this.fixedPriceValue = fixedPriceValue;
        this.fixedPriceCurrencyCode = CurrencyCode.lookUp(fixedPriceCurrencyCodeValue);
        this.fixedPriceUnitOfMeasureCode = UnitOfMeasureCode.lookUp(fixedPriceUnitOfMeasureCodeValue);
        this.marketValuationCode = ValuationCode.lookUp(marketValuationCodeValue);
        this.marketIndexId = marketIndexId;
    }

    public ValuationCode getDealPriceValuationCode() {
        return DealPriceValuationCode;
    }

    public Integer getDealPriceIndexId() {
        return dealPriceIndexId;
    }

    public BigDecimal getFixedPriceValue() {
        return fixedPriceValue;
    }

    public UnitOfMeasureCode getFixedPriceUnitOfMeasureCode() {
        return fixedPriceUnitOfMeasureCode;
    }

    public CurrencyCode getFixedPriceCurrencyCode() {
        return fixedPriceCurrencyCode;
    }

    public ValuationCode getMarketValuationCode() {
        return marketValuationCode;
    }

    public Integer getMarketIndexId() {
        return marketIndexId;
    }
}
