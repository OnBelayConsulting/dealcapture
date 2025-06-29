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
package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CreateDealFactory {


	private static Map<DealTypeCode, Supplier<BaseDeal>> factoryMap = new HashMap<>();

	static {

		factoryMap.put(DealTypeCode.PHYSICAL_DEAL, PhysicalDeal::new);
		factoryMap.put(DealTypeCode.VANILLA_OPTION, VanillaOptionDeal::new);
		factoryMap.put(DealTypeCode.FINANCIAL_SWAP, FinancialSwapDeal::new);

	}


	public static BaseDeal createDealFromSnapshot(BaseDealSnapshot snapshot) {
		BaseDeal deal =  factoryMap.get(snapshot.getDealType()).get();
		deal.createWith(snapshot);
		return deal;
	}

}
