package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FxCurveDiscoveryCalculator {
    private static final Logger logger = LogManager.getLogger();

    private ConcurrentHashMap<Integer, FxIndexCurveContainer> containerMap = new ConcurrentHashMap<>();

    public static FxCurveDiscoveryCalculator newCalculator() {
        return new FxCurveDiscoveryCalculator();
    }

    public FxCurveDiscoveryCalculator withIndices(List<FxIndexReport> indexReportList) {
        for (FxIndexReport report : indexReportList) {
            FxIndexCurveContainer existing = containerMap.get(report.getId());
            if (existing == null) {
                containerMap.put(report.getId(), newContainer(report));
            } else {
                if (existing.isHolding()) {
                    existing.mergeWith(
                            report.getFrequencyCode(),
                            report.getToCurrencyCode(),
                            report.getFromCurrencyCode());
                }
            }
        }
        return this;
    }

    public FxCurveDiscoveryCalculator withRates(List<CurveReport> reports) {
        for (CurveReport report : reports) {
            FxIndexCurveContainer container = containerMap.get(report.getIndexId());
            if (container == null) {
                logger.error("Attempt to load price reports for a missing index:" + report.getIndexId());
            } else {
                container.addRate(report);
            }

        }
        return this;
    }

    public FxRate calculateRate(Integer indexId, LocalDate curveDate) {
        FxIndexCurveContainer container = containerMap.get(indexId);
        if (container == null)
            throw new OBRuntimeException("error");
        return container.calculateRate(curveDate);
    }

    private FxIndexCurveContainer newContainer(FxIndexReport report) {
        FxIndexCurveContainer container =  FxIndexCurveContainer.newContainer(
                report.getId(),
                report.getFrequencyCode(),
                report.getToCurrencyCode(),
                report.getFromCurrencyCode());
        return container;
    }

}
