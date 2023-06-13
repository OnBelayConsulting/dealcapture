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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "organizationRoleTypeValue")
@JsonSubTypes( {
		@Type(value = CompanyRoleSnapshot.class, name = "CO"),
		@Type(value = CounterpartyRoleSnapshot.class, name = "CP")
})
public class OrganizationRoleSnapshot extends AbstractSnapshot {
	
	private String organizationRoleTypeValue;

	private EntityId organizationId;

	public OrganizationRoleSnapshot() { }
	
	protected OrganizationRoleSnapshot(OrganizationRoleType roleType) {
		this.organizationRoleTypeValue = roleType.getCode();
			
	}

	public EntityId getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(EntityId organizationId) {
		this.organizationId = organizationId;
	}

	private OrganizationRoleDetail roleDetail = new OrganizationRoleDetail();

	public OrganizationRoleDetail getRoleDetail() {
		return roleDetail;
	}

	public void setRoleDetail(OrganizationRoleDetail roleDetail) {
		this.roleDetail = roleDetail;
	}

	@JsonIgnore
	public OrganizationRoleType getOrganizationRoleType() {
		return OrganizationRoleType.lookUp(organizationRoleTypeValue);
	}
	
	public String getOrganizationRoleTypeValue() {
		return organizationRoleTypeValue;
	}

	public void setOrganizationRoleTypeValue(String organizationRoleTypeValue) {
		this.organizationRoleTypeValue = organizationRoleTypeValue;
	}
	
	
	
}
