package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.repository.PositionRiskFactorMappingRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.*;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import jakarta.persistence.*;

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
                        " WHERE position.deal.id = :dealId " +
                      "ORDER BY position.dealPositionDetail.startDate ")
})
public abstract class DealPosition extends AbstractEntity {

    private Integer id;
    private String dealTypeCodeValue;

    private BaseDeal deal;

    private FxRiskFactor costFxRiskFactor;

    private DealPositionDetail dealPositionDetail = new DealPositionDetail();

    private CostPositionDetail costDetail = new CostPositionDetail();

    private PositionSettlementDetail settlementDetail = new PositionSettlementDetail();

    protected DealPosition(String dealTypeCodeValue) {
        this.dealTypeCodeValue = dealTypeCodeValue;
    }

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

    @ManyToOne
    @JoinColumn(name = "DEAL_ID")
    public BaseDeal  getDeal() {
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

    @Embedded
    public CostPositionDetail getCostDetail() {
        return costDetail;
    }

    public void setCostDetail(CostPositionDetail costDetail) {
        this.costDetail = costDetail;
    }

    @Embedded
    public PositionSettlementDetail getSettlementDetail() {
        return settlementDetail;
    }

    public void setSettlementDetail(PositionSettlementDetail settlementDetail) {
        this.settlementDetail = settlementDetail;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_FX_RISK_FACTOR_ID")
    public FxRiskFactor getCostFxRiskFactor() {
        return costFxRiskFactor;
    }


    public void setCostFxRiskFactor(FxRiskFactor costFxRiskFactor) {
        this.costFxRiskFactor = costFxRiskFactor;
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
        this.deal = getDealRepository().load(snapshot.getDealId());
        this.dealPositionDetail.setDefaults();
        this.settlementDetail.setDefaults();

        this.dealPositionDetail.copyFrom(snapshot.getDealPositionDetail());
        this.settlementDetail.copyFrom(snapshot.getSettlementDetail());
        this.costDetail.copyFrom(snapshot.getCostDetail());
    }

    public void updateWith(DealPositionSnapshot snapshot) {
        this.dealPositionDetail.copyFrom(snapshot.getDealPositionDetail());
        this.settlementDetail.copyFrom(snapshot.getSettlementDetail());
        this.costDetail.copyFrom(snapshot.getCostDetail());
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
