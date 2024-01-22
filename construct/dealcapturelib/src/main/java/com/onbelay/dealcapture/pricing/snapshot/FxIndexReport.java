package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;

public class FxIndexReport {

    private Integer id;
    private FrequencyCode frequencyCode;
    private CurrencyCode toCurrencyCode;
    private CurrencyCode fromCurrencyCode;

    public FxIndexReport(
            Integer id,
            String frequencyCodeValue,
            String toCurrencyCodeValue,
            String fromCurrencyCodeValue) {

        this.id = id;
        this.frequencyCode = FrequencyCode.lookUp(frequencyCodeValue);
        this.toCurrencyCode = CurrencyCode.lookUp(toCurrencyCodeValue);
        this.fromCurrencyCode = CurrencyCode.lookUp(fromCurrencyCodeValue);
    }


    public Integer getId() {
        return id;
    }

    public CurrencyCode getToCurrencyCode() {
        return toCurrencyCode;
    }

    public CurrencyCode getFromCurrencyCode() {
        return fromCurrencyCode;
    }

    public FrequencyCode getFrequencyCode() {
        return frequencyCode;
    }
}
