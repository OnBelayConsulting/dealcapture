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

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPosition;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndexRepositoryBean;
import jakarta.persistence.*;

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;

@Entity
@Table (name = "PHYSICAL_DEAL")
@NamedQueries({
		@NamedQuery(
				name = PriceIndexRepositoryBean.FIND_BY_DEAL_IDS,
				query = "SELECT " +
						"	new com.onbelay.dealcapture.common.snapshot.EntityIdCollection(" +
						"			deal.dealPriceIndex.id,  " +
						"			deal.marketPriceIndex.id)" +
						"  FROM PhysicalDeal deal" +
						" WHERE deal.id in (:dealIds)  ")
})
public class PhysicalDeal extends BaseDeal {

	private PriceIndex dealPriceIndex;
	private PriceIndex marketPriceIndex;
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
	@JoinColumn(name ="DEAL_PRICE_INDEX_ID")
	public PriceIndex getDealPriceIndex() {
		return dealPriceIndex;
	}

	public void setDealPriceIndex(PriceIndex dealPriceIndex) {
		this.dealPriceIndex = dealPriceIndex;
	}

	@ManyToOne
	@JoinColumn(name ="MARKET_PRICE_INDEX_ID")
	public PriceIndex getMarketPriceIndex() {
		return marketPriceIndex;
	}

	private void setMarketPriceIndex(PriceIndex priceIndex) {
		this.marketPriceIndex = priceIndex;
	}
	
	public void updateWith(BaseDealSnapshot snapshot) {
		super.updateWith(snapshot);
		setAssociationsFromSnapshot(snapshot);
		PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) snapshot;
		this.detail.copyFrom(physicalDealSnapshot.getDetail());
		update();
	}
	
	public void createWith(BaseDealSnapshot snapshot) {
		detail.setDefaults();
		super.createWith(snapshot);
		setAssociationsFromSnapshot(snapshot);
		PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) snapshot;
		this.detail.copyFrom(physicalDealSnapshot.getDetail());
		save();
	}
	
	
	
	@Override
	protected void validate() throws OBValidationException {
		super.validate();
		detail.validate();


		if (getDealDetail().getDealStatus() == DealStatusCode.VERIFIED) {
			if (getDetail().getDealPriceValuationCode() == ValuationCode.FIXED) {
				if (dealPriceIndex != null)
					throw new OBValidationException(DealErrorCode.INVALID_DEAL_PRICE_INDEX.getCode());
				if (getDetail().getDealPrice() == null)
					throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUE.getCode());
			} else if (getDetail().getDealPriceValuationCode() == ValuationCode.INDEX) {
				if (dealPriceIndex == null)
					throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_INDEX.getCode());
				if (getDetail().getDealPrice() != null)
					throw new OBValidationException(DealErrorCode.INVALID_DEAL_PRICE_VALUE.getCode());
			} else if (getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
				if (dealPriceIndex == null)
					throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_INDEX.getCode());
				if (getDetail().getDealPrice() == null)
					throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUE.getCode());
			}

			if (marketPriceIndex == null)
				throw new OBValidationException(DealErrorCode.MISSING_MARKET_INDEX.getCode());
		}
	}

	@Override
	protected void setAssociationsFromSnapshot(BaseDealSnapshot baseSnapshot) {
		
		PhysicalDealSnapshot snapshot = (PhysicalDealSnapshot) baseSnapshot;
		
		super.setAssociationsFromSnapshot(snapshot);
		
		if (snapshot.getMarketPriceIndexId() != null) {
			this.marketPriceIndex = getPriceIndexRepository().load(snapshot.getMarketPriceIndexId());
		}

		if (snapshot.getDealPriceIndexId() != null)
			this.dealPriceIndex = getPriceIndexRepository().load(snapshot.getDealPriceIndexId());

	}

	@Override
	protected EntityId savePosition(DealPositionSnapshot snapshot) {
		if (snapshot.getEntityState() == EntityState.NEW) {
			PhysicalPosition position = new PhysicalPosition();
			position.createWith(
					this,
					snapshot);
			return position.generateEntityId();
		} else if (snapshot.getEntityState() == EntityState.MODIFIED) {
			PhysicalPosition position = (PhysicalPosition) getDealPositionsRepository().load(snapshot.getEntityId());
			position.updateWith(snapshot);
			return position.generateEntityId();
		} else if (snapshot.getEntityState() == EntityState.DELETE) {
			PhysicalPosition position = (PhysicalPosition) getDealPositionsRepository().load(snapshot.getEntityId());
			position.delete();
		}
		return null;
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



	private static DealPositionRepository getDealPositionsRepository() {
		return (DealPositionRepository) ApplicationContextFactory.getBean(DealPositionRepository.BEAN_NAME);
	}
}
