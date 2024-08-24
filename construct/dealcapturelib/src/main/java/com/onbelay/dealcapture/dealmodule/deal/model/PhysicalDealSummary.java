package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealDetail;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@DiscriminatorValue("PHY")
public class PhysicalDealSummary extends DealSummary {

    private Integer dealPriceIndexId;
    private Integer marketIndexId;

    private PhysicalDealDetail detail = new PhysicalDealDetail();


    public PhysicalDealSummary() {
        super(DealTypeCode.PHYSICAL_DEAL);
    }

    @Column(name="DEAL_PRICE_INDEX_ID")
    public Integer getDealPriceIndexId() {
        return dealPriceIndexId;
    }

    public void setDealPriceIndexId(Integer dealPriceIndexId) {
        this.dealPriceIndexId = dealPriceIndexId;
    }

    @Column(name="MARKET_PRICE_INDEX_ID")
    public Integer getMarketIndexId() {
        return marketIndexId;
    }

    public void setMarketIndexId(Integer marketIndexId) {
        this.marketIndexId = marketIndexId;
    }

    @Embedded
    public PhysicalDealDetail getDetail() {
        return detail;
    }

    public void setDetail(PhysicalDealDetail detail) {
        this.detail = detail;
    }
}
