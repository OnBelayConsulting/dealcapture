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
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonth;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DealDayByMonthAssembler extends EntityAssembler {
	
	private BaseDeal deal;

	public DealDayByMonthAssembler(BaseDeal deal) {
		super();
		this.deal = deal;
	}
	
	
	public List<DealDayByMonthSnapshot> assemble(List<DealDayByMonth> dealDayByMonths) {
		
		ArrayList<DealDayByMonthSnapshot> snapshots = new ArrayList<>();
		for (DealDayByMonth c : dealDayByMonths) {
			snapshots.add(
					assemble(c));
		}
		
		return snapshots;
		
	}
	
	public DealDayByMonthSnapshot assemble(DealDayByMonth dealDayByMonth) {
		DealDayByMonthSnapshot snapshot = new DealDayByMonthSnapshot();
		super.setEntityAttributes(dealDayByMonth, snapshot);
		snapshot.setDealId(new EntityId(deal.getId()));
		snapshot.getDetail().copyFrom(dealDayByMonth.getDetail());
		return snapshot;
	}

}
