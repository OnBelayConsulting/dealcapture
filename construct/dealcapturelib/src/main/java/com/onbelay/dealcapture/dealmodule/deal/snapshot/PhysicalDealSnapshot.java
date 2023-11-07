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
package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.shared.enums.CurrencyCode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicalDealSnapshot extends BaseDealSnapshot {

	private PhysicalDealDetail detail = new PhysicalDealDetail();
	
	private EntityId marketPricingIndexId;
	private CurrencyCode marketCurrencyCode;
	
	public PhysicalDealSnapshot() {
		super(DealTypeCode.PHYSICAL_DEAL);
	}

	public PhysicalDealSnapshot(String errorCode) {
		super(
				DealTypeCode.PHYSICAL_DEAL,
				errorCode);
	}

	public PhysicalDealSnapshot(
			String errorCode,
			boolean isPermissionException) {

		super(
				DealTypeCode.PHYSICAL_DEAL,
				errorCode,
				isPermissionException);
	}

	public PhysicalDealSnapshot(
			String errorCode,
			List<String> parameters) {
		super(
				DealTypeCode.PHYSICAL_DEAL,
				errorCode,
				parameters);
	}

	public PhysicalDealSnapshot(
			EntityId companyRoleSlot,
			EntityId counterpartySlot,
			DealDetail dealDetail,
			PhysicalDealDetail detail) {
		
		super(
				DealTypeCode.PHYSICAL_DEAL,
				companyRoleSlot,
				counterpartySlot,
				dealDetail);
		
		this.detail.copyFrom(detail);
	}
			
	
	public PhysicalDealDetail getDetail() {
		return detail;
	}

	public void setDetail(PhysicalDealDetail detail) {
		this.detail = detail;
	}

	public EntityId getMarketPricingIndexId() {
		return marketPricingIndexId;
	}

	public void setMarketPricingIndexId(EntityId marketPricingIndexId) {
		this.marketPricingIndexId = marketPricingIndexId;
	}

	public CurrencyCode getMarketCurrencyCode() {
		return marketCurrencyCode;
	}

	public void setMarketCurrencyCode(CurrencyCode marketCurrencyCode) {
		this.marketCurrencyCode = marketCurrencyCode;
	}
}
