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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

import java.util.List;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "dealTypeValue")
@JsonSubTypes( {
		@Type(value = PhysicalDealSnapshot.class, name = "PhysicalDeal"),
		@Type(value = FinancialSwapDealSnapshot.class, name = "FinancialSwap"),
		@Type(value = VanillaOptionDealSnapshot.class, name = "VanillaOption"),
		@Type(value = ErrorDealSnapshot.class, name = "E"),
})

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseDealSnapshot extends AbstractSnapshot {
	
	private EntityId companyRoleId;
	private EntityId counterpartyRoleId;

	private EntityId powerProfileId;

	private EntityId companyTraderId;
	private EntityId counterpartyTraderId;
	private EntityId administratorId;


	private DealDetail dealDetail = new DealDetail();
	
	private String dealTypeValue;
	
	public BaseDealSnapshot(
			DealTypeCode dealType,
			EntityId companyRoleId,
			EntityId counterpartyRoleId,
			DealDetail dealDetail) {
		
		this.dealTypeValue = dealType.getCode();
		this.companyRoleId = companyRoleId;
		this.counterpartyRoleId = counterpartyRoleId;
		this.dealDetail.copyFrom(dealDetail);
		
	
	}
	
	public BaseDealSnapshot(DealTypeCode dealType) {
		this.dealTypeValue = dealType.getCode();
	}

	public BaseDealSnapshot(DealTypeCode dealType, String errorCode) {
		super(errorCode);
		this.dealTypeValue = dealType.getCode();
	}

	public BaseDealSnapshot(
			DealTypeCode dealType,
			String errorCode,
			boolean isPermissionException) {
		super(errorCode, isPermissionException);
		this.dealTypeValue = dealType.getCode();
	}

	public BaseDealSnapshot(
			DealTypeCode dealType,
			String errorCode,
			List<String> parameters) {
		super(errorCode, parameters);
		this.dealTypeValue = dealType.getCode();
	}

	public String getDealTypeValue() {
		return dealTypeValue;
	}

	public void setDealTypeValue(String dealTypeValue) {
		this.dealTypeValue = dealTypeValue;
	}

	@JsonIgnore
	public DealTypeCode getDealType() {
		return DealTypeCode.lookUp(dealTypeValue);
	}
	
	public DealDetail getDealDetail() {
		return dealDetail;
	}

	public void setDealDetail(DealDetail dealDetail) {
		this.dealDetail = dealDetail;
	}

	public EntityId getCompanyRoleId() {
		return companyRoleId;
	}

	public void setCompanyRoleId(EntityId companyRoleId) {
		this.companyRoleId = companyRoleId;
	}

	public EntityId getCounterpartyRoleId() {
		return counterpartyRoleId;
	}

	public void setCounterpartyRoleId(EntityId counterpartyRoleId) {
		this.counterpartyRoleId = counterpartyRoleId;
	}

	public EntityId getPowerProfileId() {
		return powerProfileId;
	}

	public void setPowerProfileId(EntityId powerProfileId) {
		this.powerProfileId = powerProfileId;
	}

	public EntityId getCompanyTraderId() {
		return companyTraderId;
	}

	public void setCompanyTraderId(EntityId companyTraderId) {
		this.companyTraderId = companyTraderId;
	}

	public EntityId getCounterpartyTraderId() {
		return counterpartyTraderId;
	}

	public void setCounterpartyTraderId(EntityId counterpartyTraderId) {
		this.counterpartyTraderId = counterpartyTraderId;
	}

	public EntityId getAdministratorId() {
		return administratorId;
	}

	public void setAdministratorId(EntityId administratorId) {
		this.administratorId = administratorId;
	}

	public String toString() {
		return " TicketNo: " +dealDetail.getTicketNo();
	}
}
