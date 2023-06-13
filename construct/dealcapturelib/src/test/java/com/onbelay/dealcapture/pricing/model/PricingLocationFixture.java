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

import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshot;

public class PricingLocationFixture {

	
	public static PricingLocation createPricingLocation(String name) {
		
		PricingLocation location = new PricingLocation();
		
		PricingLocationSnapshot snapshot = new PricingLocationSnapshot();
		
		snapshot.getDetail().setDefaults();

		snapshot.getDetail().setName(name);
		
		location.createWith(snapshot);
		return location;
	}
	
	public static PricingLocationSnapshot createPricingLocationSnapshot(String name) {
		
		PricingLocationSnapshot snapshot = new PricingLocationSnapshot();
		
		snapshot.getDetail().setDefaults();

		snapshot.getDetail().setName(name);
		return snapshot;
	}
	
}
