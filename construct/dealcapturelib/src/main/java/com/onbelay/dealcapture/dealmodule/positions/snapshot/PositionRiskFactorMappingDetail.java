package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

public class PositionRiskFactorMappingDetail extends AbstractDetail {

    private String priceTypeCodeValue;

    @Transient
    @JsonIgnore
    public PriceTypeCode getPriceTypeCode() {
        return PriceTypeCode.lookUp(priceTypeCodeValue);
    }

    public void setPriceTypeCode(PriceTypeCode code) {
        this.priceTypeCodeValue = code.getCode();
    }

    @Column(name = "PRICE_TYPE_CODE")
    public String getPriceTypeCodeValue() {
        return priceTypeCodeValue;
    }

    public void setPriceTypeCodeValue(String priceTypeCodeValue) {
        this.priceTypeCodeValue = priceTypeCodeValue;
    }

    public void copyFrom(PositionRiskFactorMappingDetail copy) {
        if (copy.priceTypeCodeValue != null)
            this.priceTypeCodeValue = copy.priceTypeCodeValue;

    }
}
