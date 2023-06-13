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
package com.onbelay.dealcapture.dealmodule.deal.services;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.enums.BuySellType;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.PricingIndex;
import com.onbelay.dealcapture.pricing.model.PricingIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DealServiceTest extends DealCaptureSpringTestCase {
	
	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;
	private PricingIndex pricingIndex;

	@Autowired
	private DealRepository dealRepository;
	
	@Autowired
	private DealService dealService;

	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);
		
		pricingIndex = PricingIndexFixture.createPricingIndex(
				"AECO", 
				PricingLocationFixture.createPricingLocation(
						"west"));
		
		flush();
	}

	@Test
	public void testUpdateDeal() {
		PhysicalDeal physicalDeal = DealFixture.createPhysicalDeal(
				"myDeal", 
				companyRole, 
				counterpartyRole, 
				pricingIndex);
		flush();
		clearCache();
		
		PhysicalDealSnapshot snapshot = new PhysicalDealSnapshot();
		snapshot.getDealDetail().setBuySellType(BuySellType.BUY);
		snapshot.setEntityId(physicalDeal.generateEntityId());
		snapshot.setEntityState(EntityState.MODIFIED);
		
		dealService.save(snapshot);
		flush();
		clearCache();
		
		physicalDeal = (PhysicalDeal) dealRepository.load(physicalDeal.generateEntityId());
		
		assertNotNull(physicalDeal.getCompanyRole());
		assertNotNull(physicalDeal.getCounterpartyRole());
		assertNotNull(physicalDeal.getMarketPricingIndex());
		
		assertEquals(BuySellType.BUY, physicalDeal.getDealDetail().getBuySellType());
		
		flush();
		clearCache();
		
		
	}
	
	
	@Test
	public void testCreateDeal() {

		PhysicalDealSnapshot dealSnapshot = DealFixture.createPhysicalDealSnapshot(
				"mydeal",
				companyRole, 
				counterpartyRole, 
				pricingIndex);
		
		TransactionResult result  = dealService.save(dealSnapshot);
		flush();
		clearCache();
		
		
	}
	
}
