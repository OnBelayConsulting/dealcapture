package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.repository.PositionRiskFactorMappingRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.persistence.*;
import org.hibernate.type.YesNoConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DEAL_POSITION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DEAL_TYPE_CODE")
@NamedQueries({
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_BY_DEAL,
                query = "SELECT position " +
                        "  FROM DealPosition position " +
                        " WHERE position.dealId = :dealId " +
                      "ORDER BY position.dealPositionDetail.startDate ")
})
public abstract class DealPosition extends AbstractEntity {

    private Integer id;
    private String dealTypeCodeValue;

    private Integer dealId;

    private DealPositionDetail dealPositionDetail = new DealPositionDetail();

    protected DealPosition(String dealTypeCodeValue) {
        this.dealTypeCodeValue = dealTypeCodeValue;
    }

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

    @Transient
    @JsonIgnore
    public DealTypeCode getDealTypeCode() {
        return DealTypeCode.lookUp(dealTypeCodeValue);
    }

    public void setDealTypeCode(DealTypeCode code) {
        this.dealTypeCodeValue = code.getCode();
    }

    @Column(name = "DEAL_TYPE_CODE", insertable = false, updatable = false)
    private String getDealTypeCodeValue() {
        return dealTypeCodeValue;
    }

    private void setDealTypeCodeValue(String dealTypeCodeValue) {
        this.dealTypeCodeValue = dealTypeCodeValue;
    }

    @Column(name = "DEAL_ID")
    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

    @Transient
    public BaseDeal  getDeal() {
        return getDealRepository().load(new EntityId(dealId));
    }

    public void setDeal(BaseDeal deal) {
        this.dealId = deal.getId();
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
            }
        }
        return ids;
    }

    @Override
    protected void validate() throws OBValidationException {

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

    public void createWith(DealPositionSnapshot snapshot) {
        this.dealId = snapshot.getDealId().getId();
        this.dealPositionDetail.setDefaults();
        this.dealPositionDetail.copyFrom(snapshot.getDealPositionDetail());
    }

    public void updateWith(DealPositionSnapshot snapshot) {
        this.dealPositionDetail.copyFrom(snapshot.getDealPositionDetail());
    }

    @Transient
    protected static DealRepository getDealRepository() {
        return (DealRepository) ApplicationContextFactory.getBean(DealRepository.BEAN_NAME);
    }

    @Transient
    protected static PositionRiskFactorMappingRepository getPositionRiskFactorMappingRepository() {
        return (PositionRiskFactorMappingRepository) ApplicationContextFactory.getBean(PositionRiskFactorMappingRepository.BEAN_NAME);
    }

}
