package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.repository.PositionRiskFactorMappingRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public abstract void valuePosition(LocalDateTime currentDateTime);

    protected void postCreateWith(DealPositionSnapshot snapshot) {
        if (snapshot.getRiskFactorMappingSnapshots().isEmpty() == false) {
            savePositionRiskFactorMappings(snapshot.getRiskFactorMappingSnapshots());
        }
    }

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

    public List<EntityId> savePositionRiskFactorMappings(List<PositionRiskFactorMappingSnapshot> snapshots) {
        ArrayList<EntityId> ids = new ArrayList<>();
        for (PositionRiskFactorMappingSnapshot snapshot : snapshots) {
            if (snapshot.getEntityState() == EntityState.NEW) {
                PositionRiskFactorMapping mapping = PositionRiskFactorMapping.create(
                        this,
                        snapshot);
                ids.add(mapping.generateEntityId());
            } else if (snapshot.getEntityState() == EntityState.DELETE) {
                PositionRiskFactorMapping mapping = getPositionRiskFactorMappingRepository().load(snapshot.getEntityId());
                mapping.delete();
            }
        }
        return ids;
    }

    public List<PositionRiskFactorMapping> fetchPositionRiskFactorMappings() {
        return getPositionRiskFactorMappingRepository().findByDealPosition(this.generateEntityId());
    }

    protected List<PositionRiskFactorMappingSummary> findMappingSummaries(PriceTypeCode priceTypeCode) {
        return getPositionRiskFactorMappingRepository().findMappingSummaries(
                generateEntityId(),
                priceTypeCode);
    }

    protected void addPositionRiskFactorMapping(PositionRiskFactorMapping mapping) {
        mapping.setDealPosition(this);
        mapping.save();
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


    @Transient
    protected static PositionRiskFactorMappingRepository getPositionRiskFactorMappingRepository() {
        return (PositionRiskFactorMappingRepository) ApplicationContextFactory.getBean(PositionRiskFactorMappingRepository.BEAN_NAME);
    }

}
