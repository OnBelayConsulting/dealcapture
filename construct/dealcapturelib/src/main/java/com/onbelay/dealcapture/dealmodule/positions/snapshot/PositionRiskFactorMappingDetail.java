package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

public class PositionRiskFactorMappingDetail extends AbstractDetail {

    private String priceTypeCodeValue;

    private BigDecimal unitOfMeasureConversion;

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

    @Column(name = "UOM_CONVERSION")
    public BigDecimal getUnitOfMeasureConversion() {
        return unitOfMeasureConversion;
    }

    public void setUnitOfMeasureConversion(BigDecimal unitOfMeasureConversion) {
        this.unitOfMeasureConversion = unitOfMeasureConversion;
    }

    public void copyFrom(PositionRiskFactorMappingDetail copy) {
        if (copy.priceTypeCodeValue != null)
            this.priceTypeCodeValue = copy.priceTypeCodeValue;

        if (copy.unitOfMeasureConversion != null)
            this.unitOfMeasureConversion = copy.unitOfMeasureConversion;
    }
}
