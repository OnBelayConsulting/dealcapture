package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PhysicalDealSummary extends DealSummary {

    private ValuationCode DealPriceValuationCode;
    Integer dealPriceIndexId;
    private BigDecimal dealPriceValue;
    private UnitOfMeasureCode dealPriceUnitOfMeasureCode;
    private CurrencyCode dealPriceCurrencyCode;

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
            String dealPriceValuationCodeValue,
            Integer dealPriceIndexId,
            BigDecimal dealPriceValue,
            String dealPriceUnitOfMeasureCodeValue,
            String dealPriceCurrencyCodeValue,
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
                volumeUnitOfMeasureCodeValue);
        this.DealPriceValuationCode = ValuationCode.lookUp(dealPriceValuationCodeValue);
        this.dealPriceIndexId = dealPriceIndexId;
        this.dealPriceValue = dealPriceValue;
        this.dealPriceCurrencyCode = CurrencyCode.lookUp(dealPriceCurrencyCodeValue);
        this.dealPriceUnitOfMeasureCode = UnitOfMeasureCode.lookUp(dealPriceUnitOfMeasureCodeValue);
        this.marketValuationCode = ValuationCode.lookUp(marketValuationCodeValue);
        this.marketIndexId = marketIndexId;
    }

    public ValuationCode getDealPriceValuationCode() {
        return DealPriceValuationCode;
    }

    public Integer getDealPriceIndexId() {
        return dealPriceIndexId;
    }

    public BigDecimal getDealPriceValue() {
        return dealPriceValue;
    }

    public UnitOfMeasureCode getDealPriceUnitOfMeasureCode() {
        return dealPriceUnitOfMeasureCode;
    }

    public CurrencyCode getDealPriceCurrencyCode() {
        return dealPriceCurrencyCode;
    }

    public ValuationCode getMarketValuationCode() {
        return marketValuationCode;
    }

    public Integer getMarketIndexId() {
        return marketIndexId;
    }
}
