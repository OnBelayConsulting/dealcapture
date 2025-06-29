package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionPriceDetail;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("VanillaOption")
public class VanillaOptionPositionView extends DealPositionView {


    private Integer underlyingPriceRiskFactorId;
    private Integer underlyingPriceFXRiskFactorId;

    private VanillaOptionPositionDetail optionPositionDetail = new VanillaOptionPositionDetail();

    private VanillaOptionPositionPriceDetail priceDetail = new VanillaOptionPositionPriceDetail();

    @Embedded
    public VanillaOptionPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(VanillaOptionPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    @Embedded
    public VanillaOptionPositionDetail getOptionPositionDetail() {
        return optionPositionDetail;
    }

    public void setOptionPositionDetail(VanillaOptionPositionDetail optionPositionDetail) {
        this.optionPositionDetail = optionPositionDetail;
    }

    @Column(name = "UNDERLYING_PRICE_RISK_FACTOR_ID")
    public Integer getUnderlyingPriceRiskFactorId() {
        return underlyingPriceRiskFactorId;
    }

    public void setUnderlyingPriceRiskFactorId(Integer underlyingPriceRiskFactorId) {
        this.underlyingPriceRiskFactorId = underlyingPriceRiskFactorId;
    }

    @Column(name = "UNDERLYING_PRICE_FX_RISK_FACTOR_ID")
    public Integer getUnderlyingPriceFXRiskFactorId() {
        return underlyingPriceFXRiskFactorId;
    }

    public void setUnderlyingPriceFXRiskFactorId(Integer underlyingPriceFXRiskFactorId) {
        this.underlyingPriceFXRiskFactorId = underlyingPriceFXRiskFactorId;
    }

    @Transient
    @JsonIgnore
    public Price getUnderlyingPrice(ValuationIndexManager valuationIndexManager) {
        if (underlyingPriceRiskFactorId == null)
            return null;

        PriceRiskFactorSnapshot snapshot = valuationIndexManager.getPriceRiskFactor(underlyingPriceRiskFactorId);
        return valuationIndexManager.generatePrice(
                snapshot.getPriceIndexId().getId(),
                snapshot.getDetail().getValue());
    }


    public Price getDealStrikePrice(Price underlyingPrice) {
        return new Price(
                optionPositionDetail.getDealStrikePriceValue(),
                underlyingPrice.getCurrency(),
                underlyingPrice.getUnitOfMeasure());
    }


    @Transient
    @JsonIgnore
    public FxRate getUnderlyingPriceFxRate(ValuationIndexManager valuationIndexManager) {
        if (underlyingPriceFXRiskFactorId == null)
            return null;

        FxRiskFactorSnapshot snapshot = valuationIndexManager.getFxRiskFactor(underlyingPriceFXRiskFactorId);
        return valuationIndexManager.generateFxRate(
                snapshot.getFxIndexId().getId(),
                snapshot.getDetail().getValue());
    }

}
