package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VanillaOptionPositionPriceDetail {
    private BigDecimal underlyingPriceValue;
    private BigDecimal strikePriceValue;

    private LocalDate optionExpiryDate;

    public void copyFrom(VanillaOptionPositionPriceDetail copy) {

        if (copy.optionExpiryDate != null)
            this.optionExpiryDate = copy.optionExpiryDate;

        if (copy.underlyingPriceValue != null)
            this.underlyingPriceValue = copy.underlyingPriceValue;

        if (copy.strikePriceValue != null)
            this.strikePriceValue = copy.strikePriceValue;

    }

    public void setDefaults() {

    }

    @Column(name = "OPTION_EXPIRY_DATE")
    public LocalDate getOptionExpiryDate() {
        return optionExpiryDate;
    }

    public void setOptionExpiryDate(LocalDate optionExpiryDate) {
        this.optionExpiryDate = optionExpiryDate;
    }

    @Column(name = "UNDERLYING_PRICE")
    public BigDecimal getUnderlyingPriceValue() {
        return underlyingPriceValue;
    }

    public void setUnderlyingPriceValue(BigDecimal priceValue) {
        this.underlyingPriceValue = priceValue;
    }

    @Column(name = "STRIKE_PRICE")
    public BigDecimal getStrikePriceValue() {
        return strikePriceValue;
    }

    public void setStrikePriceValue(BigDecimal strikePriceValue) {
        this.strikePriceValue = strikePriceValue;
    }
}
