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
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationRoleDetail {
	
	private String organizationRoleStatusValue;


	public void validate() throws OBValidationException {
		

	}


	@Column(name = "ORG_ROLE_STATUS_CODE")
	public String getOrganizationRoleStatusValue() {
		return organizationRoleStatusValue;
	}

	public void setOrganizationRoleStatusValue(String organizationRoleStatusValue) {
		this.organizationRoleStatusValue = organizationRoleStatusValue;
	}
	
	@Transient
	@JsonIgnore
	public OrganizationRoleStatus getStatus() {
		return OrganizationRoleStatus.lookUp(organizationRoleStatusValue);
	}
	
	public void setStatus(OrganizationRoleStatus status) {
		this.organizationRoleStatusValue = status.getCode();
	}
	
	public void copyFrom(OrganizationRoleDetail copy) {
		
		if (copy.organizationRoleStatusValue != null)
			organizationRoleStatusValue = copy.organizationRoleStatusValue;
	}

	@JsonIgnore
	public void setDefaults() {
		organizationRoleStatusValue = OrganizationRoleStatus.PENDING.getCode();
		
	}
}
