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

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.organization.enums.OrganizationErrorCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyDetail extends AbstractDetail {
	
	private Boolean isHoldingParent;

	@Column(name = "CO_IS_HOLDING_PARENT")
    @org.hibernate.annotations.Type(type="yes_no")
	public Boolean getIsHoldingParent() {
		return isHoldingParent;
	}

	public void setIsHoldingParent(Boolean isRoot) {
		this.isHoldingParent = isRoot;
	}
	
	public void validate() throws OBValidationException {
		if (isHoldingParent == null)
			throw new OBValidationException(OrganizationErrorCode.MISSING_CO_IS_HOLDING_PARENT.getCode());
	}
	
	public void copyFrom(CompanyDetail copy) {
		if (copy.isHoldingParent != null)
			this.isHoldingParent = copy.isHoldingParent;
	}

}
