package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriceIndexCurveContainer {

    private boolean holding = false;
    private Integer indexId;
    private FrequencyCode frequencyCode;
    private CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode;

    private PriceIndexCurveContainer benchmarkContainer;

    private ConcurrentHashMap<LocalDate, Map<Integer, Price>> hourlyByDayPriceMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<LocalDate, Price> dailyPriceMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<LocalDate, Price> monthlyPriceMap = new ConcurrentHashMap<>();

    public static PriceIndexCurveContainer newHoldingContainer(Integer priceIndexId) {
        return new PriceIndexCurveContainer(priceIndexId);
    }

    public static PriceIndexCurveContainer newContainer(
            Integer priceIndexId,
            FrequencyCode frequencyCode,
            CurrencyCode currencyCode,
            UnitOfMeasureCode unitOfMeasureCode) {

        return new PriceIndexCurveContainer(
                priceIndexId,
                frequencyCode,
                currencyCode,
                unitOfMeasureCode);
    }

    public void mergeWith(
            FrequencyCode frequencyCode,
            CurrencyCode currencyCode,
            UnitOfMeasureCode unitOfMeasureCode) {
        this.frequencyCode = frequencyCode;
        this.currencyCode = currencyCode;
        this.unitOfMeasureCode = unitOfMeasureCode;
        holding = false;
    }

    public PriceIndexCurveContainer(Integer indexId) {
        holding = true;
        this.indexId = indexId;
    }

    private PriceIndexCurveContainer(
            Integer indexId,
            FrequencyCode frequencyCode,
            CurrencyCode currencyCode,
            UnitOfMeasureCode unitOfMeasureCode) {
        this.indexId = indexId;
        this.frequencyCode = frequencyCode;
        this.currencyCode = currencyCode;
        this.unitOfMeasureCode = unitOfMeasureCode;
    }

    public PriceIndexCurveContainer withBenchmark(PriceIndexCurveContainer benchmarkContainer) {
        this.benchmarkContainer = benchmarkContainer;
        return this;
    }

    public Price calculatePrice(
            LocalDate curveDate,
            int hourEnding) {

        return calculatePriceByFrequency(
                curveDate,
                hourEnding,
                curveDate,
                frequencyCode);

    }


    public Price calculatePrice(LocalDate curveDate) {
        return calculatePriceByFrequency(
                curveDate,
                0,
                curveDate,
                frequencyCode);
    }

    private Price calculatePriceByFrequency(
            LocalDate curveDate,
            int hourEnding,
            LocalDate searchDate,
            FrequencyCode frequencyCodeIn) {

        switch (frequencyCodeIn) {

            case HOURLY -> {
                Map<Integer, Price> priceMap = hourlyByDayPriceMap.get(searchDate);
                if (priceMap == null) {
                    return calculatePriceByFrequency(
                            curveDate,
                            hourEnding,
                            searchDate,
                            FrequencyCode.DAILY);
                } else {
                    Price price = priceMap.get(hourEnding);
                    if (price != null)
                        return price;
                    else
                        return calculatePriceByFrequency(
                                curveDate,
                                hourEnding,
                                searchDate,
                                FrequencyCode.DAILY);
                }

            }

            case DAILY -> {
                Price found = dailyPriceMap.get(searchDate);
                if (found != null)
                    return found;
                LocalDate monthDate = curveDate.withDayOfMonth(1);
                return calculatePriceByFrequency(
                        curveDate,
                        hourEnding,
                        monthDate,
                        FrequencyCode.MONTHLY);
            }
            case MONTHLY -> {
                LocalDate monthDate = searchDate.withDayOfMonth(1);
                Price monthly = monthlyPriceMap.get(monthDate);
                if (monthly != null)
                    return monthly;
                if (benchmarkContainer != null)
                    return benchmarkContainer.calculatePrice(
                            curveDate,
                            hourEnding);

                return new Price(CalculatedErrorType.ERROR);
            }
        }
        return new Price(CalculatedErrorType.ERROR);
    }

    public void addPrice(CurveReport report) {
        putPrice(report);
    }

    public PriceIndexCurveContainer withPrices(List<CurveReport> reports) {
        reports.forEach( p-> putPrice(p) );
        return this;
    }

    private void putPrice(CurveReport report) {

        switch (report.getFrequencyCode()) {

            case HOURLY -> {
                Map<Integer, Price> priceMap = hourlyByDayPriceMap.computeIfAbsent(
                        report.getCurveDate(),
                        k -> new ConcurrentHashMap<>());
                priceMap.put(
                        report.getHourEnding(),
                        new Price(report.getValue(),
                            currencyCode,
                            unitOfMeasureCode));

            }

            case DAILY -> {
                dailyPriceMap.put(
                        report.getCurveDate(),
                        new Price(report.getValue(),
                                currencyCode,
                                unitOfMeasureCode));
            }

            case MONTHLY -> {
                monthlyPriceMap.put(
                        report.getCurveDate(),
                        new Price(report.getValue(),
                                currencyCode,
                                unitOfMeasureCode));
            }

        }

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
}
