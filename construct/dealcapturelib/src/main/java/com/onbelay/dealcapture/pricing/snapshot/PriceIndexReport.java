package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

public class PriceIndexReport {

    private Integer id;
    private IndexType indexType;
    private Integer benchmarkIndexId;
    private Integer basisIndexId;
    private FrequencyCode frequencyCode;
    private CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode;

    public PriceIndexReport(
            Integer id,
            String indexTypeValue,
            Integer benchmarkIndexId,
            Integer basisIndexId,
            String frequencyCodeValue,
            String currencyCodeValue,
            String unitOfMeasureCodeValue) {

        this.id = id;
        this.indexType = IndexType.lookUp(indexTypeValue);
        this.benchmarkIndexId = benchmarkIndexId;
        this.basisIndexId = basisIndexId;
        this.frequencyCode = FrequencyCode.lookUp(frequencyCodeValue);
        this.currencyCode = CurrencyCode.lookUp(currencyCodeValue);
        this.unitOfMeasureCode = UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
    }

    public PriceIndexReport(
            Integer id,
            String frequencyCodeValue,
            String currencyCodeValue,
            String unitOfMeasureCodeValue) {

        this.id = id;
        this.indexType = IndexType.HUB;
        this.frequencyCode = FrequencyCode.lookUp(frequencyCodeValue);
        this.currencyCode = CurrencyCode.lookUp(currencyCodeValue);
        this.unitOfMeasureCode = UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
    }


    public Integer getId() {
        return id;
    }



    public IndexType getIndexType() {
        return indexType;
    }

    public Integer getBenchmarkIndexId() {
        return benchmarkIndexId;
    }

    public Integer getBasisIndexId() {
        return basisIndexId;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public UnitOfMeasureCode getUnitOfMeasureCode() {
        return unitOfMeasureCode;
    }

    public FrequencyCode getFrequencyCode() {
        return frequencyCode;
    }
}
