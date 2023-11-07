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
package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealCostSnapshot extends AbstractSnapshot {
	
	private EntityId dealKey;
	
	public DealCostSnapshot() {
	}
	
	private DealCostDetail detail = new DealCostDetail();

	public EntityId getDealKey() {
		return dealKey;
	}

	public void setDealKey(EntityId dealKey) {
		this.dealKey = dealKey;
	}

	public DealCostDetail getDetail() {
		return detail;
	}

	public void setDetail(DealCostDetail detail) {
		this.detail = detail;
	}
	
	

}
