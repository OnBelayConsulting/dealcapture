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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.OptionDealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.VanillaOptionDealSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import jakarta.persistence.*;

/**
 * Represents a vanilla option on a commodity.
 *
 */
@Entity
@Table (name = "VANILLA_OPTION_DEAL")
public class VanillaOptionDeal extends BaseDeal {

    private PriceIndex underlyingPriceIndex;

    private OptionDealDetail detail = new OptionDealDetail();


    public VanillaOptionDeal() {
        super(DealTypeCode.VANILLA_OPTION);
    }


    public static VanillaOptionDeal create(VanillaOptionDealSnapshot snapshot) {
        VanillaOptionDeal deal = new VanillaOptionDeal();
        deal.createWith(snapshot);
        return deal;
    }


    @Override
    @Transient
    public String getEntityName() {
        return "VanillaOptionDeal";
    }


    @Embedded
    public OptionDealDetail getDetail() {
        return detail;
    }

    public void setDetail(OptionDealDetail detail) {
        this.detail = detail;
    }

    public void createWith(BaseDealSnapshot dealSnapshot) {
        super.createWith(dealSnapshot);
        VanillaOptionDealSnapshot snapshot = (VanillaOptionDealSnapshot) dealSnapshot;
        detail.copyFrom(snapshot.getDetail());
        updateRelationships(snapshot);
        save();
    }

    public void updateWith(BaseDealSnapshot dealSnapshot) {
        super.updateWith(dealSnapshot);
        VanillaOptionDealSnapshot snapshot = (VanillaOptionDealSnapshot) dealSnapshot;
        detail.copyFrom(snapshot.getDetail());
        updateRelationships(snapshot);
        update();
    }

    protected void updateRelationships(BaseDealSnapshot baseSnapshot) {
        super.updateRelationships(baseSnapshot);

        VanillaOptionDealSnapshot snapshot = (VanillaOptionDealSnapshot) baseSnapshot;

        if (snapshot.getUnderlyingPriceIndexId() != null)
            this.underlyingPriceIndex = getPriceIndexRepository().load(snapshot.getUnderlyingPriceIndexId());

    }

    @Override
    protected void validate() throws OBValidationException {
        super.validate();
        detail.validate();

        if (underlyingPriceIndex == null && getPowerProfile() == null) {
            throw new OBValidationException(DealErrorCode.MISSING_UNDERLYING_PRICE_IDX.getCode());
        }

    }

    @ManyToOne()
    @JoinColumn(name = "UNDERLYING_INDEX_ID")
    public PriceIndex getUnderlyingPriceIndex() {
        return underlyingPriceIndex;
    }

    public void setUnderlyingPriceIndex(PriceIndex paysPriceIndex) {
        this.underlyingPriceIndex = paysPriceIndex;
    }


    @Override
    protected AuditAbstractEntity createHistory() {
        return VanillaOptionDealAudit.create(this);
    }
}
