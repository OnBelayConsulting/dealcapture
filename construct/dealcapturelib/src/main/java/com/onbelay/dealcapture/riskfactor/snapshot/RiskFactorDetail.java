package com.onbelay.dealcapture.riskfactor.snapshot;

import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RiskFactorDetail {

    private BigDecimal value;
    private LocalDate marketDate;
    private Integer hourEnding;
    private LocalDateTime createUpdateDateTime;

    @Transient
    public  void setDefaults() {
        createUpdateDateTime = LocalDateTime.now();
        hourEnding = 0;
    }

    @Column(name = "MARKET_VALUE")
    public BigDecimal getValue() {
        return this.value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    @Column(name = "MARKET_DATE")
    public LocalDate getMarketDate() {
        return this.marketDate;
    }

    public void setMarketDate(final LocalDate marketDate) {
        this.marketDate = marketDate;
    }

    @Column(name = "HOUR_ENDING")
    public Integer getHourEnding() {
        return hourEnding;
    }

    public void setHourEnding(Integer hourEnding) {
        this.hourEnding = hourEnding;
    }

    public void validate() throws OBValidationException {
        if (marketDate == null)
            throw new OBValidationException(RiskFactorErrorCode.MISSING_RISK_FACTOR_DATE.getCode());
    }

    @Column(name = "CREATE_UPDATE_DATETIME")
    public LocalDateTime getCreateUpdateDateTime() {
        return createUpdateDateTime;
    }

    public void setCreateUpdateDateTime(LocalDateTime createUpdateDateTime) {
        this.createUpdateDateTime = createUpdateDateTime;
    }

    public void copyFrom(RiskFactorDetail copy) {
        if (copy.marketDate != null)
            this.marketDate = copy.marketDate;

        if (copy.hourEnding != null)
            this.hourEnding = copy.hourEnding;

        if (copy.createUpdateDateTime != null)
            this.createUpdateDateTime = copy.createUpdateDateTime;

        if (copy.value != null)
            this.value = copy.value;
    }

}
