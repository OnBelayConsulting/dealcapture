package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.DealCost;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealCostRepository;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.*;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import jakarta.persistence.*;

@Entity
@Table(name = "COST_POSITION")
@NamedQueries({
        @NamedQuery(
                name = CostPositionRepositoryBean.FIND_IDS_BY_DEAL,
                query = "SELECT position.id " +
                        "  FROM CostPosition position " +
                        " WHERE position.deal.id = :dealId " +
                      "ORDER BY position.detail.startDate "),
        @NamedQuery(
                name = CostPositionRepositoryBean.CALC_TOTAL_COST_SUMMARIES_BY_DEAL,
                query = "SELECT new com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary( " +
                        "       position.deal.id, " +
                        "       position.detail.currencyCodeValue, " +
                        "       position.detail.startDate, " +
                        "       position.detail.createdDateTime, " +
                        "       sum(position.detail.costAmount) ) " +
                        "  FROM CostPosition position " +
                     " GROUP BY position.deal.id, position.detail.currencyCodeValue, position.detail.startDate, position.detail.createdDateTime " +
                      "  HAVING position.deal.id = :dealId" +
                        "   AND position.detail.currencyCodeValue = :currencyCode " +
                        "   AND position.detail.createdDateTime = :createdDateTime"),
        @NamedQuery(
                name = CostPositionRepositoryBean.CALC_TOTAL_COST_SUMMARIES,
                query = "SELECT new com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary( " +
                        "       position.deal.id, " +
                        "       position.detail.currencyCodeValue, " +
                        "       position.detail.startDate, " +
                        "       position.detail.createdDateTime, " +
                        "       sum(position.detail.costAmount) ) " +
                        "  FROM CostPosition position " +
                     " GROUP BY position.deal.id, position.detail.currencyCodeValue, position.detail.startDate, position.detail.createdDateTime " +
                      "  HAVING position.deal.id in (:dealIds)" +
                        "   AND position.detail.currencyCodeValue = :currencyCode " +
                        "   AND position.detail.createdDateTime = :createdDateTime"),
        @NamedQuery(
                name = CostPositionRepositoryBean.FIND_BY_DEAL,
                query = "SELECT position " +
                        "  FROM CostPosition position " +
                        " WHERE position.deal.id = :dealId " +
                      "ORDER BY position.detail.startDate ")
})
public class CostPosition extends AbstractEntity {

    private Integer id;

    private BaseDeal deal;

    private DealCost dealCost;

    private FxRiskFactor costFxRiskFactor;

    private CostPositionDetail detail = new CostPositionDetail();

    @Id
    @Column(name="ENTITY_ID", insertable =  false, updatable = false)
    @SequenceGenerator(name="dealCostPosGen", sequenceName="COST_POSITION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "dealCostPosGen")
    public Integer getId() {
        return id;
    }

    private void setId(Integer dealId) {
        this.id = dealId;
    }

    @ManyToOne
    @JoinColumn(name = "DEAL_ID")
    public BaseDeal  getDeal() {
        return deal;
    }

    public void setDeal(BaseDeal deal) {
        this.deal = deal;
    }

    @ManyToOne
    @JoinColumn(name = "DEAL_COST_ID")
    public DealCost getDealCost() {
        return dealCost;
    }

    public void setDealCost(DealCost dealCost) {
        this.dealCost = dealCost;
    }

    @Embedded
    public CostPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(CostPositionDetail costDetail) {
        this.detail = costDetail;
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_FX_RISK_FACTOR_ID")
    public FxRiskFactor getCostFxRiskFactor() {
        return costFxRiskFactor;
    }


    public void setCostFxRiskFactor(FxRiskFactor costFxRiskFactor) {
        this.costFxRiskFactor = costFxRiskFactor;
    }

    @Override
    protected void validate() throws OBValidationException {

    }

    public void createWith(CostPositionSnapshot snapshot) {
        this.deal = getDealRepository().load(snapshot.getDealId());
        this.detail.copyFrom(snapshot.getDetail());
        updateRelationships(snapshot);
    }

    public void updateWith(CostPositionSnapshot snapshot) {
        this.detail.copyFrom(snapshot.getDetail());
        updateRelationships(snapshot);
    }

    private void updateRelationships(CostPositionSnapshot snapshot) {
        if (snapshot.getDealCostId() != null)
            this.dealCost = getDealCostRepository().load(snapshot.getDealCostId());

        if (snapshot.getCostFxRiskFactorId() != null)
            this.costFxRiskFactor = getFxRiskFactorRepository().load(snapshot.getCostFxRiskFactorId());
    }

    @Transient
    protected static DealRepository getDealRepository() {
        return (DealRepository) ApplicationContextFactory.getBean(DealRepository.BEAN_NAME);
    }

    @Transient
    protected static DealCostRepository getDealCostRepository() {
        return (DealCostRepository) ApplicationContextFactory.getBean(DealCostRepository.BEAN_NAME);
    }

    @Transient
    protected static FxRiskFactorRepository getFxRiskFactorRepository() {
        return (FxRiskFactorRepository) ApplicationContextFactory.getBean(FxRiskFactorRepository.BEAN_NAME);
    }

}
