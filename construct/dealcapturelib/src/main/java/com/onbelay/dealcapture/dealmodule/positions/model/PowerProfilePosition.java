package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourPriceDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourPriceRiskFactorIdMap;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import jakarta.persistence.*;

@Entity
@Table(name = "POWER_PROFILE_POSITION")
@NamedQueries({
        @NamedQuery(
                name = PowerProfilePositionRepositoryBean.FIND_BY_POWER_PROFILE,
                query = "SELECT position " +
                        "  FROM PowerProfilePosition position " +
                        " WHERE position.powerProfile.id = :powerProfileId " +
                      "ORDER BY position.detail.startDate, position.detail.powerFlowCodeValue, position.detail.basisNo ")
})
public class PowerProfilePosition extends AbstractEntity {

    private Integer id;

    private PowerProfile powerProfile;

    private PriceIndex priceIndex;

    private PowerProfilePositionDetail detail = new PowerProfilePositionDetail();

    private HourPriceDayDetail hourPriceDayDetail = new HourPriceDayDetail();

    private HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap = new HourPriceRiskFactorIdMap();


    @Id
    @Column(name="ENTITY_ID", insertable =  false, updatable = false)
    @SequenceGenerator(name="powerProfilePosGen", sequenceName="COST_POSITION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "powerProfilePosGen")
    public Integer getId() {
        return id;
    }

    private void setId(Integer dealId) {
        this.id = dealId;
    }

    @ManyToOne
    @JoinColumn(name = "POWER_PROFILE_ID")
    public PowerProfile getPowerProfile() {
        return powerProfile;
    }

    public void setPowerProfile(PowerProfile powerProfile) {
        this.powerProfile = powerProfile;
    }


    @ManyToOne
    @JoinColumn(name = "PRICE_INDEX_ID")
    public PriceIndex getPriceIndex() {
        return priceIndex;
    }

    public void setPriceIndex(PriceIndex priceIndex) {
        this.priceIndex = priceIndex;
    }

    @Transient
    public HourPriceDayDetail getHourPriceDayDetail() {
        if (hourPriceDayDetail != null)
            return hourPriceDayDetail;
        return new HourPriceDayDetail();
    }

    public void setHourPriceDayDetail(HourPriceDayDetail hourPriceDayDetail) {
        this.hourPriceDayDetail = hourPriceDayDetail;
    }

    @Embedded
    public HourPriceDayDetail getInternalPriceDetail() {
        return hourPriceDayDetail;
    }

    public void setInternalPriceDetail(HourPriceDayDetail priceDetail) {
        this.hourPriceDayDetail = priceDetail;
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
    public HourPriceRiskFactorIdMap getInternalHourPriceRiskFactorIdMap() {
        return hourPriceRiskFactorIdMap;
    }

    public void setInternalHourPriceRiskFactorIdMap(HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap) {
        this.hourPriceRiskFactorIdMap = hourPriceRiskFactorIdMap;
    }


    @Embedded
    public PowerProfilePositionDetail getDetail() {
        return detail;
    }

    public void setDetail(PowerProfilePositionDetail detail) {
        this.detail = detail;
    }

    @Override
    protected void validate() throws OBValidationException {

    }

    public void createWith(PowerProfilePositionSnapshot snapshot) {
        this.powerProfile = getPowerProfileRepository().load(snapshot.getPowerProfileId());
        this.detail.copyFrom(snapshot.getDetail());
        this.hourPriceDayDetail.copyFrom(snapshot.getHourPriceDayDetail());
        updateRelationships(snapshot);
        save();
    }

    public void updateWith(PowerProfilePositionSnapshot snapshot) {
        this.detail.copyFrom(snapshot.getDetail());
        this.hourPriceDayDetail.copyFrom(snapshot.getHourPriceDayDetail());
        updateRelationships(snapshot);
        update();
    }

    private void updateRelationships(PowerProfilePositionSnapshot snapshot) {
        if (snapshot.getPriceIndexId() != null)
            this.priceIndex = getPriceIndexRepository().load(snapshot.getPriceIndexId());
    }

    @Transient
    protected static PowerProfileRepository getPowerProfileRepository() {
        return (PowerProfileRepository) ApplicationContextFactory.getBean(PowerProfileRepository.BEAN_NAME);
    }

    @Transient
    protected static PriceIndexRepository getPriceIndexRepository() {
        return (PriceIndexRepository) ApplicationContextFactory.getBean(PriceIndexRepository.BEAN_NAME);
    }


}
