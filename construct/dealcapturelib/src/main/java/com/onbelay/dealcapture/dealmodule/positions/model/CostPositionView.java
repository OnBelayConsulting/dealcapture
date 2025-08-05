package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionViewDetail;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "COST_POSITION_VIEW")
@Immutable
@NamedQueries({
        @NamedQuery(
                name = CostPositionRepositoryBean.FIND_COST_POSITION_VIEWS_FX,
                query = "SELECT position " +
                        "  FROM CostPositionView position " +
                        " WHERE position.dealId in (:dealIds) " +
                        "   AND position.detail.isFixedValued = false " +
                        "   AND position.detail.currencyCodeValue = :currencyCode  " +
                        "   AND position.detail.startDate >= :fromDate  " +
                        "   AND position.detail.startDate <= :toDate  " +
                        "   AND position.detail.createdDateTime = :createdDateTime " +
                      "ORDER BY position.detail.startDate "),
        @NamedQuery(
                name = CostPositionRepositoryBean.FIND_COST_POSITION_VIEWS_FX_BY_DEAL,
                query = "SELECT position " +
                        "  FROM CostPositionView position " +
                        " WHERE position.dealId = :dealId " +
                        "   AND position.detail.isFixedValued = false " +
                        "   AND position.detail.currencyCodeValue = :currencyCode  " +
                        "   AND position.detail.startDate >= :fromDate  " +
                        "   AND position.detail.startDate <= :toDate  " +
                        "   AND position.detail.createdDateTime = :createdDateTime " +
                      "ORDER BY position.detail.startDate ")
})
public class CostPositionView extends AbstractEntity {
    private Integer id;

    private Integer dealId;

    private Integer costFXRiskFactorId;

    private CostPositionViewDetail detail = new CostPositionViewDetail();

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

    @Column(name = "COST_FX_RISK_FACTOR_ID")
    public Integer getCostFXRiskFactorId() {
        return costFXRiskFactorId;
    }

    public void setCostFXRiskFactorId(Integer costFXRiskFactorId) {
        this.costFXRiskFactorId = costFXRiskFactorId;
    }

    @Embedded
    public CostPositionViewDetail getDetail() {
        return detail;
    }

    public void setDetail(CostPositionViewDetail detail) {
        this.detail = detail;
    }

    @Transient
    @JsonIgnore
    public FxRate getCostFxRate(ValuationIndexManager valuationIndexManager) {
        if (costFXRiskFactorId == null)
            return null;

        FxRiskFactorSnapshot snapshot = valuationIndexManager.getFxRiskFactor(costFXRiskFactorId);
        return valuationIndexManager.generateFxRate(
                snapshot.getFxIndexId().getId(),
                snapshot.getDetail().getValue());
    }

    @Transient
    @JsonIgnore
    public Price getCostAsPrice() {
        return new Price(
                detail.getCostValue(),
                detail.getCostCurrencyCode(),
                detail.getCostUnitOfMeasure());
    }

    @Transient
    @JsonIgnore
    public Amount getCostAsAmount() {
        return new Amount(
                detail.getCostValue(),
                detail.getCostCurrencyCode());
    }

    @Transient
    public Quantity getQuantity() {
        return new Quantity(
                detail.getVolumeQuantityValue(),
                detail.getUnitOfMeasure());
    }


    @Override
    protected void validate() throws OBValidationException {

    }

}
