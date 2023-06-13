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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.onbelay.core.entity.model.TemporalAbstractEntity;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.snapshot.CompanyDetail;

@Entity
@Table(name = "COMPANY_ROLE_AUDIT")
public class CompanyRoleAudit extends OrganizationRoleAudit {

	private CompanyDetail detail = new CompanyDetail();
	
	protected CompanyRoleAudit() {
	}

	protected CompanyRoleAudit(CompanyRole companyRole) {
		super(
				OrganizationRoleType.COMPANY_ROLE,
				companyRole);
	}
	

	
	protected static CompanyRoleAudit create(CompanyRole companyRole) {
		CompanyRoleAudit audit = new CompanyRoleAudit(companyRole);
		audit.copyFrom(companyRole);
		return audit;
	}

	@Embedded
	public CompanyDetail getDetail() {
		return detail;
	}

	public void setDetail(CompanyDetail detail) {
		this.detail = detail;
	}

	@Override
	public void copyFrom(TemporalAbstractEntity entity) {
		super.copyFrom(entity);
		CompanyRole companyRole = (CompanyRole) entity;
		detail.copyFrom(companyRole.getDetail());
	}
	
	
	
}
