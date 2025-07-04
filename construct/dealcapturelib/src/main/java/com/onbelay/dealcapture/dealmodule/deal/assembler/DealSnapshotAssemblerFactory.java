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

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DealSnapshotAssemblerFactory {
	
	private static Map<DealTypeCode, Supplier<AbstractDealAssembler>> assemblersMap = new HashMap<DealTypeCode, Supplier<AbstractDealAssembler>>();
	
	static {
		
		assemblersMap.put(DealTypeCode.PHYSICAL_DEAL, PhysicalDealAssembler::new);
		assemblersMap.put(DealTypeCode.FINANCIAL_SWAP, FinancialSwapDealAssembler::new);
		assemblersMap.put(DealTypeCode.VANILLA_OPTION, VanillaOptionDealAssembler::new);

	}

	public static AbstractDealAssembler newAssembler(DealTypeCode dealType) {
		return assemblersMap.get(dealType).get();
	}
	
	public static List<BaseDealSnapshot> assemble(List<BaseDeal> deals) {
		ArrayList<BaseDealSnapshot> snapshots = new ArrayList<BaseDealSnapshot>();
		
		for (BaseDeal deal : deals) {
			AbstractDealAssembler assembler = newAssembler(deal.getDealType());
			snapshots.add(
					assembler.assemble(deal));
		}
		
		return snapshots;
	}
	
	
	
}
