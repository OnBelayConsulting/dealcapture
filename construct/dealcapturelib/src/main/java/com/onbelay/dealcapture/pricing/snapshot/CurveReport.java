package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.shared.enums.FrequencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A projection of curve values for use in price/rate discovery.
 */
public class CurveReport {
    private Integer indexId;
    private LocalDate curveDate;
    private BigDecimal value;
    private FrequencyCode frequencyCode;

    public CurveReport(
            Integer indexId,
            LocalDate curveDate,
            BigDecimal value,
            String frequencyCodeValue) {

        this.indexId = indexId;
        this.curveDate = curveDate;
        this.value = value;
        this.frequencyCode = FrequencyCode.lookUp(frequencyCodeValue);
    }

    public Integer getIndexId() {
        return indexId;
    }

    public LocalDate getCurveDate() {
        return curveDate;
    }

    public FrequencyCode getFrequencyCode() {
        return frequencyCode;
    }

    public BigDecimal getValue() {
        return value;
    }
}
