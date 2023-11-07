package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionDetail;
import jakarta.persistence.*;

@Entity
@Table(name = "DEAL_POSITION")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_BY_DEAL,
                query = "SELECT position " +
                        "  FROM DealPosition position " +
                        " WHERE position.deal.id = :dealId " +
                      "ORDER BY position.dealPositionDetail.startDate ")
})
public abstract class DealPosition extends TemporalAbstractEntity {

    private Integer id;

    private BaseDeal deal;

    private DealPositionDetail dealPositionDetail = new DealPositionDetail();

    @Id
    @Column(name="ENTITY_ID", insertable =  false, updatable = false)
    @SequenceGenerator(name="dealPosGen", sequenceName="DEAL_POSITION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "dealPosGen")
    public Integer getId() {
        return id;
    }

    private void setId(Integer dealId) {
        this.id = dealId;
    }

    @ManyToOne
    @JoinColumn(name = "DEAL_ID")
    public BaseDeal getDeal() {
        return deal;
    }

    public void setDeal(BaseDeal deal) {
        this.deal = deal;
    }

    @Embedded
    public DealPositionDetail getDealPositionDetail() {
        return dealPositionDetail;
    }

    public void setDealPositionDetail(DealPositionDetail detail) {
        this.dealPositionDetail = detail;
    }

    public void createWith(
            BaseDeal deal,
            DealPositionSnapshot snapshot) {
        this.dealPositionDetail.setDefaults();
        this.dealPositionDetail.copyFrom(snapshot.getDealPositionDetail());
    }

    public void updateWith(DealPositionSnapshot snapshot) {
        this.dealPositionDetail.copyFrom(snapshot.getDealPositionDetail());
    }

    @Override
    public AuditAbstractEntity fetchRecentHistory() {
        return DealPositionAudit.findRecentHistory(this);
    }
}
