package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealDetail;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import jakarta.persistence.*;

@Entity
@Table(name = "FINANCIAL_SWAP_DEAL_AUDIT")
public class FinancialSwapDealAudit  extends BaseDealAudit {

    private FinancialSwapDealDetail detail = new FinancialSwapDealDetail();

    private PriceIndex paysPriceIndex;

    private PriceIndex receivesPriceIndex;

    public FinancialSwapDealAudit() {

    }

    public FinancialSwapDealAudit(BaseDeal deal) {
        super(deal);
    }

    protected static FinancialSwapDealAudit create(FinancialSwapDeal deal) {
        FinancialSwapDealAudit audit = new FinancialSwapDealAudit(deal);
        audit.copyFrom(deal);

        return audit;
    }

    @Override
    public void copyFrom(TemporalAbstractEntity entity) {
        super.copyFrom(entity);
        FinancialSwapDeal deal = (FinancialSwapDeal) entity;
        this.paysPriceIndex = deal.getPaysPriceIndex();
        this.receivesPriceIndex = deal.getReceivesPriceIndex();
        detail.copyFrom(deal.getDetail());
    }

    @ManyToOne()
    @JoinColumn(name = "PAYS_INDEX_ID")
    public PriceIndex getPaysPriceIndex() {
        return paysPriceIndex;
    }

    public void setPaysPriceIndex(PriceIndex paysPriceIndex) {
        this.paysPriceIndex = paysPriceIndex;
    }

    @ManyToOne
    @JoinColumn(name = "RECEIVES_INDEX_ID")
    public PriceIndex getReceivesPriceIndex() {
        return receivesPriceIndex;
    }

    public void setReceivesPriceIndex(PriceIndex receivesPriceIndex) {
        this.receivesPriceIndex = receivesPriceIndex;
    }



    @Embedded
    public FinancialSwapDealDetail getDetail() {
        return detail;
    }

    public void setDetail(FinancialSwapDealDetail detail) {
        this.detail = detail;
    }
}
