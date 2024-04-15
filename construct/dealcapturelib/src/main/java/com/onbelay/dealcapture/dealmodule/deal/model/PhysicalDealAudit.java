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

import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealDetail;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import jakarta.persistence.*;

@Entity
@Table (name = "PHYSICAL_DEAL_AUDIT")
public class PhysicalDealAudit extends BaseDealAudit {

	private PriceIndex marketPriceIndex;
	private PhysicalDealDetail detail = new PhysicalDealDetail();
	
	protected PhysicalDealAudit() {
		
	}
	
	protected PhysicalDealAudit(BaseDeal deal) {
		super(deal);
	}
	
	protected static PhysicalDealAudit create(PhysicalDeal deal) {
		PhysicalDealAudit audit = new PhysicalDealAudit(deal);
		audit.copyFrom(deal);
		
		return audit;
	}

	@ManyToOne
	@JoinColumn(name ="MARKET_PRICE_INDEX_ID")
	public PriceIndex getMarketPriceIndex() {
		return marketPriceIndex;
	}

	private void setMarketPriceIndex(PriceIndex priceIndex) {
		this.marketPriceIndex = priceIndex;
	}

	@Embedded
	public PhysicalDealDetail getDetail() {
		return detail;
	}


	private void setDetail(PhysicalDealDetail detail) {
		this.detail = detail;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		super.copyFrom(entity);
		PhysicalDeal physicalDeal = (PhysicalDeal) entity;
		marketPriceIndex = physicalDeal.getMarketPriceIndex();
		detail.copyFrom(physicalDeal.getDetail());
	}


	
	
	
}
