package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionPriceDetail;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PhysicalDeal")
public class PhysicalPositionView extends DealPositionView {


    private Integer dealPriceRiskFactorId;
    private Integer dealPriceFXRiskFactorId;

    private Integer marketPriceRiskFactorId;
    private Integer marketPriceFXRiskFactorId;

    private PhysicalPositionDetail detail = new PhysicalPositionDetail();

    private PhysicalPositionPriceDetail priceDetail = new PhysicalPositionPriceDetail();

    @Embedded
    public PhysicalPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(PhysicalPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    @Embedded
    public PhysicalPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(PhysicalPositionDetail detail) {
        this.detail = detail;
    }

    @Column(name = "DEAL_PRICE_RISK_FACTOR_ID")
    public Integer getDealPriceRiskFactorId() {
        return dealPriceRiskFactorId;
    }

    public void setDealPriceRiskFactorId(Integer dealPriceRiskFactorId) {
        this.dealPriceRiskFactorId = dealPriceRiskFactorId;
    }

    @Column(name = "DEAL_PRICE_FX_RISK_FACTOR_ID")
    public Integer getDealPriceFXRiskFactorId() {
        return dealPriceFXRiskFactorId;
    }

    public void setDealPriceFXRiskFactorId(Integer dealPriceFXRiskFactorId) {
        this.dealPriceFXRiskFactorId = dealPriceFXRiskFactorId;
    }

    @Column(name = "MKT_PRICE_RISK_FACTOR_ID")
    public Integer getMarketPriceRiskFactorId() {
        return marketPriceRiskFactorId;
    }

    public void setMarketPriceRiskFactorId(Integer marketPriceRiskFactorId) {
        this.marketPriceRiskFactorId = marketPriceRiskFactorId;
    }

    @Column(name = "MKT_PRICE_FX_RISK_FACTOR_ID")
    public Integer getMarketPriceFXRiskFactorId() {
        return marketPriceFXRiskFactorId;
    }

    public void setMarketPriceFXRiskFactorId(Integer marketPriceFXRiskFactorId) {
        this.marketPriceFXRiskFactorId = marketPriceFXRiskFactorId;
    }

    @Transient
    @JsonIgnore
    public Price getDealPrice(ValuationIndexManager valuationIndexManager) {
        if (dealPriceRiskFactorId == null)
            return null;

        PriceRiskFactorSnapshot snapshot = valuationIndexManager.getPriceRiskFactor(dealPriceRiskFactorId);
        return valuationIndexManager.generatePrice(
                snapshot.getPriceIndexId().getId(),
                snapshot.getDetail().getValue());
    }

    @Transient
    @JsonIgnore
    public FxRate getDealPriceFxRate(ValuationIndexManager valuationIndexManager) {
        if (dealPriceFXRiskFactorId == null)
            return null;

        FxRiskFactorSnapshot snapshot = valuationIndexManager.getFxRiskFactor(dealPriceFXRiskFactorId);
        return valuationIndexManager.generateFxRate(
                snapshot.getFxIndexId().getId(),
                snapshot.getDetail().getValue());
    }


    @Transient
    @JsonIgnore
    public Price getMarketPrice(ValuationIndexManager valuationIndexManager) {
        if (marketPriceRiskFactorId == null)
            return null;

        PriceRiskFactorSnapshot snapshot = valuationIndexManager.getPriceRiskFactor(marketPriceRiskFactorId);
        return valuationIndexManager.generatePrice(
                snapshot.getPriceIndexId().getId(),
                snapshot.getDetail().getValue());
    }
    @Transient
    @JsonIgnore
    public FxRate getMarketPriceFxRate(ValuationIndexManager valuationIndexManager) {
        if (marketPriceFXRiskFactorId == null)
            return null;

        FxRiskFactorSnapshot snapshot = valuationIndexManager.getFxRiskFactor(marketPriceFXRiskFactorId);
        return valuationIndexManager.generateFxRate(
                snapshot.getFxIndexId().getId(),
                snapshot.getDetail().getValue());
    }


}
