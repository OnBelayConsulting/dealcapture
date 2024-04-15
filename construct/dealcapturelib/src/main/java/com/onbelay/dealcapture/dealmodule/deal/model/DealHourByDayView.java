package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDayDetail;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "DEAL_HOUR_BY_DAY_VIEW")
@Immutable
@NamedQueries({
        @NamedQuery(
                name = DealHourByDayRepositoryBean.FETCH_DEAL_HOUR_VIEWS,
                query = " SELECT view " +
                        "   FROM DealHourByDayView view " +
                        "  WHERE view.dealId = :dealId " +
                     "  ORDER BY view.detail.dealDayDate"),
        @NamedQuery(
                name = DealHourByDayRepositoryBean.FETCH_DEAL_HOUR_VIEWS_BY_TYPE,
                query = "  SELECT view " +
                        "    FROM DealHourByDayView view " +
                        "   WHERE view.dealId = :dealId " +
                        "     AND view.detail.dayTypeCodeValue = :dayTypeCode " +
                        "     AND view.detail.dealDayDate >= :fromDate " +
                        "     AND view.detail.dealDayDate < :toDate" +
                      "  ORDER BY view.detail.dealDayDate"),
        @NamedQuery(
                name = DealHourByDayRepositoryBean.FETCH_ALL_DEAL_HOUR_VIEWS_BY_DATE,
                query = "  SELECT view " +
                        "    FROM DealHourByDayView view " +
                        "   WHERE view.dealId in (:dealIds) " +
                        "     AND view.detail.dealDayDate >= :fromDate " +
                        "     AND view.detail.dealDayDate < :toDate" +
                      "  ORDER BY view.dealId, view.detail.dealDayDate")
})
public class DealHourByDayView extends AbstractEntity {

    private Integer id;

    private Integer dealId;

    private DealHourByDayDetail detail = new DealHourByDayDetail();

    private String reportingCurrencyCodeValue;

    private String volumeUnitOfMeasureCodeValue;

    @Id
    @Column(name="ENTITY_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer viewId) {
        this.id = viewId;
    }

    @Embedded
    public DealHourByDayDetail getDetail() {
        return detail;
    }

    public void setDetail(DealHourByDayDetail detail) {
        this.detail = detail;
    }

    @Column(name = "DEAL_ID")
    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

    @Column(name = "REPORTING_CURRENCY_CODE")
    public String getReportingCurrencyCodeValue() {
        return reportingCurrencyCodeValue;
    }

    public void setReportingCurrencyCodeValue(String reportingCurrencyCodeValue) {
        this.reportingCurrencyCodeValue = reportingCurrencyCodeValue;
    }

    @Column(name = "VOLUME_UOM_CODE")
    public String getVolumeUnitOfMeasureCodeValue() {
        return volumeUnitOfMeasureCodeValue;
    }

    public void setVolumeUnitOfMeasureCodeValue(String volumeUnitOfMeasureCodeValue) {
        this.volumeUnitOfMeasureCodeValue = volumeUnitOfMeasureCodeValue;
    }

    @Override
    protected void validate() throws OBValidationException {

    }

}
