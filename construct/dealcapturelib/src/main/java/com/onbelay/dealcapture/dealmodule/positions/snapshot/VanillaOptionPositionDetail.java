package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionStyleCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.TradeTypeCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

public class VanillaOptionPositionDetail {
    private BigDecimal dealStrikePriceValue;

    private String tradeTypeCodeValue;

    private String optionTypeCodeValue;
    private String optionStyleCodeValue;
    private Double volatilityValue;


    public VanillaOptionPositionDetail() {
    }

    @Transient
    @JsonIgnore
    public TradeTypeCode getTradeTypeCode() {
        return TradeTypeCode.lookUp(tradeTypeCodeValue);
    }

    public void setTradeTypeCode(TradeTypeCode code) {
        this.tradeTypeCodeValue = code.getCode();
    }

    @Column(name="TRADE_TYPE_CODE")
    public String getTradeTypeCodeValue() {
        return tradeTypeCodeValue;
    }

    public void setTradeTypeCodeValue(String tradeTypeCodeValue) {
        this.tradeTypeCodeValue = tradeTypeCodeValue;
    }

    @Transient
    @JsonIgnore
    public OptionTypeCode getOptionTypeCode() {
        return OptionTypeCode.lookUp(optionTypeCodeValue);
    }

    public void setOptionTypeCode(OptionTypeCode code) {
        this.optionTypeCodeValue = code.getCode();
    }

    @Column(name="OPTION_TYPE_CODE")
    public String getOptionTypeCodeValue() {
        return optionTypeCodeValue;
    }

    public void setOptionTypeCodeValue(String paysPriceValuationCodeValue) {
        this.optionTypeCodeValue = paysPriceValuationCodeValue;
    }

    @Transient
    @JsonIgnore
    public OptionStyleCode getOptionStyleCode() {
        return OptionStyleCode.lookUp(optionStyleCodeValue);
    }

    public void setOptionStyleCode(OptionStyleCode code) {
        this.optionStyleCodeValue = code.getCode();
    }

    @Column(name="OPTION_STYLE_CODE")
    public String getOptionStyleCodeValue() {
        return optionStyleCodeValue;
    }

    public void setOptionStyleCodeValue(String optionStyleCodeValue) {
        this.optionStyleCodeValue = optionStyleCodeValue;
    }


    @Column(name = "DEAL_STRIKE_PRICE")
    public BigDecimal getDealStrikePriceValue() {
        return dealStrikePriceValue;
    }

    public void setDealStrikePriceValue(BigDecimal dealStrikePriceValue) {
        this.dealStrikePriceValue = dealStrikePriceValue;
    }

    @Column(name = "VOLATILITY_VALUE")
    public Double getVolatilityValue() {
        return volatilityValue;
    }

    public void setVolatilityValue(Double volatilityValue) {
        this.volatilityValue = volatilityValue;
    }


    public void copyFrom(VanillaOptionPositionDetail copy) {

        if (copy.dealStrikePriceValue != null)
            this.dealStrikePriceValue = copy.dealStrikePriceValue;

        if (copy.tradeTypeCodeValue != null)
            this.tradeTypeCodeValue = copy.tradeTypeCodeValue;

        if (copy.optionTypeCodeValue != null)
            this.optionTypeCodeValue = copy.optionTypeCodeValue;

        if (copy.optionStyleCodeValue != null)
            this.optionStyleCodeValue = copy.optionStyleCodeValue;


        if (copy.volatilityValue != null)
            this.volatilityValue = copy.volatilityValue;

    }

}
