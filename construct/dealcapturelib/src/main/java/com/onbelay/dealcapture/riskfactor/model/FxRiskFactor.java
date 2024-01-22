package com.onbelay.dealcapture.riskfactor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.RiskFactorDetail;
import com.onbelay.shared.enums.CurrencyCode;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "FX_RISK_FACTOR")
@NamedQueries({
        @NamedQuery(
                name = FxRiskFactorRepositoryBean.FETCH_RISK_FACTOR_BY_MARKET_DATE,
                query = "  SELECT riskFactor " +
                        "    FROM FxRiskFactor riskFactor " +
                        "   WHERE riskFactor.index.id = :indexId " +
                        "     AND riskFactor.detail.marketDate = :marketDate "),
        @NamedQuery(
                name = FxRiskFactorRepositoryBean.FIND_BY_INDEX_ID,
                query = "  SELECT riskFactor " +
                        "    FROM FxRiskFactor riskFactor " +
                        "   WHERE riskFactor.index.id = :indexId " +
                        "ORDER BY riskFactor.detail.marketDate"),

        @NamedQuery(
                name = FxRiskFactorRepositoryBean.FETCH_BY_FX_INDEX_IDS,
                query = "  SELECT riskFactor " +
                        "    FROM FxRiskFactor riskFactor " +
                        "   WHERE riskFactor.index.id in (:indexIds) " +
                        "ORDER BY riskFactor.id, riskFactor.detail.marketDate "),
        @NamedQuery(
                name = FxRiskFactorRepositoryBean.FETCH_RISK_FACTORS_BY_DATES,
                query = "  SELECT riskFactor " +
                        "    FROM FxRiskFactor riskFactor " +
                        "   WHERE riskFactor.index.id = :indexId " +
                        "     AND riskFactor.detail.marketDate >= :toMarketDate " +
                        "     AND riskFactor.detail.marketDate <= :fromMarketDate " +
                        "ORDER BY riskFactor.detail.marketDate")
})
public class FxRiskFactor extends TemporalAbstractEntity {

    private Integer id;

    private FxIndex index;

    private RiskFactorDetail detail = new RiskFactorDetail();

    @Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="FxRFGen", sequenceName="FX_RISK_FACTOR_SEQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "FxRFGen")
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @ManyToOne()
    @JoinColumn(name = "FX_INDEX_ID")
    public FxIndex getIndex() {
        return this.index;
    }

    public void setIndex(final FxIndex fxIndex) {
        this.index = fxIndex;
    }

    @Override
    protected void validate() throws OBValidationException {
        super.validate();
        detail.validate();
    }

    @Embedded
    public RiskFactorDetail getDetail() {
        return this.detail;
    }

    @Transient
    @JsonIgnore
    public FxRate fetchFxRate(
            CurrencyCode toCurrencyCode,
            CurrencyCode fromCurrencyCode) {
        if (getIndex().getDetail().getToCurrencyCode() == toCurrencyCode) {
            return new FxRate(
                    detail.getValue(),
                    index.getDetail().getToCurrencyCode(),
                    index.getDetail().getFromCurrencyCode());
        } else {
            FxRate rate = new FxRate(
                    detail.getValue(),
                    index.getDetail().getToCurrencyCode(),
                    index.getDetail().getFromCurrencyCode());
            return rate.getInversion();
        }
    }


    public void setDetail(final RiskFactorDetail detail) {
        this.detail = detail;
    }


    public static FxRiskFactor create(
            FxIndex fxIndex,
            FxRiskFactorSnapshot snapshot) {
        FxRiskFactor factor = new FxRiskFactor();
        factor.createWith(
                fxIndex,
                snapshot);
        return factor;
    }

    protected void createWith(
            FxIndex index,
            FxRiskFactorSnapshot snapshot) {
        detail.setDefaults();
        detail.copyFrom(snapshot.getDetail());
        index.add(this);
    }


    public void updateRate(FxRate fxRate) {
        detail.setValue(fxRate.getValue());
        detail.setCreateUpdateDateTime(LocalDateTime.now());
        update();
    }

    @Transient
    @JsonIgnore
    public FxRate getCurrentFxRate() {
        return new FxRate(
                detail.getValue(),
                index.getDetail().getToCurrencyCode(),
                index.getDetail().getFromCurrencyCode());
    }

    @Override
    protected AuditAbstractEntity createHistory() {
        return FxRiskFactorAudit.create(this);
    }


    @Override
    public AuditAbstractEntity fetchRecentHistory() {
        return FxRiskFactorAudit.findRecentHistory(this);
    }
}
