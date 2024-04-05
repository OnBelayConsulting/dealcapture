package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionViewDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
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
                        "   AND detail.createdDateTime = :createdDateTime " +
                        "ORDER BY position.detail.startDate ")
})
public class DealPositionView extends AbstractEntity {
    private Integer id;

    private Integer dealId;

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
        return valuationIndexManager.generateFxRate(
                detail.getFixedFxIndexId(),
                detail.getFixedFxRateValue());
    }

    @Transient
    @JsonIgnore
    public Price getDealPrice(ValuationIndexManager valuationIndexManager) {
        return valuationIndexManager.generatePrice(
                detail.getDealPriceIndexId(),
                detail.getDealPriceRfValue());
    }

    @Transient
    @JsonIgnore
    public FxRate getDealPriceFxRate(ValuationIndexManager valuationIndexManager) {
        return valuationIndexManager.generateFxRate(
                detail.getDealPriceFxIndexId(),
                detail.getDealPriceFxRateValue());
    }


    @Transient
    @JsonIgnore
    public Price getMarketPrice(ValuationIndexManager valuationIndexManager) {
        return valuationIndexManager.generatePrice(
                detail.getMarketPriceIndexId(),
                detail.getMarketPriceRfValue());
    }
    @Transient
    @JsonIgnore
    public FxRate getMarketPriceFxRate(ValuationIndexManager valuationIndexManager) {
        return valuationIndexManager.generateFxRate(
                detail.getMarketPriceFxIndexId(),
                detail.getMarketPriceFxValue());
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
