package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayDetail;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "DEAL_DAY_VIEW")
@Immutable
@NamedQueries({
        @NamedQuery(
                name = DealDayRepositoryBean.FETCH_DEAL_DAY_VIEWS,
                query = " SELECT dealDay " +
                        "   FROM DealDayView dealDay " +
                        "  WHERE dealDay.dealId = :dealId " +
                     "  ORDER BY dealDay.detail.dealDayDate"),
        @NamedQuery(
                name = DealDayRepositoryBean.FETCH_DEAL_DAY_VIEWS_BY_TYPE,
                query = "  SELECT dealDay " +
                        "    FROM DealDayView dealDay " +
                        "   WHERE dealDay.dealId = :dealId " +
                        "     AND dealDay.detail.dayTypeCodeValue = :dayTypeCode " +
                        "     AND dealDay.detail.dealDayDate >= :fromDate " +
                        "     AND dealDay.detail.dealDayDate < :toDate" +
                      "  ORDER BY dealDay.detail.dealDayDate"),
        @NamedQuery(
                name = DealDayRepositoryBean.FETCH_ALL_DEAL_DAY_VIEWS_BY_DATE,
                query = "  SELECT dealDay " +
                        "    FROM DealDayView dealDay " +
                        "   WHERE dealDay.dealId in (:dealIds) " +
                        "     AND dealDay.detail.dealDayDate >= :fromDate " +
                        "     AND dealDay.detail.dealDayDate < :toDate" +
                      "  ORDER BY dealDay.dealId, dealDay.detail.dealDayDate")
})
public class DealDayView extends AbstractEntity {

    private Integer id;

    private Integer dealId;

    private DealDayDetail detail = new DealDayDetail();

    private String reportingCurrencyCodeValue;

    private String volumeUnitOfMeasureCodeValue;

    @Id
    @Column(name="ENTITY_ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer dealDayId) {
        this.id = dealDayId;
    }

    @Embedded
    public DealDayDetail getDetail() {
        return detail;
    }

    public void setDetail(DealDayDetail detail) {
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
