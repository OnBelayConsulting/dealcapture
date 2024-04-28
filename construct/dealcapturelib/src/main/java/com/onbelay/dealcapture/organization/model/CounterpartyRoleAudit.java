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
package com.onbelay.dealcapture.organization.model;

import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.snapshot.CounterpartyDetail;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "COUNTERPARTY_ROLE_AUDIT")
public class CounterpartyRoleAudit extends OrganizationRoleAudit {

	private CounterpartyDetail detail = new CounterpartyDetail();
	
	protected CounterpartyRoleAudit() {
	}
	
	protected CounterpartyRoleAudit(CounterpartyRole counterpartyRole) {
		super(
				OrganizationRoleType.COUNTERPARTY_ROLE,
				counterpartyRole);
	}
	
	protected static CounterpartyRoleAudit create(CounterpartyRole counterpartyRole) {
		CounterpartyRoleAudit audit = new CounterpartyRoleAudit(counterpartyRole);
		audit.copyFrom(counterpartyRole);
		return audit;
	}
	
	@Embedded
	public CounterpartyDetail getDetail() {
		return detail;
	}

	public void setDetail(CounterpartyDetail detail) {
		this.detail = detail;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		super.copyFrom(entity);
		CounterpartyRole counterpartyRole = (CounterpartyRole) entity;
		detail.copyFrom(counterpartyRole.getDetail());
	}

	
}
