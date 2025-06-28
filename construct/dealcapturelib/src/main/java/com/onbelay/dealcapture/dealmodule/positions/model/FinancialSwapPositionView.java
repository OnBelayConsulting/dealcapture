package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionPriceDetail;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("FinancialSwap")
public class FinancialSwapPositionView extends DealPositionView {


    private Integer paysPriceRiskFactorId;
    private Integer paysPriceFXRiskFactorId;

    private Integer receivesPriceRiskFactorId;
    private Integer receivesPriceFXRiskFactorId;

    private FinancialSwapPositionDetail detail = new FinancialSwapPositionDetail();

    private FinancialSwapPositionPriceDetail priceDetail = new FinancialSwapPositionPriceDetail();

    @Embedded
    public FinancialSwapPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(FinancialSwapPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    @Embedded
    public FinancialSwapPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(FinancialSwapPositionDetail detail) {
        this.detail = detail;
    }

    @Column(name = "PAYS_PRICE_RISK_FACTOR_ID")
    public Integer getPaysPriceRiskFactorId() {
        return paysPriceRiskFactorId;
    }

    public void setPaysPriceRiskFactorId(Integer paysPriceRiskFactorId) {
        this.paysPriceRiskFactorId = paysPriceRiskFactorId;
    }

    @Column(name = "DEAL_PRICE_FX_RISK_FACTOR_ID")
    public Integer getPaysPriceFXRiskFactorId() {
        return paysPriceFXRiskFactorId;
    }

    public void setPaysPriceFXRiskFactorId(Integer paysPriceFXRiskFactorId) {
        this.paysPriceFXRiskFactorId = paysPriceFXRiskFactorId;
    }

    @Column(name = "RECEIVES_PRICE_RISK_FACTOR_ID")
    public Integer getReceivesPriceRiskFactorId() {
        return receivesPriceRiskFactorId;
    }

    public void setReceivesPriceRiskFactorId(Integer receivesPriceRiskFactorId) {
        this.receivesPriceRiskFactorId = receivesPriceRiskFactorId;
    }

    @Column(name = "RECEIVES_PRICE_FX_RISK_FACTOR_ID")
    public Integer getReceivesPriceFXRiskFactorId() {
        return receivesPriceFXRiskFactorId;
    }

    public void setReceivesPriceFXRiskFactorId(Integer receivesPriceFXRiskFactorId) {
        this.receivesPriceFXRiskFactorId = receivesPriceFXRiskFactorId;
    }

    @Transient
    @JsonIgnore
    public Price getPaysPrice(ValuationIndexManager valuationIndexManager) {
        if (paysPriceRiskFactorId == null)
            return null;

        PriceRiskFactorSnapshot snapshot = valuationIndexManager.getPriceRiskFactor(paysPriceRiskFactorId);
        return valuationIndexManager.generatePrice(
                snapshot.getPriceIndexId().getId(),
                snapshot.getDetail().getValue());
    }

    @Transient
    @JsonIgnore
    public FxRate getPaysPriceFxRate(ValuationIndexManager valuationIndexManager) {
        if (paysPriceFXRiskFactorId == null)
            return null;

        FxRiskFactorSnapshot snapshot = valuationIndexManager.getFxRiskFactor(paysPriceFXRiskFactorId);
        return valuationIndexManager.generateFxRate(
                snapshot.getFxIndexId().getId(),
                snapshot.getDetail().getValue());
    }


    @Transient
    @JsonIgnore
    public Price getReceivesPrice(ValuationIndexManager valuationIndexManager) {
        if (receivesPriceRiskFactorId == null)
            return null;

        PriceRiskFactorSnapshot snapshot = valuationIndexManager.getPriceRiskFactor(receivesPriceRiskFactorId);
        return valuationIndexManager.generatePrice(
                snapshot.getPriceIndexId().getId(),
                snapshot.getDetail().getValue());
    }
    @Transient
    @JsonIgnore
    public FxRate getReceivesPriceFxRate(ValuationIndexManager valuationIndexManager) {
        if (receivesPriceFXRiskFactorId == null)
            return null;

        FxRiskFactorSnapshot snapshot = valuationIndexManager.getFxRiskFactor(receivesPriceFXRiskFactorId);
        return valuationIndexManager.generateFxRate(
                snapshot.getFxIndexId().getId(),
                snapshot.getDetail().getValue());
    }


}
