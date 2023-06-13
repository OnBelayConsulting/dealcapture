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
package com.onbelay.dealcapture.pricing.model;

import com.onbelay.dealcapture.pricing.enums.IndexType;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshot;

public class PricingIndexFixture {

	
	public static PricingIndex createPricingIndex(
			String name, 
			PricingLocation pricingLocation) {
		
		 PricingIndex pricingIndex = new PricingIndex();

		 PricingIndexSnapshot snapshot = new PricingIndexSnapshot();
		 snapshot.getDetail().setDefaults();
		 snapshot.getDetail().setDaysOffsetForExpiry(4);
		 snapshot.getDetail().setName(name);
		 snapshot.getDetail().setDescription(name + "-Desc");
		 snapshot.getDetail().setIndexType(IndexType.HUB);
		 
		 snapshot.setPricingLocationId(pricingLocation.generateEntityId());
		 
		 pricingIndex.createWith(snapshot);
		
		 return pricingIndex;
	}
	
	
	public static PricingIndexSnapshot createPricingIndexSnapshot(
			String name, 
			PricingLocation pricingLocation) {
		

		 PricingIndexSnapshot snapshot = new PricingIndexSnapshot();
		 snapshot.getDetail().setDefaults();
		 snapshot.getDetail().setDaysOffsetForExpiry(4);
		 snapshot.getDetail().setName(name);
		 snapshot.getDetail().setDescription(name + "-Desc");
		 snapshot.getDetail().setIndexType(IndexType.HUB);
		 
		 snapshot.setPricingLocationId(pricingLocation.generateEntityId());
		 return snapshot;
	}
	
}
