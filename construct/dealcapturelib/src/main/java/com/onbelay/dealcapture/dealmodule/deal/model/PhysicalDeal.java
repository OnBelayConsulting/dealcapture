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
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexRepositoryBean;
import jakarta.persistence.*;

/**
 * A physical deal buys and sells the commodity with either a fixed price, float price or a float plus fixed price.
 *
 */
@Entity
@Table (name = "PHYSICAL_DEAL")
@NamedQueries({
    @NamedQuery(
       name = DealRepositoryBean.FETCH_PHYSICAL_DEAL_SUMMARIES,
       query = "SELECT new com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary( "
       		+ "          deal.id, "
	        + "          powerProfile.id, "
       		+ "          deal.dealDetail.ticketNo, "
       		+ "          deal.dealDetail.startDate,"
	  	    + "          deal.dealDetail.endDate,"
       		+ "          deal.dealTypeValue, "
       		+ "          deal.dealDetail.buySellCodeValue,"
		    +  "         deal.dealDetail.reportingCurrencyCodeValue,"
			+  "		 deal.dealDetail.volumeQuantity,"
			+  "         deal.dealDetail.volumeUnitOfMeasureCodeValue,"
		    +  "         deal.dealDetail.volumeFrequencyCodeValue,"
			+  "	  	 deal.dealDetail.settlementCurrencyCodeValue,"
			+  "         deal.detail.dealPriceValuationCodeValue,"
			+  "         dealIndex.id,"
			+  "         deal.detail.fixedPriceValue,"
			+  "         deal.detail.fixedPriceUnitOfMeasureCodeValue,"
			+  "         deal.detail.fixedPriceCurrencyCodeValue,"
			+  "         deal.detail.marketValuationCodeValue,"
			+  "         marketIndex.id"
			+  "         ) "
       		+ "   FROM PhysicalDeal deal " +
  "    LEFT OUTER JOIN deal.powerProfile as powerProfile " +
   "   LEFT OUTER JOIN deal.dealPriceIndex as dealIndex " +
   "   LEFT OUTER JOIN deal.marketPriceIndex as marketIndex " +
			   " WHERE deal.id in (:dealIds) " +
       	     "ORDER BY deal.dealDetail.ticketNo DESC")
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
		updateRelationships(snapshot);
		PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) snapshot;
		this.detail.copyFrom(physicalDealSnapshot.getDetail());
		update();
	}
	
	public void createWith(BaseDealSnapshot snapshot) {
		detail.setDefaults();
		super.createWith(snapshot);
		updateRelationships(snapshot);
		PhysicalDealSnapshot physicalDealSnapshot = (PhysicalDealSnapshot) snapshot;
		this.detail.copyFrom(physicalDealSnapshot.getDetail());
		save();
	}
	
	
	
	@Override
	protected void validate() throws OBValidationException {
		super.validate();
		detail.validate();

		if (getDealDetail().getSettlementCurrencyCode() == null)
			throw new OBValidationException(DealErrorCode.MISSING_SETTLEMENT_CURRENCY.getCode());

		if (getDealDetail().getDealStatus() == DealStatusCode.VERIFIED) {

			switch (getDetail().getDealPriceValuationCode()) {
				case FIXED -> {
					if (dealPriceIndex != null)
						throw new OBValidationException(DealErrorCode.INVALID_DEAL_PRICE_INDEX.getCode());
					if (getDetail().getFixedPrice() == null)
						throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUE.getCode());
				}
				case INDEX -> {
					if (dealPriceIndex == null)
						throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_INDEX.getCode());
					if (getDetail().getFixedPrice() != null)
						throw new OBValidationException(DealErrorCode.INVALID_FIXED_PRICE_VALUE.getCode());
				}
				case INDEX_PLUS -> {
					if (dealPriceIndex == null)
						throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_INDEX.getCode());
					if (getDetail().getFixedPrice() == null)
						throw new OBValidationException(DealErrorCode.MISSING_DEAL_PRICE_VALUE.getCode());
				}

				case POWER_PROFILE -> {
					throw new OBValidationException(DealErrorCode.INVALID_DEAL_PRICE_VALUATION.getCode());
				}
			}

			switch (getDetail().getMarketValuationCode()) {
				case INDEX -> {
					if (marketPriceIndex == null)
						throw new OBValidationException(DealErrorCode.MISSING_MARKET_INDEX.getCode());
				}
				case POWER_PROFILE -> {
					if (getPowerProfile() == null)
						throw new OBValidationException(DealErrorCode.MISSING_MARKET_POWER_PROFILE.getCode());
				}
				default -> {throw new OBValidationException(DealErrorCode.INVALID_MARKET_PRICE_VALUATION.getCode());}
			}
		}
	}

	protected void updateRelationships(BaseDealSnapshot baseSnapshot) {
		super.updateRelationships(baseSnapshot);

		PhysicalDealSnapshot snapshot = (PhysicalDealSnapshot) baseSnapshot;

		if (snapshot.getMarketPriceIndexId() != null)
			this.marketPriceIndex = getPriceIndexRepository().load(snapshot.getMarketPriceIndexId());

		if (snapshot.getDealPriceIndexId() != null)
			this.dealPriceIndex = getPriceIndexRepository().load(snapshot.getDealPriceIndexId());

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
