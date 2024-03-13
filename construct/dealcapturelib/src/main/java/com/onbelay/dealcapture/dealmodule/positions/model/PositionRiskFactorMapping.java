package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.persistence.*;

@Entity
@Table(name = "POSITION_RISK_FACTOR_MAP")
@NamedQueries({
        @NamedQuery(
                name = PositionRiskFactorMappingRepositoryBean.FIND_BY_DEAL_POSITION,
                query = "SELECT mapping " +
                        "  FROM PositionRiskFactorMapping mapping " +
                        " WHERE mapping.dealPosition.id = :positionId "),
        @NamedQuery(
                name = PositionRiskFactorMappingRepositoryBean.FIND_MAPPING_SUMMARY,
                query = "SELECT new com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary(" +
                        "   mapping.id," +
                        "   mapping.dealPosition.id, " +
                        "   mapping.detail.priceTypeCodeValue, " +
                        "   priceRiskFactor.index.detail.currencyCodeValue, " +
                        "   priceRiskFactor.index.detail.unitOfMeasureCodeValue, " +
                        "   priceRiskFactor.detail.value," +
                        "   fxRiskFactor.detail.value," +
                        "   fxRiskFactor.index.detail.toCurrencyCodeValue, " +
                        "   fxRiskFactor.index.detail.fromCurrencyCodeValue) " +
                        "  FROM PositionRiskFactorMapping mapping " +
                        "  JOIN mapping.priceRiskFactor priceRiskFactor " +
               "LEFT OUTER JOIN mapping.fxRiskFactor fxRiskFactor " +
                        " WHERE mapping.dealPosition.id = :positionId " +
                        "   AND  mapping.detail.priceTypeCodeValue = :priceTypeCode "),
        @NamedQuery(
                name = PositionRiskFactorMappingRepositoryBean.FIND_ALL_MAPPING_SUMMARIES,
                query = "SELECT new com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary(" +
                        "   mapping.id," +
                        "   mapping.dealPosition.id, " +
                        "   mapping.detail.priceTypeCodeValue, " +
                        "   priceRiskFactor.index.detail.currencyCodeValue, " +
                        "   priceRiskFactor.index.detail.unitOfMeasureCodeValue, " +
                        "   priceRiskFactor.detail.value," +
                        "   fxRiskFactor.detail.value," +
                        "   fxRiskFactor.index.detail.toCurrencyCodeValue, " +
                        "   fxRiskFactor.index.detail.fromCurrencyCodeValue) " +
                        "  FROM PositionRiskFactorMapping mapping " +
                        "  JOIN mapping.priceRiskFactor priceRiskFactor " +
               "LEFT OUTER JOIN mapping.fxRiskFactor fxRiskFactor " +
                        " WHERE mapping.dealPosition.id in (:positionIds) ")
})
public class PositionRiskFactorMapping extends AbstractEntity {

    private Integer id;

    private DealPosition dealPosition;

    private PriceRiskFactor priceRiskFactor;

    private FxRiskFactor fxRiskFactor;

    private PositionRiskFactorMappingDetail detail = new PositionRiskFactorMappingDetail();

    protected PositionRiskFactorMapping() {

    }

    public PositionRiskFactorMapping(
            DealPosition position,
            PriceRiskFactor priceRiskFactor) {
        this.priceRiskFactor = priceRiskFactor;
        position.addPositionRiskFactorMapping(this);
    }

    public PositionRiskFactorMapping(
            DealPosition position,
            PriceRiskFactor priceRiskFactor,
            FxRiskFactor fxRiskFactor) {
        this.priceRiskFactor = priceRiskFactor;
        this.fxRiskFactor = fxRiskFactor;
        position.addPositionRiskFactorMapping(this);
    }


    @Override
    @Transient
    public String getEntityName() {
        return "PositionRiskFactorMapping";
    }


    @Id
    @Column(name="ENTITY_ID", insertable = false, updatable = false)
    @SequenceGenerator(name="PosRfMapGen", sequenceName="POSITION_RISK_FACTOR_MAP_SQ", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "PosRfMapGen")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public static PositionRiskFactorMapping create(
            DealPosition position,
            PositionRiskFactorMappingSnapshot snapshot) {
        PositionRiskFactorMapping mapping;
        if (snapshot.getFxRiskFactorId() != null)
            mapping = new PositionRiskFactorMapping(
                    position,
                    getPriceRiskFactorRepository().load(snapshot.getPriceRiskFactorId()),
                    getFxRiskFactorRepository().load(snapshot.getFxRiskFactorId()));
        else
            mapping = new PositionRiskFactorMapping(
                    position,
                    getPriceRiskFactorRepository().load(snapshot.getPriceRiskFactorId()));

        mapping.getDetail().copyFrom(snapshot.getDetail());
        return mapping;
    }

    @Embedded
    public PositionRiskFactorMappingDetail getDetail() {
        return detail;
    }

    public void setDetail(PositionRiskFactorMappingDetail detail) {
        this.detail = detail;
    }

    @ManyToOne()
    @JoinColumn(name = "DEAL_POSITION_ID")
    public DealPosition getDealPosition() {
        return dealPosition;
    }

    public void setDealPosition(DealPosition dealPosition) {
        this.dealPosition = dealPosition;
    }

    @ManyToOne()
    @JoinColumn(name = "PRICE_RISK_FACTOR_ID")
    public PriceRiskFactor getPriceRiskFactor() {
        return priceRiskFactor;
    }

    public void setPriceRiskFactor(PriceRiskFactor priceRiskFactor) {
        this.priceRiskFactor = priceRiskFactor;
    }

    @ManyToOne()
    @JoinColumn(name = "FX_RISK_FACTOR_ID")
    public FxRiskFactor getFxRiskFactor() {
        return fxRiskFactor;
    }

    public void setFxRiskFactor(FxRiskFactor fxRiskFactor) {
        this.fxRiskFactor = fxRiskFactor;
    }

    @Override
    public EntityId generateEntityId() {
        return new EntityId(
                getId(),
                "",
                "",
                false);
    }

    @Override
    protected void validate() throws OBValidationException {

    }

    @Transient
    protected static  PriceRiskFactorRepository getPriceRiskFactorRepository() {
        return (PriceRiskFactorRepository) ApplicationContextFactory.getBean(PriceRiskFactorRepository.BEAN_NAME);
    }

    @Transient
    protected static FxRiskFactorRepository getFxRiskFactorRepository() {
        return (FxRiskFactorRepository) ApplicationContextFactory.getBean(FxRiskFactorRepository.BEAN_NAME);
    }

}
