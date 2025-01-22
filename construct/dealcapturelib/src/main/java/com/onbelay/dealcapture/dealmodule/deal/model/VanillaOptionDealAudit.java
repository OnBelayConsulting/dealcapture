package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.OptionDealDetail;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import jakarta.persistence.*;

@Entity
@Table(name = "VANILLA_OPTION_DEAL_AUDIT")
public class VanillaOptionDealAudit extends BaseDealAudit {

    private OptionDealDetail detail = new OptionDealDetail();

    private PriceIndex underlyingPriceIndex;

    public VanillaOptionDealAudit() {

    }

    public VanillaOptionDealAudit(BaseDeal deal) {
        super(deal);
    }

    protected static VanillaOptionDealAudit create(VanillaOptionDeal deal) {
        VanillaOptionDealAudit audit = new VanillaOptionDealAudit(deal);
        audit.copyFrom(deal);

        return audit;
    }

    @Override
    public void copyFrom(TemporalAbstractEntity entity) {
        super.copyFrom(entity);
        VanillaOptionDeal deal = (VanillaOptionDeal) entity;
        this.underlyingPriceIndex = deal.getUnderlyingPriceIndex();
        detail.copyFrom(deal.getDetail());
    }

    @ManyToOne()
    @JoinColumn(name = "UNDERLYING_INDEX_ID")
    public PriceIndex getUnderlyingPriceIndex() {
        return underlyingPriceIndex;
    }

    public void setUnderlyingPriceIndex(PriceIndex paysPriceIndex) {
        this.underlyingPriceIndex = paysPriceIndex;
    }


    @Embedded
    public OptionDealDetail getDetail() {
        return detail;
    }

    public void setDetail(OptionDealDetail detail) {
        this.detail = detail;
    }
}
