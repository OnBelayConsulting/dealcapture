package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.OptionDealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealDetail;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@DiscriminatorValue("VanillaOption")
public class VanillaOptionDealSummary extends DealSummary {

    private Integer underlyingPriceIndexId;

    private OptionDealDetail detail = new OptionDealDetail();


    public VanillaOptionDealSummary() {
        super(DealTypeCode.VANILLA_OPTION);
    }

    @Column(name="UNDERLYING_INDEX_ID")
    public Integer getUnderlyingPriceIndexId() {
        return underlyingPriceIndexId;
    }

    public void setUnderlyingPriceIndexId(Integer dealPriceIndexId) {
        this.underlyingPriceIndexId = dealPriceIndexId;
    }

    @Embedded
    public OptionDealDetail getDetail() {
        return detail;
    }

    public void setDetail(OptionDealDetail detail) {
        this.detail = detail;
    }
}
