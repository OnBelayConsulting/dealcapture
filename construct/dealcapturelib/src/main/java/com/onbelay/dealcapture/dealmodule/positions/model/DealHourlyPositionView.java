package com.onbelay.dealcapture.dealmodule.positions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourPriceRiskFactorIdMap;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

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

    private Integer powerProfilePositionId;

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

    @Column(name = "POWER_PROFILE_POSITION_ID")
    public Integer getPowerProfilePositionId() {
        return powerProfilePositionId;
    }

    public void setPowerProfilePositionId(Integer powerProfileId) {
        this.powerProfilePositionId = powerProfileId;
    }

    @Embedded
    public DealHourlyPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(DealHourlyPositionDetail detail) {
        this.detail = detail;
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
        if (hourPriceRiskFactorIdMap != null)
            return hourPriceRiskFactorIdMap;
        else
            return new HourPriceRiskFactorIdMap();
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
        if (hourFixedValueDayDetail != null)
            return hourFixedValueDayDetail;
        else
            return new HourFixedValueDayDetail();
    }

    public void setHourFixedValueDayDetail(HourFixedValueDayDetail hourFixedValueDayDetail) {
        this.hourFixedValueDayDetail = hourFixedValueDayDetail;
    }


    @Override
    protected void validate() throws OBValidationException {

    }

    @Transient
    @JsonIgnore
    public Price getPrice(int hourEnding, ValuationIndexManager valuationIndexManager) {

        Integer riskFactorId = getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(hourEnding);
        if (riskFactorId == null)
            return null;

        PriceRiskFactorSnapshot snapshot = valuationIndexManager.getPriceRiskFactor(riskFactorId);

        return valuationIndexManager.generatePrice(
                snapshot.getPriceIndexId().getId(),
                snapshot.getDetail().getValue());
    }



}
