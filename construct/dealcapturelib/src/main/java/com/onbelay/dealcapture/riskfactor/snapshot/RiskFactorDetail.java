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
    private LocalDateTime createUpdateDate;

    @Transient
    public  void setDefaults() {
        createUpdateDate = LocalDateTime.now();
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

    public void validate() throws OBValidationException {
        if (marketDate == null)
            throw new OBValidationException(RiskFactorErrorCode.MISSING_RISK_FACTOR_DATE.getCode());
    }

    @Column(name = "CREATE_UPDATE_DATETIME")
    public LocalDateTime getCreateUpdateDate() {
        return createUpdateDate;
    }

    public void setCreateUpdateDate(LocalDateTime createUpdateDate) {
        this.createUpdateDate = createUpdateDate;
    }

    public void copyFrom(RiskFactorDetail copy) {
        if (copy.marketDate != null)
            this.marketDate = copy.marketDate;

        if (copy.createUpdateDate != null)
            this.createUpdateDate = copy.createUpdateDate;

        if (copy.value != null)
            this.value = copy.value;
    }

}
