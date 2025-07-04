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
package com.onbelay.dealcapture.organization.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleStatus;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationRoleSummary extends AbstractSnapshot {

	private Integer organizationId;
	private String organizationRoleTypeValue;
	private OrganizationRoleSummaryDetail detail = new OrganizationRoleSummaryDetail();
	
	public OrganizationRoleSummary () { 
		
	}

	public OrganizationRoleSummary (
			Integer id,
			Integer organizationId,
			final String organizationRoleTypeValue,
			String shortName,
			String longName,
			String status) {
		setEntityId(new EntityId(id, shortName, longName, true));
		this.organizationId = organizationId;
		this.organizationRoleTypeValue = organizationRoleTypeValue;
		detail.setLegalName(longName);
		detail.setShortName(shortName);
		detail.setStatus(OrganizationRoleStatus.lookUp(status));

	}

	public OrganizationRoleSummaryDetail getDetail() {
		return detail;
	}

	public void setDetail(OrganizationRoleSummaryDetail detail) {
		this.detail = detail;
	}

	public String getOrganizationRoleTypeValue() {
		return organizationRoleTypeValue;
	}

	public void setOrganizationRoleTypeValue(String organizationRoleTypeValue) {
		this.organizationRoleTypeValue = organizationRoleTypeValue;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	@JsonIgnore
	public OrganizationRoleType getOrganizationRoleType() {
		return OrganizationRoleType.lookUp(organizationRoleTypeValue);
	}


}
