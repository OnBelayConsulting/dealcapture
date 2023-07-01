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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.shared.PhysicalDealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.pricing.model.PricingIndex;

@Entity
@Table (name = "PHYSICAL_DEAL")
public class PhysicalDeal extends BaseDeal {

	private PricingIndex marketPricingIndex;
	private PhysicalDealDetail detail = new PhysicalDealDetail();
	
	public PhysicalDeal() {
		super(DealTypeCode.PHYSICAL_DEAL);
	}
	
	public static PhysicalDeal create(PhysicalDealSnapshot snapshot) {
		PhysicalDeal deal = new PhysicalDeal();
		deal.createWith(snapshot);
		return deal;
	}

	@Override
	@Transient
	public String getEntityName() {
		return "PhysicalDeal";
	}

	@ManyToOne
	@JoinColumn(name ="MARKET_PRICING_INDEX_ID")
	public PricingIndex getMarketPricingIndex() {
		return marketPricingIndex;
	}

	private void setMarketPricingIndex(PricingIndex pricingIndex) {
		this.marketPricingIndex = pricingIndex;
	}
	
	public void updateWith(BaseDealSnapshot snapshot) {
		super.updateWith(snapshot);
		setAssociationsFromSnapshot(snapshot);
		PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) snapshot;
		this.detail.copyFrom(physicalDealSnapshot.getPhysicalDealDetail());
		update();
	}
	
	public void createWith(BaseDealSnapshot snapshot) {
		detail.setDefaults();
		super.createWith(snapshot);
		setAssociationsFromSnapshot(snapshot);
		PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) snapshot;
		this.detail.copyFrom(physicalDealSnapshot.getPhysicalDealDetail());
		save();
	}
	
	
	
	@Override
	protected void validate() throws OBValidationException {
		super.validate();
		detail.validate();
		if (getDealDetail().getDealStatus() == DealStatusCode.VERIFIED) {
			if (marketPricingIndex == null)
				throw new OBValidationException(DealErrorCode.MISSING_MARKET_INDEX.getCode());
		}
	}

	@Override
	protected void setAssociationsFromSnapshot(BaseDealSnapshot baseSnapshot) {
		
		PhysicalDealSnapshot snapshot = (PhysicalDealSnapshot) baseSnapshot;
		
		super.setAssociationsFromSnapshot(snapshot);
		
		if (snapshot.getMarketPricingIndexId() != null) {
			this.marketPricingIndex = getPricingIndexRepository().load(snapshot.getMarketPricingIndexId());
		}
	}
	


	@Embedded
	public PhysicalDealDetail getDetail() {
		return detail;
	}


	private void setDetail(PhysicalDealDetail detail) {
		this.detail = detail;
	}


	@Override
	protected AuditAbstractEntity createHistory() {
		return PhysicalDealAudit.create(this);
	}

	
	
}
