/*
 Copyright 2019, OnBelay Consulting Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  
 */
package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexRepositoryBean;
import jakarta.persistence.*;

/**
 * A financial swap deal consists of a pays leg and a receives leg. The buy and sell on the deal indicates
 * who is paying and who is selling.
 *
 * A company that buys a fixed for float financial swap will  pay a fixed price and receive float index.
 * A company that sells a fixed  for float financial swap will receive the fixed  and pay the float index.
 *
 * Subtypes of financial swaps are as follows:
 * <:ol>
 * <:li>fixed for float = pays fixed receives index
 * <:li>fixed for power profile - pays fixed and receives power profile indices.
 * <:li>float for float = pays float receives float
 * <:li>float for power profile - pays float and receives power profile indices.
 * <:li>float plus fixed for float =  pays float plus a positive or negative fixed value for a float.
 * <:li>float plus for power profile - pays float and fixed prices and receives power profile indices.
 * </:ol>
 *
 *
 */
@Entity
@Table (name = "FINANCIAL_SWAP_DEAL")
public class FinancialSwapDeal extends BaseDeal {

    private PriceIndex paysPriceIndex;

    private PriceIndex receivesPriceIndex;

    private FinancialSwapDealDetail detail = new FinancialSwapDealDetail();


    public FinancialSwapDeal() {
        super(DealTypeCode.FINANCIAL_SWAP);
    }


    public static FinancialSwapDeal create(FinancialSwapDealSnapshot snapshot) {
        FinancialSwapDeal deal = new FinancialSwapDeal();
        deal.createWith(snapshot);
        return deal;
    }


    @Override
    @Transient
    public String getEntityName() {
        return "FinancialSwap";
    }


    @Embedded
    public FinancialSwapDealDetail getDetail() {
        return detail;
    }

    public void setDetail(FinancialSwapDealDetail detail) {
        this.detail = detail;
    }

    public void createWith(BaseDealSnapshot dealSnapshot) {
        super.createWith(dealSnapshot);
        FinancialSwapDealSnapshot snapshot = (FinancialSwapDealSnapshot) dealSnapshot;
        detail.copyFrom(snapshot.getDetail());
        updateRelationships(snapshot);
        save();
    }

    public void updateWith(BaseDealSnapshot dealSnapshot) {
        super.updateWith(dealSnapshot);
        FinancialSwapDealSnapshot snapshot = (FinancialSwapDealSnapshot) dealSnapshot;
        detail.copyFrom(snapshot.getDetail());
        updateRelationships(snapshot);
        update();
    }

    protected void updateRelationships(BaseDealSnapshot baseSnapshot) {
        super.updateRelationships(baseSnapshot);

        FinancialSwapDealSnapshot snapshot = (FinancialSwapDealSnapshot) baseSnapshot;

        if (snapshot.getPaysPriceIndexId() != null)
            this.paysPriceIndex = getPriceIndexRepository().load(snapshot.getPaysPriceIndexId());

        if (snapshot.getReceivesPriceIndexId() != null)
            this.receivesPriceIndex = getPriceIndexRepository().load(snapshot.getReceivesPriceIndexId());

    }

    @Override
    protected void validate() throws OBValidationException {
        super.validate();
        detail.validate();

        if (detail.getPaysValuationCode() == ValuationCode.FIXED || detail.getPaysValuationCode() == ValuationCode.INDEX_PLUS) {
            if (getDealDetail().isFixedPriceMissing())
                throw new OBValidationException(DealErrorCode.MISSING_FIXED_PRICE_VALUE.getCode());
        }

        if (detail.getPaysValuationCode() == ValuationCode.INDEX) {
            if (paysPriceIndex == null)
                throw new OBValidationException(DealErrorCode.MISSING_PAYS_PRICE.getCode());
        }

        if (detail.getReceivesValuationCode() == ValuationCode.INDEX)
            if (receivesPriceIndex == null)
                throw new OBValidationException(DealErrorCode.MISSING_RECEIVES_PRICE.getCode());


        if (detail.getReceivesValuationCode() == ValuationCode.POWER_PROFILE)
            if (getPowerProfile() == null)
                throw new OBValidationException(DealErrorCode.MISSING_MARKET_POWER_PROFILE.getCode());


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

    @Override
    protected AuditAbstractEntity createHistory() {
        return FinancialSwapDealAudit.create(this);
    }
}
