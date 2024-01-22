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

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DealTest extends DealCaptureSpringTestCase {
	
	@Autowired
	private DealRepositoryBean dealRepository;
	
	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;

	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);
	}

	@Test
	public void testCreateDeal() {
		
		
		flush();

		PhysicalDealSnapshot snapshot = new PhysicalDealSnapshot();
		snapshot.setCompanyRoleId(companyRole.generateEntityId());
		snapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());
		snapshot.getDealDetail().setCommodityCode(CommodityCode.CRUDE);
		setDealAttributes(
				snapshot.getDealDetail(),
				DealStatusCode.PENDING,
				BuySellCode.BUY,
				"deal-12n",
				LocalDate.of(2019, 1, 1),
				LocalDate.of(2019, 1, 1),
				BigDecimal.valueOf(34.78),
				UnitOfMeasureCode.GJ,
				CurrencyCode.USD);

		snapshot.getDetail().setDealPriceValue(BigDecimal.valueOf(1.55));
		snapshot.getDetail().setDealPriceCurrency(CurrencyCode.CAD);
		snapshot.getDetail().setDealPriceUnitOfMeasure(UnitOfMeasureCode.GJ);

		PhysicalDeal deal = PhysicalDeal.create(snapshot);

		flush();
		clearCache();
		
		List<BaseDeal> deals = dealRepository.fetchAllDeals();
		assertEquals(1, deals.size());
		
		PhysicalDeal created = (PhysicalDeal) deals.get(0);
		
		assertTrue(created.getId().longValue() > 0);
	}

	private void setDealAttributes(
			DealDetail detail,
			DealStatusCode status,
			BuySellCode buySellCode,
			String ticketNo,
			LocalDate startDate,
			LocalDate endDate,
			BigDecimal quantity,
			UnitOfMeasureCode unitOfMeasureCode,
			CurrencyCode currencyCode) {

		detail.setDealStatus(status);
		detail.setBuySell(buySellCode);
		detail.setTicketNo(ticketNo);
		detail.setStartDate(startDate);
		detail.setEndDate(endDate);
		detail.setVolumeQuantity(quantity);
		detail.setReportingCurrencyCode(currencyCode);
		detail.setVolumeUnitOfMeasure(unitOfMeasureCode);

	}
	
	public void createDealFromSnapshot() {
		
		flush();

		
		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();
		
		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setDealStatus(DealStatusCode.PENDING);
		dealSnapshot.getDealDetail().setStartDate(LocalDate.of(2019, 1, 1));
		dealSnapshot.getDealDetail().setEndDate(LocalDate.of(2019, 12, 31));
		dealSnapshot.getDealDetail().setTicketNo("myTicket");
		
		dealSnapshot.getDealDetail().setVolume(
				new Quantity(
						BigDecimal.valueOf(34.78),
						UnitOfMeasureCode.GJ));
		
		dealSnapshot.getDetail().setDealPrice(
				new Price(
						BigDecimal.valueOf(1.55),
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));
		
		PhysicalDeal physicalDeal = PhysicalDeal.create(dealSnapshot);
		
		clearCache();
		
		List<BaseDeal> deals = dealRepository.fetchAllDeals();
		assertEquals(1, deals.size());
		
		PhysicalDeal created = (PhysicalDeal) deals.get(0);
	}
	
}
