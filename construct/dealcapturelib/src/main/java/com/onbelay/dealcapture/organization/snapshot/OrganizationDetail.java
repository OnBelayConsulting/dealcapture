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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.organization.enums.OrganizationErrorCode;

import jakarta.persistence.Column;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationDetail {

	private String shortName;
	private String legalName;

	private Integer externalReferenceId;


	public void validate() throws OBValidationException {
		
		if (shortName == null)
			throw new OBValidationException(OrganizationErrorCode.MISSING_ORGANIZATION_ID.getCode());
		
		if (legalName == null)
			throw new OBValidationException(OrganizationErrorCode.MISSING_ORGANIZATION_NAME.getCode());
		
	}

	public void copyFrom(OrganizationDetail copy) {
		if (copy.shortName != null)
			this.shortName = copy.shortName;

		if (copy.legalName != null)
			this.legalName = copy.legalName;

		if (copy.externalReferenceId != null)
			this.externalReferenceId = copy.externalReferenceId;
	}

	@Column(name = "SHORT_NAME")
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Column(name = "LEGAL_NAME")
	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	@Column(name = "EXTERNAL_REF_ID")
	public Integer getExternalReferenceId() {
		return externalReferenceId;
	}

	public void setExternalReferenceId(Integer externalReferenceId) {
		this.externalReferenceId = externalReferenceId;
	}
}
