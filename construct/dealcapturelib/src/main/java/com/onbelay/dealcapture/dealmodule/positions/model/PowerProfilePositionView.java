package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourPriceRiskFactorIdMap;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionDetail;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "POWER_PROFILE_POSITION_VIEW")
@Immutable
@NamedQueries({
        @NamedQuery(
                name = PowerProfilePositionRepositoryBean.FIND_PROFILE_POSITION_VIEWS,
                query = "SELECT position " +
                        "  FROM PowerProfilePositionView position " +
                        " WHERE position.powerProfileId in (:powerProfileIds) " +
                        "   AND detail.currencyCodeValue = :currencyCode " +
                        "   AND detail.createdDateTime = :createdDateTime " +
                        "ORDER BY position.detail.startDate ")
})
public class PowerProfilePositionView extends AbstractEntity {
    private Integer id;

    private Integer powerProfileId;

    private PowerProfilePositionDetail detail = new PowerProfilePositionDetail();

    private HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap = new HourPriceRiskFactorIdMap();

    @Id
    @Column(name="ENTITY_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer dealDayId) {
        this.id = dealDayId;
    }

    @Column(name = "POWER_PROFILE_ID")
    public Integer getPowerProfileId() {
        return powerProfileId;
    }

    public void setPowerProfileId(Integer dealId) {
        this.powerProfileId = dealId;
    }

    @Embedded
    public PowerProfilePositionDetail getDetail() {
        return detail;
    }

    public void setDetail(PowerProfilePositionDetail detail) {
        this.detail = detail;
    }

    @Embedded
    public HourPriceRiskFactorIdMap getHourPriceRiskFactorIdMap() {
        return hourPriceRiskFactorIdMap;
    }

    public void setHourPriceRiskFactorIdMap(HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap) {
        this.hourPriceRiskFactorIdMap = hourPriceRiskFactorIdMap;
    }

    @Override
    protected void validate() throws OBValidationException {

    }
}