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
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DEAL_POSITION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DEAL_TYPE_CODE")
@NamedQueries({
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_MIN_START_DATE,
                query = "SELECT min(position.detail.startDate) " +
                        "  FROM DealPosition position " +
                        " WHERE position.detail.createdDateTime = :createdDateTime " +
                      "     AND position.detail.currencyCodeValue = :currencyCode "),
         @NamedQuery(
                name = DealPositionRepositoryBean.FIND_MAX_START_DATE,
                query = "SELECT max(position.detail.startDate) " +
                        "  FROM DealPosition position " +
                        " WHERE position.detail.createdDateTime = :createdDateTime " +
                      "     AND position.detail.currencyCodeValue = :currencyCode "),
       @NamedQuery(
                name = DealPositionRepositoryBean.FIND_IDS_BY_DEAL,
                query = "SELECT position.id " +
                        "  FROM DealPosition position " +
                        " WHERE position.deal.id = :dealId " +
                      "ORDER BY position.detail.startDate "),
        @NamedQuery(
                name = DealPositionRepositoryBean.FIND_BY_DEAL,
                query = "SELECT position " +
                        "  FROM DealPosition position " +
                        " WHERE position.deal.id = :dealId " +
                      "ORDER BY position.detail.startDate ")
})
public abstract class DealPosition extends AbstractEntity {

    private Integer id;
    private String dealTypeCodeValue;

    private BaseDeal deal;

    private FxRiskFactor fixedPriceFxRiskFactor;

    private DealPositionDetail detail = new DealPositionDetail();

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


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FIXED_PRICE_FX_RISK_FACTOR_ID")
    public FxRiskFactor getFixedPriceFxRiskFactor() {
        return fixedPriceFxRiskFactor;
    }


    public void setFixedPriceFxRiskFactor(FxRiskFactor fixedPriceFxRiskFactor) {
        this.fixedPriceFxRiskFactor = fixedPriceFxRiskFactor;
    }


    @Embedded
    public DealPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(DealPositionDetail detail) {
        this.detail = detail;
    }

    @Embedded
    public PositionSettlementDetail getSettlementDetail() {
        return settlementDetail;
    }

    public void setSettlementDetail(PositionSettlementDetail settlementDetail) {
        this.settlementDetail = settlementDetail;
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

    private void setAssociations(DealPositionSnapshot snapshot) {

        if (snapshot.getFixedPriceFxRiskFactorId() != null)
            this.fixedPriceFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getFixedPriceFxRiskFactorId());

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
        this.detail.setDefaults();
        this.settlementDetail.setDefaults();

        this.detail.copyFrom(snapshot.getPositionDetail());
        this.settlementDetail.copyFrom(snapshot.getSettlementDetail());
    }

    public void updateWith(DealPositionSnapshot snapshot) {
        this.detail.copyFrom(snapshot.getPositionDetail());
        this.settlementDetail.copyFrom(snapshot.getSettlementDetail());
    }

    @Transient
    protected static DealRepository getDealRepository() {
        return (DealRepository) ApplicationContextFactory.getBean(DealRepository.BEAN_NAME);
    }

    protected static FxRiskFactorRepository getFxRiskFactorRepository() {
        return (FxRiskFactorRepository) ApplicationContextFactory.getBean(FxRiskFactorRepository.BEAN_NAME);
    }

    @Transient
    protected static PositionRiskFactorMappingRepository getPositionRiskFactorMappingRepository() {
        return (PositionRiskFactorMappingRepository) ApplicationContextFactory.getBean(PositionRiskFactorMappingRepository.BEAN_NAME);
    }

}
