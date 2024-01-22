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
package com.onbelay.dealcapture.pricing.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceIndexSnapshot extends AbstractSnapshot {

	private PriceIndexDetail detail = new PriceIndexDetail();
	
	private EntityId benchmarkIndexId;
	
	private EntityId baseIndexId;
	
	private EntityId pricingLocationId;

	public PriceIndexSnapshot() {
	}

	public PriceIndexSnapshot(EntityId entityId) {
		super(entityId);
	}

	public PriceIndexSnapshot(String errorCode) {
		super(errorCode);
	}

	public PriceIndexSnapshot(String errorCode, boolean isPermissionException) {
		super(errorCode, isPermissionException);
	}

	public PriceIndexSnapshot(String errorCode, List<String> parameters) {
		super(errorCode, parameters);
	}

	public PriceIndexDetail getDetail() {
		return detail;
	}

	public void setDetail(PriceIndexDetail detail) {
		this.detail = detail;
	}

	public EntityId getBenchmarkIndexId() {
		return benchmarkIndexId;
	}

	public void setBenchmarkIndexId(EntityId benchmarkIndexId) {
		this.benchmarkIndexId = benchmarkIndexId;
	}

	public EntityId getBaseIndexId() {
		return baseIndexId;
	}

	public void setBaseIndexId(EntityId hubIndexSlot) {
		this.baseIndexId = hubIndexSlot;
	}

	public EntityId getPricingLocationId() {
		return pricingLocationId;
	}

	public void setPricingLocationId(EntityId pricingLocationId) {
		this.pricingLocationId = pricingLocationId;
	}
	
	
	

}
