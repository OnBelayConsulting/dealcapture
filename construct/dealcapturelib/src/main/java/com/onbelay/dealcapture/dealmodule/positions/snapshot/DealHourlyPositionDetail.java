package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.onbelay.core.codes.annotations.CodeLabelSerializer;
import com.onbelay.core.codes.annotations.InjectCodeLabel;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Transient;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealHourlyPositionDetail extends AbstractDetail {

    private LocalDate startDate;
    private LocalDate endDate;
    private String powerFlowCodeValue;
    private String priceTypeCodeValue;

    private LocalDateTime createdDateTime;
    private LocalDateTime valuedDateTime;

    private String currencyCodeValue;
    private String unitOfMeasureValue;
    private Integer basisNo;
    private String indexTypeCodeValue;

    private Boolean isSettlementPosition;

    private String errorCode;
    private String errorMessage;


    public void setDefaults() {
        errorCode = "0";
    }

    public void validate() throws OBValidationException {

    }

    public void copyFrom(DealHourlyPositionDetail copy) {

        if (copy.startDate != null)
            this.startDate = copy.startDate;

        if (copy.endDate != null)
            this.endDate = copy.endDate;

        if (copy.powerFlowCodeValue != null)
            this.powerFlowCodeValue = copy.powerFlowCodeValue;

        if (copy.priceTypeCodeValue != null)
            this.priceTypeCodeValue = copy.priceTypeCodeValue;

        if (copy.createdDateTime != null)
            this.createdDateTime = copy.createdDateTime;

        if (copy.valuedDateTime != null)
            this.valuedDateTime = copy.valuedDateTime;

        if (copy.unitOfMeasureValue != null)
            this.unitOfMeasureValue = copy.unitOfMeasureValue;

        if (copy.currencyCodeValue != null)
            this.currencyCodeValue = copy.currencyCodeValue;

        if (copy.isSettlementPosition != null)
            this.isSettlementPosition = copy.isSettlementPosition;

        if (copy.basisNo != null)
            this.basisNo = copy.basisNo;

        if (copy.indexTypeCodeValue != null)
            this.indexTypeCodeValue = copy.indexTypeCodeValue;

        if (copy.errorCode != null)
            this.errorCode = copy.errorCode;

        if (copy.errorMessage != null)
            this.errorMessage = copy.errorMessage;

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
    @Transient
    @JsonIgnore
    public PowerFlowCode getPowerFlowCode() {
        return PowerFlowCode.lookUp(powerFlowCodeValue);
    }

    public void setPowerFlowCode(PowerFlowCode powerFlowCode) {
        this.powerFlowCodeValue = powerFlowCode.getCode();
    }

    @Column(name = "POWER_FLOW_CODE")
    public String getPowerFlowCodeValue() {
        return powerFlowCodeValue;
    }

    public void setPowerFlowCodeValue(String powerFlowCodeValue) {
        this.powerFlowCodeValue = powerFlowCodeValue;
    }

    @Transient
    @JsonIgnore
    public PriceTypeCode getPriceTypeCode() {
        return PriceTypeCode.lookUp(priceTypeCodeValue);
    }

    public void setPriceTypeCode(PriceTypeCode priceTypeCode) {
        this.priceTypeCodeValue = priceTypeCode.getCode();
    }

    @Column(name = "PRICE_TYPE_CODE")
    public String getPriceTypeCodeValue() {
        return priceTypeCodeValue;
    }

    public void setPriceTypeCodeValue(String priceTypeCodeValue) {
        this.priceTypeCodeValue = priceTypeCodeValue;
    }


    @Column(name = "BASIS_NO")
    public Integer getBasisNo() {
        return basisNo;
    }

    public void setBasisNo(Integer basisNo) {
        this.basisNo = basisNo;
    }

    @Transient
    @JsonIgnore
    public IndexType getIndexTypeCode() {
        return IndexType.lookUp(indexTypeCodeValue);
    }

    public void setIndexTypeCode(IndexType indexTypeCode) {
        this.indexTypeCodeValue = indexTypeCode.getCode();
    }

    @Column(name = "INDEX_TYPE_CODE")
    public String getIndexTypeCodeValue() {
        return indexTypeCodeValue;
    }

    public void setIndexTypeCodeValue(String indexTypeCodeValue) {
        this.indexTypeCodeValue = indexTypeCodeValue;
    }



    @Column(name = "CREATE_UPDATE_DATETIME")
    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createUpdateDate) {
        this.createdDateTime = createUpdateDate;
    }

    @Column(name = "VALUED_DATETIME")
    public LocalDateTime getValuedDateTime() {
        return valuedDateTime;
    }

    public void setValuedDateTime(LocalDateTime valuedDateTime) {
        this.valuedDateTime = valuedDateTime;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
    public UnitOfMeasureCode getUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(unitOfMeasureValue);
    }

    public void setUnitOfMeasure(UnitOfMeasureCode code) {
        this.unitOfMeasureValue = code.getCode();
    }

    @Column(name = "UNIT_OF_MEASURE_CODE")
    @InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "volumeUnitOfMeasureCodeItem")
    @JsonSerialize(using = CodeLabelSerializer.class)
    public String getUnitOfMeasureValue() {
        return unitOfMeasureValue;
    }

    public void setUnitOfMeasureValue(String unitOfMeasureValue) {
        this.unitOfMeasureValue = unitOfMeasureValue;
    }

    @Column(name = "IS_SETTLEMENT_POSITION")
    @Convert(
            converter = YesNoConverter.class
    )
    public Boolean getIsSettlementPosition() {
        return isSettlementPosition;
    }

    public void setIsSettlementPosition(Boolean settlementPosition) {
        isSettlementPosition = settlementPosition;
    }

    @Column(name = "ERROR_CODE")
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    @Column(name = "ERROR_MSG")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
