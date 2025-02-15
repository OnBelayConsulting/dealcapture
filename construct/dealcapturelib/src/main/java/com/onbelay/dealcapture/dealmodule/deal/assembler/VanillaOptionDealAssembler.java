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

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.VanillaOptionDealSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaOptionDealAssembler extends AbstractDealAssembler {
	private static Logger logger = LogManager.getLogger(VanillaOptionDealAssembler.class);
	
	@Override
	public BaseDealSnapshot assemble(BaseDeal deal) {
		
		VanillaOptionDealSnapshot snapshot = new VanillaOptionDealSnapshot();
		
		setCommonAttributes(deal, snapshot);
		
		if (deal instanceof VanillaOptionDeal == false) {
			logger.fatal("Invalid class passed into assembler - expecting VanillaOptionDeal. dealNo=", deal.getDealDetail().getTicketNo());
			throw new OBRuntimeException(DealErrorCode.INVALID_DEAL_ID.getCode());
		}
		
		VanillaOptionDeal optionDeal = (VanillaOptionDeal) deal;
		snapshot.getDetail().copyFrom(optionDeal.getDetail());

		if (optionDeal.getUnderlyingPriceIndex() != null) {
			snapshot.setUnderlyingPriceIndexId(optionDeal.getUnderlyingPriceIndex().generateEntityId());
		}
		return snapshot;
	}


}
