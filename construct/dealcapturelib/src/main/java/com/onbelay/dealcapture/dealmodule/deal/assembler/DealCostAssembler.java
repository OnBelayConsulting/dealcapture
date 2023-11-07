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
package com.onbelay.dealcapture.dealmodule.deal.assembler;

import java.util.ArrayList;
import java.util.List;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.DealCost;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSnapshot;

public class DealCostAssembler extends EntityAssembler {
	
	private BaseDeal deal;

	public DealCostAssembler(BaseDeal deal) {
		super();
		this.deal = deal;
	}
	
	
	public List<DealCostSnapshot> assemble(List<DealCost> dealCosts) {
		
		ArrayList<DealCostSnapshot> snapshots = new ArrayList<>();
		for (DealCost c : dealCosts) {
			snapshots.add(
					assemble(c));
		}
		
		return snapshots;
		
	}
	
	public DealCostSnapshot assemble(DealCost dealCost) {
		DealCostSnapshot snapshot = new DealCostSnapshot();
		super.setEntityAttributes(dealCost, snapshot);
		snapshot.setDealKey(new EntityId(deal.getId()));
		snapshot.getDetail().copyFrom(dealCost.getDetail());
		return snapshot;
	}

}
