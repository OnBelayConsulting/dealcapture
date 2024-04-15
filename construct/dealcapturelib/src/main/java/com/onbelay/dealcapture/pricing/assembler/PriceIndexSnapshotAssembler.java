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
package com.onbelay.dealcapture.pricing.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class PriceIndexSnapshotAssembler extends EntityAssembler {

	public PriceIndexSnapshot assemble(PriceIndex index) {
		
		PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
		super.setEntityAttributes(index, snapshot);
		snapshot.getDetail().copyFrom(index.getDetail());
		if (index.getBenchmarkPricingIndex() != null)
			snapshot.setBenchmarkIndexId(index.getBenchmarkPricingIndex().generateEntityId());

		if (index.getBasePricingIndex() != null)
			snapshot.setBaseIndexId(index.getBasePricingIndex().generateEntityId());

		return snapshot;
	}
	
	public List<PriceIndexSnapshot> assemble(List<PriceIndex> locations) {
		return locations
			.stream()
			.map( c -> assemble(c))
			.collect(Collectors.toList());
	}
	
}
