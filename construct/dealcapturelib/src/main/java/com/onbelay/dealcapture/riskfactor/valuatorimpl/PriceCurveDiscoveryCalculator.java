package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PriceCurveDiscoveryCalculator {
    private static final Logger logger = LogManager.getLogger();

    private ConcurrentHashMap<Integer, PriceIndexCurveContainer> containerMap = new ConcurrentHashMap<>();

    public static PriceCurveDiscoveryCalculator newCalculator() {
        return new PriceCurveDiscoveryCalculator();
    }

    public PriceCurveDiscoveryCalculator withIndices(List<PriceIndexReport> indexReportList) {
        for (PriceIndexReport report : indexReportList) {
            PriceIndexCurveContainer existing = containerMap.get(report.getId());
            if (existing == null) {
                containerMap.put(report.getId(), newContainer(report));
            } else {
                if (existing.isHolding()) {
                    existing.mergeWith(
                            report.getFrequencyCode(),
                            report.getCurrencyCode(),
                            report.getUnitOfMeasureCode());
                    if (report.getBenchmarkIndexId() != null) {
                        handleBenchmark(
                                existing,
                                report.getBenchmarkIndexId());
                    }
                }
            }
        }
        return this;
    }

    public PriceCurveDiscoveryCalculator withPrices(List<CurveReport> reports) {
        for (CurveReport report : reports) {
            PriceIndexCurveContainer container = containerMap.get(report.getIndexId());
            if (container == null) {
                logger.error("Attempt to load price reports for a missing index:" + report.getIndexId());
            } else {
                container.addPrice(report);
            }

        }
        return this;
    }

    public Price calculatePrice(
            Integer priceIndexId,
            LocalDate curveDate,
            int hourEnding) {
        PriceIndexCurveContainer container = containerMap.get(priceIndexId);

        if (container == null)
            throw new OBRuntimeException("error");

        return container.calculatePrice(
                curveDate,
                hourEnding);
    }

    private PriceIndexCurveContainer newContainer(PriceIndexReport report) {
        PriceIndexCurveContainer container =  PriceIndexCurveContainer.newContainer(
                report.getId(),
                report.getFrequencyCode(),
                report.getCurrencyCode(),
                report.getUnitOfMeasureCode());
        if (report.getBenchmarkIndexId() != null) {
            handleBenchmark(
                    container,
                    report.getBenchmarkIndexId());
        }
        return container;
    }

    private void handleBenchmark(
            PriceIndexCurveContainer container,
            Integer benchmarkId) {

        PriceIndexCurveContainer existing = containerMap.get(benchmarkId);
        if (existing != null) {
            container.withBenchmark(existing);
        } else {
            PriceIndexCurveContainer benchmark = PriceIndexCurveContainer.newHoldingContainer(benchmarkId);
            containerMap.put(benchmarkId, benchmark);
            container.withBenchmark(benchmark);
        }

    }
}
