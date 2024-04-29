package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionViewDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "DEAL_POSITION_VIEW")
@Immutable
@NamedQueries({
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_DEAL_POSITION_VIEWS,
                query = "SELECT position " +
                        "  FROM DealPositionView position " +
                        " WHERE position.dealId in (:dealIds) " +
                        "   AND detail.currencyCodeValue = :currencyCode " +
                        "   AND detail.createdDateTime = :createdDateTime "),
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_DEAL_POSITION_VIEWS_BY_DEAL,
                query = "SELECT position " +
                        "  FROM DealPositionView position " +
                        " WHERE position.dealId = :dealId " +
                        "   AND detail.currencyCodeValue = :currencyCode " +
                        "   AND detail.createdDateTime = :createdDateTime ")
})
public class DealPositionView extends AbstractEntity {
    private Integer id;

    private Integer dealId;

    private Integer powerProfileId;

    private Integer fixedPriceFXRiskFactorId;

    private Integer dealPriceRiskFactorId;
    private Integer dealPriceFXRiskFactorId;

    private Integer marketPriceRiskFactorId;
    private Integer marketPriceFXRiskFactorId;

    private DealPositionViewDetail detail = new DealPositionViewDetail();

    private List<PositionRiskFactorMappingSummary> mappingSummaries = new ArrayList<>();

    @Id
    @Column(name="ENTITY_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer dealDayId) {
        this.id = dealDayId;
    }

    @Column(name = "DEAL_ID")
    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

    @Column(name = "POWER_PROFILE_ID")
    public Integer getPowerProfileId() {
        return powerProfileId;
    }

    public void setPowerProfileId(Integer powerProfileId) {
        this.powerProfileId = powerProfileId;
    }

    @Column(name = "FIXED_PRICE_FX_RISK_FACTOR_ID")
    public Integer getFixedPriceFXRiskFactorId() {
        return fixedPriceFXRiskFactorId;
    }

    public void setFixedPriceFXRiskFactorId(Integer fixedPriceFXRiskFactorId) {
        this.fixedPriceFXRiskFactorId = fixedPriceFXRiskFactorId;
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

    @Embedded
    public DealPositionViewDetail getDetail() {
        return detail;
    }

    public void setDetail(DealPositionViewDetail detail) {
        this.detail = detail;
    }


    @Transient
    @JsonIgnore
    public Price getFixedPrice() {
        return new Price(
                detail.getFixedPriceValue(),
                detail.getFixedPriceCurrencyCode(),
                detail.getFixedPriceUnitOfMeasure());
    }

    @Transient
    @JsonIgnore
    public FxRate getFixedFxRate(ValuationIndexManager valuationIndexManager) {
        if (fixedPriceFXRiskFactorId == null)
            return null;

        FxRiskFactorSnapshot snapshot = valuationIndexManager.getFxRiskFactor(fixedPriceFXRiskFactorId);
        return valuationIndexManager.generateFxRate(
                snapshot.getFxIndexId().getId(),
                snapshot.getDetail().getValue());
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

    @Override
    protected void validate() throws OBValidationException {

    }

    public List<PositionRiskFactorMappingSummary> findMappingSummaries(PriceTypeCode code) {
        return mappingSummaries.stream().filter(c-> c.getPriceTypeCode() == code).collect(Collectors.toList());
    }

    @Transient
    public List<PositionRiskFactorMappingSummary> getMappingSummaries() {
        return mappingSummaries;
    }

    public void setMappingSummaries(List<PositionRiskFactorMappingSummary> mappingSummaries) {
        this.mappingSummaries = mappingSummaries;
    }

    public void addMappingSummary(PositionRiskFactorMappingSummary summary) {
        mappingSummaries.add(summary);
    }
}
