package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.positions.repository.PowerProfilePositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourPriceRiskFactorIdMap;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import jakarta.persistence.*;

@Entity
@Table(name = "DEAL_HOURLY_POSITION")
@NamedQueries({
        @NamedQuery(
                name = DealHourlyPositionRepositoryBean.FIND_IDS_BY_DEAL,
                query = "SELECT position.id " +
                        "  FROM DealHourlyPosition position " +
                        " WHERE position.deal.id = :dealId " +
                      "ORDER BY position.detail.startDate "),
        @NamedQuery(
                name = DealHourlyPositionRepositoryBean.FIND_BY_DEAL,
                query = "SELECT position " +
                        "  FROM DealHourlyPosition position " +
                        " WHERE position.deal.id = :dealId " +
                      "ORDER BY position.detail.startDate ")
})
public class DealHourlyPosition extends AbstractEntity {

    private Integer id;

    private BaseDeal deal;

    private PowerProfilePosition powerProfilePosition;

    private PriceIndex priceIndex;

    private FxRiskFactor fxRiskFactor;

    private HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap = new HourPriceRiskFactorIdMap();

    private DealHourlyPositionDetail detail = new DealHourlyPositionDetail();

    private HourFixedValueDayDetail hourFixedValueDayDetail = new HourFixedValueDayDetail();

    @Id
    @Column(name="ENTITY_ID", insertable =  false, updatable = false)
    @SequenceGenerator(name="dealDealHourlyPosGen", sequenceName="DEAL_HOURLY_POSITION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "dealDealHourlyPosGen")
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
    @JoinColumn(name = "POWER_PROFILE_POSITION_ID")
    public PowerProfilePosition getPowerProfilePosition() {
        return powerProfilePosition;
    }

    public void setPowerProfilePosition(PowerProfilePosition powerProfilePosition) {
        this.powerProfilePosition = powerProfilePosition;
    }

    @ManyToOne
    @JoinColumn(name = "PRICE_INDEX_ID")
    public PriceIndex getPriceIndex() {
        return priceIndex;
    }

    public void setPriceIndex(PriceIndex priceIndex) {
        this.priceIndex = priceIndex;
    }

    @ManyToOne
    @JoinColumn(name = "FX_RISK_FACTOR_ID")
    public FxRiskFactor getFxRiskFactor() {
        return fxRiskFactor;
    }

    public void setFxRiskFactor(FxRiskFactor fxRiskFactor) {
        this.fxRiskFactor = fxRiskFactor;
    }

    @Embedded
    public DealHourlyPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(DealHourlyPositionDetail costDetail) {
        this.detail = costDetail;
    }

    @Embedded
    protected HourPriceRiskFactorIdMap getInternalHourPriceRiskFactorIdMap() {
        return hourPriceRiskFactorIdMap;
    }

    protected void setInternalHourPriceRiskFactorIdMap(HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap) {
        this.hourPriceRiskFactorIdMap = hourPriceRiskFactorIdMap;
    }

    @Transient
    public HourPriceRiskFactorIdMap getHourPriceRiskFactorIdMap() {
        if (hourPriceRiskFactorIdMap == null)
            hourPriceRiskFactorIdMap =  new HourPriceRiskFactorIdMap();
        return hourPriceRiskFactorIdMap;
    }

    public void setHourPriceRiskFactorIdMap(HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap) {
        this.hourPriceRiskFactorIdMap = hourPriceRiskFactorIdMap;
    }


    @Embedded
    protected HourFixedValueDayDetail getInternalHourFixedValueDayDetail() {
        return hourFixedValueDayDetail;
    }

    protected void setInternalHourFixedValueDayDetail(HourFixedValueDayDetail hourFixedValueDayDetail) {
        this.hourFixedValueDayDetail = hourFixedValueDayDetail;
    }


    @Transient
    public HourFixedValueDayDetail getHourFixedValueDayDetail() {
        if (hourFixedValueDayDetail == null)
            hourFixedValueDayDetail =  new HourFixedValueDayDetail();
        return hourFixedValueDayDetail;
    }

    public void setHourFixedValueDayDetail(HourFixedValueDayDetail hourFixedValueDayDetail) {
        this.hourFixedValueDayDetail = hourFixedValueDayDetail;
    }


    @Override
    protected void validate() throws OBValidationException {

    }

    public void createWith(DealHourlyPositionSnapshot snapshot) {
        this.deal = getDealRepository().load(snapshot.getDealId());
        this.detail.copyFrom(snapshot.getDetail());
        updateRelationships(snapshot);
    }

    public void updateWith(DealHourlyPositionSnapshot snapshot) {
        this.detail.copyFrom(snapshot.getDetail());
        updateRelationships(snapshot);
    }

    private void updateRelationships(DealHourlyPositionSnapshot snapshot) {

        if (snapshot.getPowerProfilePositionId() != null)
            this.powerProfilePosition = getPowerProfilePositionRepository().load(snapshot.getPowerProfilePositionId());

        if (snapshot.getFxRiskFactorId() != null)
            this.fxRiskFactor = getFxRiskFactorRepository().load(snapshot.getFxRiskFactorId());

        if (snapshot.getPriceIndexId() != null)
            this.priceIndex = getPriceIndexRepository().load(snapshot.getPriceIndexId());
    }

    @Transient
    protected static DealRepository getDealRepository() {
        return (DealRepository) ApplicationContextFactory.getBean(DealRepository.BEAN_NAME);
    }

    @Transient
    protected static PriceIndexRepository getPriceIndexRepository() {
        return (PriceIndexRepository) ApplicationContextFactory.getBean(PriceIndexRepository.BEAN_NAME);
    }

    @Transient
    protected static FxRiskFactorRepository getFxRiskFactorRepository() {
        return (FxRiskFactorRepository) ApplicationContextFactory.getBean(FxRiskFactorRepository.BEAN_NAME);
    }


    @Transient
    protected static PowerProfilePositionRepository getPowerProfilePositionRepository() {
        return (PowerProfilePositionRepository) ApplicationContextFactory.getBean(PowerProfilePositionRepository.BEAN_NAME);
    }

}
