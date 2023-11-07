package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.onbelay.core.codes.annotations.CodeLabelSerializer;
import com.onbelay.core.codes.annotations.InjectCodeLabel;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;

import com.onbelay.shared.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DealPositionDetail extends AbstractDetail {

    private String dealTypeCodeValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createUpdateDateTime;
    private BigDecimal volumeQuantity;
    private String volumeUnitOfMeasureValue;
    private String frequencyCodeValue;

    private String currencyCodeValue;

    private BigDecimal markToMarketValuation;

    private String errorCode;


    public void setDefaults() {
        errorCode = "0";
    }

    public void validate() throws OBValidationException {

    }

    public void copyFrom(DealPositionDetail copy) {

        if (copy.dealTypeCodeValue != null)
            this.dealTypeCodeValue = copy.dealTypeCodeValue;

        if (copy.startDate != null)
            this.startDate = copy.startDate;

        if (copy.endDate != null)
            this.endDate = copy.endDate;

        if (copy.createUpdateDateTime != null)
            this.createUpdateDateTime = copy.createUpdateDateTime;

        if (copy.volumeQuantity != null)
            this.volumeQuantity = copy.volumeQuantity;

        if (copy.volumeUnitOfMeasureValue != null)
            this.volumeUnitOfMeasureValue = copy.volumeUnitOfMeasureValue;

        if (copy.frequencyCodeValue != null)
            this.frequencyCodeValue = copy.frequencyCodeValue;

        if (copy.markToMarketValuation != null)
            this.markToMarketValuation = copy.markToMarketValuation;

        if (copy.currencyCodeValue != null)
            this.currencyCodeValue = copy.currencyCodeValue;

        if (copy.errorCode != null)
            this.errorCode = copy.errorCode;
    }

    @Transient
    public DealTypeCode getDealTypeCode() {
        return DealTypeCode.lookUp(dealTypeCodeValue);
    }

    public void setDealTypeCode(DealTypeCode code) {
        this.dealTypeCodeValue = code.getCode();
    }

    @Column(name = "DEAL_TYPE_CODE")
    public String getDealTypeCodeValue() {
        return dealTypeCodeValue;
    }

    public void setDealTypeCodeValue(String dealTypeCodeValue) {
        this.dealTypeCodeValue = dealTypeCodeValue;
    }

    @Column(name = "START_DATE")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Column(name = "END_DATE")
    public LocalDate getEndDate() {
        return endDate;
    }

    @Column(name = "CREATE_UPDATE_DATETIME")
    public LocalDateTime getCreateUpdateDateTime() {
        return createUpdateDateTime;
    }

    public void setCreateUpdateDateTime(LocalDateTime createUpdateDate) {
        this.createUpdateDateTime = createUpdateDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Column(name = "VOLUME_QUANTITY")
    public BigDecimal getVolumeQuantity() {
        return volumeQuantity;
    }

    public void setVolumeQuantity(BigDecimal volumeQuantity) {
        this.volumeQuantity = volumeQuantity;
    }

    @Transient
    @JsonIgnore
    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.lookUp(currencyCodeValue);
    }

    public void setCurrencyCode(CurrencyCode code) {
        this.currencyCodeValue = code.getCode();
    }

    @Column(name = "CURRENCY_CODE")
    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public void setCurrencyCodeValue(String currencyCodeValue) {
        this.currencyCodeValue = currencyCodeValue;
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getVolumeUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(volumeUnitOfMeasureValue);
    }

    public void setVolumeUnitOfMeasure(UnitOfMeasureCode code) {
        this.volumeUnitOfMeasureValue = code.getCode();
    }

    @Column(name = "VOLUME_UOM_CODE")
    @InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "volumeUnitOfMeasureCodeItem")
    @JsonSerialize(using = CodeLabelSerializer.class)
    public String getVolumeUnitOfMeasureValue() {
        return volumeUnitOfMeasureValue;
    }

    public void setVolumeUnitOfMeasureValue(String volumeUnitOfMeasureValue) {
        this.volumeUnitOfMeasureValue = volumeUnitOfMeasureValue;
    }

    @Transient
    @JsonIgnore
    public FrequencyCode getFrequencyCode() {
        return FrequencyCode.lookUp(frequencyCodeValue);
    }

    public void setFrequencyCode(FrequencyCode code) {
        this.frequencyCodeValue = code.getCode();
    }

    @Column(name = "FREQUENCY_CODE")
    @InjectCodeLabel(codeFamily = "frequencyCode", injectedPropertyName = "frequencyCodeItem")
    @JsonSerialize(using = CodeLabelSerializer.class)
    public String getFrequencyCodeValue() {
        return frequencyCodeValue;
    }

    public void setFrequencyCodeValue(String frequencyCodeValue) {
        this.frequencyCodeValue = frequencyCodeValue;
    }

    @Column(name = "MTM_VALUATION")
    public BigDecimal getMarkToMarketValuation() {
        return markToMarketValuation;
    }

    public void setMarkToMarketValuation(BigDecimal markToMarketValuation) {
        this.markToMarketValuation = markToMarketValuation;
    }

    @Column(name = "ERROR_CODE")
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
