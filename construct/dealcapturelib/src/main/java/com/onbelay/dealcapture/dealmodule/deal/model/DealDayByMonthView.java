package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthDetail;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "DEAL_DAY_BY_MTH_VIEW")
@Immutable
@NamedQueries({
        @NamedQuery(
                name = DealDayByMonthRepositoryBean.FETCH_DEAL_DAY_VIEWS,
                query = " SELECT view " +
                        "   FROM DealDayByMonthView view " +
                        "  WHERE view.dealId = :dealId " +
                     "  ORDER BY view.detail.dealMonthDate"),
        @NamedQuery(
                name = DealDayByMonthRepositoryBean.FETCH_DEAL_DAY_VIEWS_BY_TYPE,
                query = "  SELECT view " +
                        "    FROM DealDayByMonthView view " +
                        "   WHERE view.dealId = :dealId " +
                        "     AND view.detail.dayTypeCodeValue = :dayTypeCode " +
                        "     AND view.detail.dealMonthDate >= :fromDate " +
                        "     AND view.detail.dealMonthDate <= :toDate" +
                      "  ORDER BY view.detail.dealMonthDate"),
        @NamedQuery(
                name = DealDayByMonthRepositoryBean.FETCH_ALL_DEAL_DAY_VIEWS_BY_DATE,
                query = "  SELECT view " +
                        "    FROM DealDayByMonthView view " +
                        "   WHERE view.dealId in (:dealIds) " +
                        "     AND view.detail.dealMonthDate >= :fromDate " +
                        "     AND view.detail.dealMonthDate <= :toDate" +
                      "  ORDER BY view.dealId, view.detail.dealMonthDate")
})
public class DealDayByMonthView extends AbstractEntity {

    private Integer id;

    private Integer dealId;

    private DealDayByMonthDetail detail = new DealDayByMonthDetail();

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
    public DealDayByMonthDetail getDetail() {
        return detail;
    }

    public void setDetail(DealDayByMonthDetail detail) {
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
