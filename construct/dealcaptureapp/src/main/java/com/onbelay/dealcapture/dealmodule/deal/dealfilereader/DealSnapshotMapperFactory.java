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
package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DealSnapshotMapperFactory {
	
	private static Map<DealTypeCode, Supplier<BaseDealSnapshotMapper>> mappersMap = new HashMap<DealTypeCode, Supplier<BaseDealSnapshotMapper>>();
	
	static {
		
		mappersMap.put(DealTypeCode.PHYSICAL_DEAL, PhysicalDealSnapshotMapper::new);
		mappersMap.put(DealTypeCode.FINANCIAL_SWAP, FinancialSwapDealSnapshotMapper::new);
		mappersMap.put(DealTypeCode.VANILLA_OPTION, VanillaOptionDealSnapshotMapper::new);

	}

	public static BaseDealSnapshotMapper newMapper(DealTypeCode dealType) {
		return mappersMap.get(dealType).get();
	}

}
