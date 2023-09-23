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
package com.onbelay.dealcapture.pricing.shared;

import jakarta.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PricingLocationDetail extends AbstractDetail {

	private String countryCode;
	private String stateProvinceCode;
	private String name;
	private String description; 
	
	
	public PricingLocationDetail() {
	}

	public void setDefaults() {
		
	}
			

	public void validate() throws OBValidationException {
		if (name == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICING_LOCATION_NAME.getCode());
	}

	@Column(name = "COUNTRY_CODE")
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Column(name = "STATE_PROV_CODE")
	public String getStateProvinceCode() {
		return stateProvinceCode;
	}


	public void setStateProvinceCode(String stateProvinceCode) {
		this.stateProvinceCode = stateProvinceCode;
	}

	@Column(name = "LOCATION_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "LOCATION_DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}





	public void copyFrom(PricingLocationDetail copy) {
		if (copy.countryCode != null)
			this.countryCode = copy.countryCode;
		
		if (copy.stateProvinceCode != null)
			this.stateProvinceCode = copy.stateProvinceCode;
		
		if (copy.name != null)
			this.name = copy.name;
		
		if (copy.description != null)
			this.description = copy.description;
	}
	
}
