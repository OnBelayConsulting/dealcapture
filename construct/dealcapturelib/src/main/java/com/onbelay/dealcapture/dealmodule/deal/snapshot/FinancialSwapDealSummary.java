package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialSwapDealSummary extends DealSummary {

    private ValuationCode paysValuationCode;
    Integer paysIndexId;
    private BigDecimal fixedPriceValue;
    private UnitOfMeasureCode fixedPriceUnitOfMeasureCode;
    private CurrencyCode fixedPriceCurrencyCode;

    private ValuationCode receivesValuationCode;
    Integer receivesIndexId;


    public FinancialSwapDealSummary(
            Integer dealId,
            Integer powerProfileId,
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
            String paysValuationCodeValue,
            Integer paysIndexId,
            BigDecimal fixedPriceValue,
            String fixedPriceUnitOfMeasureCodeValue,
            String fixedPriceCurrencyCodeValue,
            String receivesValuationCodeValue,
            Integer receivesIndexId) {

        super(
                dealId,
                powerProfileId,
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

        this.paysValuationCode = ValuationCode.lookUp(paysValuationCodeValue);
        this.paysIndexId = paysIndexId;
        this.fixedPriceValue = fixedPriceValue;
        this.fixedPriceCurrencyCode = CurrencyCode.lookUp(fixedPriceCurrencyCodeValue);
        this.fixedPriceUnitOfMeasureCode = UnitOfMeasureCode.lookUp(fixedPriceUnitOfMeasureCodeValue);
        this.receivesValuationCode = ValuationCode.lookUp(receivesValuationCodeValue);
        this.receivesIndexId = receivesIndexId;
    }

    public ValuationCode getPaysValuationCode() {
        return paysValuationCode;
    }

    public Integer getPaysIndexId() {
        return paysIndexId;
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

    public ValuationCode getReceivesValuationCode() {
        return receivesValuationCode;
    }

    public Integer getReceivesIndexId() {
        return receivesIndexId;
    }
}
