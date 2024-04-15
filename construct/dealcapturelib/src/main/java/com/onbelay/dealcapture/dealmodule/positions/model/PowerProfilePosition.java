package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourPriceDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import jakarta.persistence.*;

@Entity
@Table(name = "POWER_PROFILE_POSITION")
@NamedQueries({
        @NamedQuery(
                name = PowerProfilePositionRepositoryBean.FIND_BY_POWER_PROFILE,
                query = "SELECT position.id " +
                        "  FROM PowerProfilePosition position " +
                        " WHERE position.powerProfile.id = :powerProfileId " +
                      "ORDER BY position.detail.startDate ")
})
public class PowerProfilePosition extends AbstractEntity {

    private Integer id;

    private PowerProfile powerProfile;

    private PowerProfilePositionDetail detail = new PowerProfilePositionDetail();

    private HourPriceDayDetail priceDetail = new HourPriceDayDetail();

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

    @Embedded
    public HourPriceDayDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(HourPriceDayDetail costDetail) {
        this.priceDetail = costDetail;
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
        this.priceDetail.copyFrom(snapshot.getHourPriceDayDetail());
    }

    public void updateWith(PowerProfilePositionSnapshot snapshot) {
        this.detail.copyFrom(snapshot.getDetail());
        this.priceDetail.copyFrom(snapshot.getHourPriceDayDetail());
    }

    @Transient
    protected static PowerProfileRepository getPowerProfileRepository() {
        return (PowerProfileRepository) ApplicationContextFactory.getBean(PowerProfileRepository.BEAN_NAME);
    }

}
