package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourPriceRiskFactorIdMap;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "DEAL_HOURLY_POSITION_VIEW")
@Immutable
@NamedQueries({
        @NamedQuery(
                name = DealHourlyPositionRepositoryBean.FIND_DEAL_HOURLY_POSITION_VIEWS,
                query = "SELECT position " +
                        "  FROM DealHourlyPositionView position " +
                        " WHERE position.dealId in (:dealIds) " +
                        "   AND position.detail.currencyCodeValue = :currencyCode  " +
                        "   AND position.detail.createdDateTime = :createdDateTime " +
                      "ORDER BY position.detail.startDate ")
})
public class DealHourlyPositionView extends AbstractEntity {
    private Integer id;

    private Integer dealId;

    private Integer powerProfileId;

    private HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap = new HourPriceRiskFactorIdMap();

    private DealHourlyPositionDetail detail = new DealHourlyPositionDetail();

    private HourFixedValueDayDetail hourFixedValueDayDetail = new HourFixedValueDayDetail();

    @Id
    @Column(name="ENTITY_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer dealDayId) {
        this.id = dealDayId;
    }

    @Column(name = "DEAL_ID")
    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

    @Column(name = "POWER_PROFILE_ID")
    public Integer getPowerProfileId() {
        return powerProfileId;
    }

    public void setPowerProfileId(Integer powerProfileId) {
        this.powerProfileId = powerProfileId;
    }

    @Embedded
    public DealHourlyPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(DealHourlyPositionDetail detail) {
        this.detail = detail;
    }

    @Embedded
    public HourPriceRiskFactorIdMap getHourPriceRiskFactorIdMap() {
        return hourPriceRiskFactorIdMap;
    }

    public void setHourPriceRiskFactorIdMap(HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap) {
        this.hourPriceRiskFactorIdMap = hourPriceRiskFactorIdMap;
    }

    @Embedded
    public HourFixedValueDayDetail getHourQuantityDayDetail() {
        return hourFixedValueDayDetail;
    }

    public void setHourQuantityDayDetail(HourFixedValueDayDetail hourFixedValueDayDetail) {
        this.hourFixedValueDayDetail = hourFixedValueDayDetail;
    }

    @Override
    protected void validate() throws OBValidationException {

    }

}
