package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionViewDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
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
                name = DealPositionRepositoryBean.FIND_DEAL_POSITION_VIEWS_BY_DEAL,
                query = "SELECT position " +
                        "  FROM DealPositionView position " +
                        " WHERE position.dealId = :dealId " +
                        "ORDER BY position.detail.startDate "),
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_DEAL_POSITION_VIEWS,
                query = "SELECT position " +
                        "  FROM DealPositionView position " +
                        " WHERE position.id in (:positionIds) " +
                        "ORDER BY position.detail.startDate ")
})
public class DealPositionView extends AbstractEntity {
    private Integer id;

    private Integer dealId;

    private DealPositionViewDetail detail = new DealPositionViewDetail();

    private CostPositionDetail costPositionDetail = new CostPositionDetail();

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

    @Embedded
    public CostPositionDetail getCostPositionDetail() {
        return costPositionDetail;
    }

    public void setCostPositionDetail(CostPositionDetail costPositionDetail) {
        this.costPositionDetail = costPositionDetail;
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
