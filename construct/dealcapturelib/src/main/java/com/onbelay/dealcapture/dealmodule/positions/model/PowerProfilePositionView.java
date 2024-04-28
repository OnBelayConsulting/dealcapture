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
                        "   AND position.detail.createdDateTime = :createdDateTime " +
                        "ORDER BY position.detail.startDate "),
        @NamedQuery(
                name = PowerProfilePositionRepositoryBean.FIND_PROFILE_POSITION_VIEWS_BY_CONTEXT,
                query = "SELECT position " +
                        "  FROM PowerProfilePositionView position " +
                        " WHERE position.detail.startDate >= :startDate" +
                        "   AND position.detail.endDate  <= :endDate " +
                        "   AND position.detail.createdDateTime = :createdDateTime " +
                        "ORDER BY position.detail.startDate ")
})
public class PowerProfilePositionView extends AbstractEntity {
    private Integer id;

    private Integer powerProfileId;

    private Integer priceIndexId;

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

    @Column(name = "PRICE_INDEX_ID")
    public Integer getPriceIndexId() {
        return priceIndexId;
    }

    public void setPriceIndexId(Integer priceIndexId) {
        this.priceIndexId = priceIndexId;
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
