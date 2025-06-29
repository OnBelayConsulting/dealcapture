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

import com.onbelay.core.entity.model.AuditAbstractEntity;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.snapshot.CounterpartyDetail;
import com.onbelay.dealcapture.organization.snapshot.CounterpartyRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "COUNTERPARTY_ROLE")
public class CounterpartyRole extends OrganizationRole {

	private CounterpartyDetail detail = new CounterpartyDetail();
	
	
	public CounterpartyRole() {
		super(OrganizationRoleType.COUNTERPARTY_ROLE);
	}

	
	
	@Override
	public void updateWith(OrganizationRoleSnapshot snapshot) {
		super.updateWith(snapshot);
		CounterpartyRoleSnapshot counterpartyRoleSnapshot = (CounterpartyRoleSnapshot) snapshot;
		getDetail().copyFrom(counterpartyRoleSnapshot.getDetail());
		update();
	}

	protected void validate() throws OBValidationException {
		getDetail().validate();
	}

	@Override
	protected void createWith(
			Organization organization,
			OrganizationRoleSnapshot snapshot) {

		super.createWith(
				organization,
				snapshot);

		CounterpartyRoleSnapshot counterpartyRoleSnapshot = (CounterpartyRoleSnapshot) snapshot;
		getDetail().copyFrom(counterpartyRoleSnapshot.getDetail());
		organization.addOrganizationRole(this);
	}



	@Embedded
	public CounterpartyDetail getDetail() {
		return detail;
	}

	public void setDetail(CounterpartyDetail detail) {
		this.detail = detail;
	}

	@Override
	protected AuditAbstractEntity createHistory() {
		CounterpartyRoleAudit audit = CounterpartyRoleAudit.create(this);
		return audit;
	}

	@Override
	@Transient
	public String getEntityName() {
		return "CounterpartyRole";
	}

}
