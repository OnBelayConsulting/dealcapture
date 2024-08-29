package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialSwapPositionDetail extends AbstractDetail {

    private String paysValuationValue;
    private String receivesValuationValue;


    public void copyFrom(FinancialSwapPositionDetail copy) {
        if (copy.paysValuationValue != null)
            this.paysValuationValue = copy.paysValuationValue;

        if (copy.receivesValuationValue != null)
            this.receivesValuationValue = copy.receivesValuationValue;

    }

    @Transient
    public ValuationCode getPaysValuationCode() {
        return ValuationCode.lookUp(paysValuationValue);
    }

    public void setPaysValuationCode(ValuationCode code) {
        this.paysValuationValue = code.getCode();
    }

    @Column(name = "PAYS_VALUATION_CODE")
    public String getPaysValuationValue() {
        return paysValuationValue;
    }

    public void setPaysValuationValue(String paysValuationValue) {
        this.paysValuationValue = paysValuationValue;
    }

    @Transient
    public ValuationCode getReceivesValuationCode() {
        return ValuationCode.lookUp(receivesValuationValue);
    }

    public void setReceivesValuationCode(ValuationCode code) {
        this.receivesValuationValue = code.getCode();
    }

    @Column(name = "RECEIVES_VALUATION_CODE")
    public String getReceivesValuationValue() {
        return receivesValuationValue;
    }

    public void setReceivesValuationValue(String receivesValuationValue) {
        this.receivesValuationValue = receivesValuationValue;
    }

}
