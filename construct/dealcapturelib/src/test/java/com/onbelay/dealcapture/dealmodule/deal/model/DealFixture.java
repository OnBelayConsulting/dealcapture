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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.common.enums.CurrencyCode;
import com.onbelay.dealcapture.common.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.BuySellType;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatus;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.pricing.model.PricingIndex;

public class DealFixture {
	
	private DealFixture() { }

	public static PhysicalDealSnapshot createPhysicalDealSnapshot(
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PricingIndex pricingIndex)  {
		
		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();
		
		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setDealStatus(DealStatus.PENDING);
		dealSnapshot.getDealDetail().setBuySellType(BuySellType.SELL);
		dealSnapshot.getDealDetail().setStartDate(LocalDate.of(2019, 1, 1));
		dealSnapshot.getDealDetail().setEndDate(LocalDate.of(2019, 12, 31));
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);
		
		dealSnapshot.setMarketPricingIndexId(pricingIndex.generateEntityId());
		
		dealSnapshot.getDealDetail().setVolume(
				new Quantity(
						UnitOfMeasureCode.GJ, 
						BigDecimal.valueOf(34.78)));
		
		dealSnapshot.getPhysicalDealDetail().setDealPrice(
				new Price(
						CurrencyCode.CDN,
						UnitOfMeasureCode.GJ,
						BigDecimal.valueOf(1.55)));

		return dealSnapshot;
	}

	
	public static PhysicalDeal createPhysicalDeal(
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PricingIndex pricingIndex)  {
		
		return PhysicalDeal.create(
				createPhysicalDealSnapshot(
						ticketNo, 
						companyRole, 
						counterpartyRole, 
						pricingIndex));
		
	}
	

}
