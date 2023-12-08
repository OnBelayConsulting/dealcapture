package com.onbelay.dealcapture.riskfactor.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.RiskFactorDetail;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PRICE_RISK_FACTOR")
@NamedQueries({
        @NamedQuery(
                name = PriceRiskFactorRepositoryBean.FETCH_RISK_FACTOR_BY_MARKET_DATE,
                query = "  SELECT riskFactor " +
                        "    FROM PriceRiskFactor riskFactor " +
                        "   WHERE riskFactor.index.id = :indexId " +
                        "     AND riskFactor.detail.marketDate = :marketDate "),
        @NamedQuery(
                name = PriceRiskFactorRepositoryBean.FIND_BY_PRICE_INDEX_ID,
                query = "  SELECT riskFactor " +
                        "    FROM PriceRiskFactor riskFactor " +
                        "   WHERE riskFactor.index.id = :indexId " +
                        "ORDER BY riskFactor.detail.marketDate "),

        @NamedQuery(
                name = PriceRiskFactorRepositoryBean.LOAD_ALL,
                query = "  SELECT riskFactor " +
                        "    FROM PriceRiskFactor riskFactor " +
                        "ORDER BY riskFactor.detail.marketDate"),
        @NamedQuery(
                name = PriceRiskFactorRepositoryBean.FETCH_RISK_FACTORS_BY_DATES,
                query = "  SELECT riskFactor " +
                        "    FROM PriceRiskFactor riskFactor " +
                        "   WHERE riskFactor.index.id = :indexId " +
                        "     AND riskFactor.detail.marketDate >= :toMarketDate " +
                        "     AND riskFactor.detail.marketDate <= :fromMarketDate")
})
public class PriceRiskFactor extends TemporalAbstractEntity {

    private Integer id;
    private PriceIndex index;

    private RiskFactorDetail detail = new RiskFactorDetail();

    @Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PriceRFGen", sequenceName="PRICE_RISK_FACTOR_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PriceRFGen")
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Embedded
    public RiskFactorDetail getDetail() {
        return detail;
    }

    public void setDetail(RiskFactorDetail detail) {
        this.detail = detail;
    }

    @ManyToOne()
    @JoinColumn(name = "PRICE_INDEX_ID")
    public PriceIndex getIndex() {
        return this.index;
    }

    public void setIndex(final PriceIndex priceIndex) {
        this.index = priceIndex;
    }


    public static PriceRiskFactor create(
            PriceIndex priceIndex,
            PriceRiskFactorSnapshot snapshot) {

        PriceRiskFactor factor = new PriceRiskFactor();
        factor.createWith(
                priceIndex,
                snapshot);
        return factor;
    }

    protected void createWith(
            PriceIndex index,
            PriceRiskFactorSnapshot snapshot) {
        detail.setDefaults();
        this.detail.copyFrom(snapshot.getDetail());
        index.addPriceRiskFactor(this);
    }

    @Override
    protected void validate() throws OBValidationException {
        super.validate();
        detail.validate();
    }

    @Override
    protected AuditAbstractEntity createHistory() {
        return PriceRiskFactorAudit.create(this);
    }


    @Override
    public AuditAbstractEntity fetchRecentHistory() {
        return PriceRiskFactorAudit.findRecentHistory(this);
    }

    public void valueRiskFactor(LocalDateTime currentDateTime) {
        Price price = index.getCurrentPrice(detail.getMarketDate());
        detail.setCreateUpdateDate(currentDateTime);
        if (price.isInError())
            detail.setValue(null);
        else
            detail.setValue(price.getValue());
    }

    public Price fetchCurrentPrice() {
        return new Price(
                detail.getValue(),
                index.getDetail().getCurrencyCode(),
                index.getDetail().getUnitOfMeasureCode());
    }
}
