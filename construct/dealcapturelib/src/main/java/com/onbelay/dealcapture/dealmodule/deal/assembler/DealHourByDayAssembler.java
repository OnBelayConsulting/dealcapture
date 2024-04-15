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

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDay;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DealHourByDayAssembler extends EntityAssembler {
	
	private BaseDeal deal;

	public DealHourByDayAssembler(BaseDeal deal) {
		super();
		this.deal = deal;
	}
	
	
	public List<DealHourByDaySnapshot> assemble(List<DealHourByDay> dealHourByDays) {
		
		ArrayList<DealHourByDaySnapshot> snapshots = new ArrayList<>();
		for (DealHourByDay c : dealHourByDays) {
			snapshots.add(
					assemble(c));
		}
		
		return snapshots;
		
	}
	
	public DealHourByDaySnapshot assemble(DealHourByDay dealHourByDay) {
		DealHourByDaySnapshot snapshot = new DealHourByDaySnapshot();
		super.setEntityAttributes(dealHourByDay, snapshot);
		snapshot.setDealId(new EntityId(deal.getId()));
		snapshot.getDetail().copyFrom(dealHourByDay.getDetail());
		return snapshot;
	}

}
