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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealType;
import com.onbelay.dealcapture.dealmodule.deal.shared.DealDetail;

import java.util.List;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "dealTypeValue")
@JsonSubTypes( {
		@Type(value = PhysicalDealSnapshot.class, name = "P"),
		@Type(value = ErrorDealSnapshot.class, name = "E"),
})

public abstract class BaseDealSnapshot extends AbstractSnapshot {
	
	private EntityId companyRoleId;
	private EntityId counterpartyRoleId;
	private DealDetail dealDetail = new DealDetail();
	
	private String dealTypeValue;
	
	public BaseDealSnapshot(
			DealType dealType,
			EntityId companyRoleId,
			EntityId counterpartyRoleId,
			DealDetail dealDetail) {
		
		this.dealTypeValue = dealType.getCode();
		this.companyRoleId = companyRoleId;
		this.counterpartyRoleId = counterpartyRoleId;
		this.dealDetail.copyFrom(dealDetail);
		
	
	}
	
	public BaseDealSnapshot(DealType dealType) {
		this.dealTypeValue = dealType.getCode();
	}

	public BaseDealSnapshot(DealType dealType, String errorCode) {
		super(errorCode);
		this.dealTypeValue = dealType.getCode();
	}

	public BaseDealSnapshot(
			DealType dealType,
			String errorCode,
			boolean isPermissionException) {
		super(errorCode, isPermissionException);
		this.dealTypeValue = dealType.getCode();
	}

	public BaseDealSnapshot(
			DealType dealType,
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
	public DealType getDealType() {
		return DealType.lookUp(dealTypeValue);
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

	public String toString() {
		return " TicketNo: " +dealDetail.getTicketNo();
	}
}
