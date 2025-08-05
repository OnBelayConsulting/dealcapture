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
import com.onbelay.dealcapture.pricing.model.FxCurve;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class FxCurveSnapshotAssembler extends EntityAssembler {

	public FxCurveSnapshot assemble(FxCurve rate) {
		
		FxCurveSnapshot snapshot = new FxCurveSnapshot();
		snapshot.setIndexId(rate.getFxIndex().generateEntityId());
		super.setEntityAttributes(rate, snapshot);
		snapshot.getDetail().copyFrom(rate.getDetail());
		return snapshot;
	}
	
	public List<FxCurveSnapshot> assemble(List<FxCurve> rates) {
		return rates
			.stream()
			.map( c -> assemble(c))
			.collect(Collectors.toList());
	}
	
}
