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
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
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
		
		PhysicalDeal deal = PhysicalDeal.create(
				new PhysicalDealSnapshot(
						companyRole.generateEntityId(), 
						counterpartyRole.generateEntityId(),
						new DealDetail(
							DealStatusCode.PENDING,
							BuySellCode.BUY,
							"deal-12n",
							LocalDate.of(2019, 1, 1),
							LocalDate.of(2019, 1, 1),
							BigDecimal.valueOf(34.78),
							UnitOfMeasureCode.GJ,
							CurrencyCode.US),
						new PhysicalDealDetail(
								BigDecimal.valueOf(1.55),
								CurrencyCode.CAD,
								UnitOfMeasureCode.GJ)));
		
		flush();
		clearCache();
		
		List<BaseDeal> deals = dealRepository.fetchAllDeals();
		assertEquals(1, deals.size());
		
		PhysicalDeal created = (PhysicalDeal) deals.get(0);
		
		assertTrue(created.getId().longValue() > 0);
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
						UnitOfMeasureCode.GJ, 
						BigDecimal.valueOf(34.78)));
		
		dealSnapshot.getDetail().setDealPrice(
				new Price(
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ,
						BigDecimal.valueOf(1.55)));
		
		PhysicalDeal physicalDeal = PhysicalDeal.create(dealSnapshot);
		
		clearCache();
		
		List<BaseDeal> deals = dealRepository.fetchAllDeals();
		assertEquals(1, deals.size());
		
		PhysicalDeal created = (PhysicalDeal) deals.get(0);
	}
	
}
