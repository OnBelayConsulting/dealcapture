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
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhysicalDealAssembler extends AbstractDealAssembler {
	private static Logger logger = LogManager.getLogger(PhysicalDealAssembler.class);
	
	@Override
	public BaseDealSnapshot assemble(BaseDeal deal) {
		
		PhysicalDealSnapshot snapshot = new PhysicalDealSnapshot();
		
		setCommonAttributes(deal, snapshot);
		
		if (deal instanceof PhysicalDeal == false) {
			logger.fatal("Invalid class passed into assembler - expecting PhysicalDeal. dealNo=", deal.getDealDetail().getTicketNo());
			throw new OBRuntimeException(DealErrorCode.INVALID_DEAL_ID.getCode());
		}
		
		PhysicalDeal physicalDeal = (PhysicalDeal) deal;
		snapshot.getDetail().copyFrom(physicalDeal.getDetail());

		if (physicalDeal.getDealPriceIndex() != null) {
			snapshot.setDealPriceIndexId(physicalDeal.getDealPriceIndex().generateEntityId());
		}

		snapshot.setMarketPriceIndexId(
				physicalDeal.getMarketPriceIndex().generateEntityId());
		snapshot.setMarketCurrencyCode(physicalDeal.getMarketPriceIndex().getDetail().getCurrencyCode());
		
		return snapshot;
	}


}
