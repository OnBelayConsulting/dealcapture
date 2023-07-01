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

import javax.persistence.Column;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PricingIndexDetail {

	private String name;
	private String description;
	private Integer daysOffsetForExpiry;
	private String indexTypeValue;
	
	public PricingIndexDetail() {
		
	}
	
	@JsonIgnore
	public void setDefaults() {
		daysOffsetForExpiry = 0;
		indexTypeValue = IndexType.HUB.getCode();
	}
	
	public void validate() throws OBValidationException {
		
		if (name == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICING_INDEX_NAME.getCode());
		
		if (indexTypeValue == null)
			throw new OBValidationException(PricingErrorCode.MISSING_PRICING_INDEX_TYPE.getCode());
		
		if (daysOffsetForExpiry == null)
			throw new OBValidationException(PricingErrorCode.MISSING_INDEX_DAYS_EXPIRY.getCode());
		
	}
	
	public PricingIndexDetail(String name, String description) {
		super();
		this.name = name;
		this.description = description;
		indexTypeValue = IndexType.HUB.getCode();
	}
	
	
	@Transient
	@JsonIgnore
	public IndexType getIndexType() {
		return IndexType.lookUp(indexTypeValue);
	}
	
	public void setIndexType(IndexType type) {
		if (type != null)
			this.indexTypeValue = type.getCode();
		else
			this.indexTypeValue = null;
	}

	@Column(name = "INDEX_TYPE_CODE")
	public String getIndexTypeValue() {
		return indexTypeValue;
	}

	public void setIndexTypeValue(String indexTypeValue) {
		this.indexTypeValue = indexTypeValue;
	}

	@Column(name = "INDEX_NAME")
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Column(name = "INDEX_DESCRIPTION")
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	@Column(name = "OFFSET_EXPIRY_DAYS")
	public Integer getDaysOffsetForExpiry() {
		return daysOffsetForExpiry;
	}


	public void setDaysOffsetForExpiry(Integer daysOffsetForExpiry) {
		this.daysOffsetForExpiry = daysOffsetForExpiry;
	}


	public void copyFrom(PricingIndexDetail copy) {
		
		if (copy.name != null)
			this.name = copy.name;
		
		if (copy.description != null)
			this.description = copy.description;
		
		if (copy.daysOffsetForExpiry != null)
			this.daysOffsetForExpiry = copy.daysOffsetForExpiry;
		
		if (copy.indexTypeValue != null)
			this.indexTypeValue = copy.getIndexTypeValue();
	}
	
}
