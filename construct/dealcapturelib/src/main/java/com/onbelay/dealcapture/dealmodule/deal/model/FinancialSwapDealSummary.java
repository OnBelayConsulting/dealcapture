package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealDetail;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@DiscriminatorValue("FinancialSwap")
public class FinancialSwapDealSummary extends DealSummary {

    private Integer paysIndexId;
    private Integer receivesIndexId;

    private FinancialSwapDealDetail detail = new FinancialSwapDealDetail();

    public FinancialSwapDealSummary() {
        super(DealTypeCode.FINANCIAL_SWAP);
    }

    @Column(name = "PAYS_INDEX_ID")
    public Integer getPaysIndexId() {
        return paysIndexId;
    }

    public void setPaysIndexId(Integer paysIndexId) {
        this.paysIndexId = paysIndexId;
    }

    @Column(name = "RECEIVES_INDEX_ID")
    public Integer getReceivesIndexId() {
        return receivesIndexId;
    }

    public void setReceivesIndexId(Integer receivesIndexId) {
        this.receivesIndexId = receivesIndexId;
    }

    @Embedded
    public FinancialSwapDealDetail getDetail() {
        return detail;
    }

    public void setDetail(FinancialSwapDealDetail detail) {
        this.detail = detail;
    }
}
