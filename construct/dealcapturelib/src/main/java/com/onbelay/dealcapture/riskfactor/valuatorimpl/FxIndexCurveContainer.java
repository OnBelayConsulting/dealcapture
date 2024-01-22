package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FxIndexCurveContainer {

    private boolean holding = false;
    private Integer indexId;
    private FrequencyCode frequencyCode;
    private CurrencyCode toCurrencyCode;
    private CurrencyCode fromCurrencyCode;

    private ConcurrentHashMap<LocalDate, FxRate> dailyPriceMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<LocalDate, FxRate> monthlyPriceMap = new ConcurrentHashMap<>();

    public static FxIndexCurveContainer newHoldingContainer(Integer fxIndexId) {
        return new FxIndexCurveContainer(fxIndexId);
    }

    public static FxIndexCurveContainer newContainer(
            Integer fxIndexId,
            FrequencyCode frequencyCode,
            CurrencyCode toCurrencyCode,
            CurrencyCode fromCurrencyCode) {

        return new FxIndexCurveContainer(
                fxIndexId,
                frequencyCode,
                toCurrencyCode,
                fromCurrencyCode);
    }

    public void mergeWith(
            FrequencyCode frequencyCode,
            CurrencyCode toCurrencyCode,
            CurrencyCode fromCurrencyCode) {
        this.frequencyCode = frequencyCode;
        this.fromCurrencyCode = fromCurrencyCode;
        this.toCurrencyCode = toCurrencyCode;
        holding = false;
    }

    public FxIndexCurveContainer(Integer indexId) {
        holding = true;
        this.indexId = indexId;
    }

    private FxIndexCurveContainer(
            Integer indexId,
            FrequencyCode frequencyCode,
            CurrencyCode toCurrencyCode,
            CurrencyCode fromCurrencyCode) {
        this.indexId = indexId;
        this.frequencyCode = frequencyCode;
        this.fromCurrencyCode = fromCurrencyCode;
        this.toCurrencyCode = toCurrencyCode;
    }

    public FxRate calculateRate(LocalDate curveDate) {
        if (this.frequencyCode == FrequencyCode.DAILY) {
            FxRate found = dailyPriceMap.get(curveDate);
            if (found != null)
                return found;
            LocalDate monthDate = curveDate.withDayOfMonth(1);
            FxRate monthly = monthlyPriceMap.get(monthDate);
            if (monthly != null)
                return monthly;

            return new FxRate(CalculatedErrorType.ERROR);
        } else {
            LocalDate monthDate = curveDate.withDayOfMonth(1);
            FxRate monthly = monthlyPriceMap.get(monthDate);
            if (monthly != null)
                return monthly;
            return new FxRate(CalculatedErrorType.ERROR);
        }
    }

    public void addRate(CurveReport report) {
        putRate(report);
    }

    public FxIndexCurveContainer withRates(List<CurveReport> reports) {
        reports.forEach( p-> putRate(p) );
        return this;
    }

    private void putRate(CurveReport report) {
        if (report.getFrequencyCode() == FrequencyCode.DAILY)
            dailyPriceMap.put(
                    report.getCurveDate(),
                    new FxRate(
                            report.getValue(),
                            toCurrencyCode,
                            fromCurrencyCode));
        else
            monthlyPriceMap.put(
                    report.getCurveDate(),
                    new FxRate(
                            report.getValue(),
                            toCurrencyCode,
                            fromCurrencyCode));

    }

    public Integer getIndexId() {
        return indexId;
    }

    public FrequencyCode getFrequencyCode() {
        return frequencyCode;
    }

    public boolean isHolding() {
        return holding;
    }

    public CurrencyCode getFromCurrencyCode() {
        return fromCurrencyCode;
    }

    public CurrencyCode getToCurrencyCode() {
        return toCurrencyCode;
    }
}
