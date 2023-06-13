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
package com.onbelay.dealcapture.dealmodule.deal.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.adapter.DealRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onbelay.core.entity.persistence.TransactionalSpringTestCase;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.PricingIndex;
import com.onbelay.dealcapture.pricing.model.PricingIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;

@WithMockUser(username="test")
public class DealRestControllerTest extends DealCaptureSpringTestCase {
	private static final Logger logger = LogManager.getLogger(DealRestControllerTest.class);
	
	@Autowired
	private DealRestController dealRestController;

	@Autowired
	private DealRestAdapter dealRestAdapter;


	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;
	private PricingIndex pricingIndex;

	private PhysicalDeal physicalDeal;
	
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

		physicalDeal = DealFixture.createPhysicalDeal(
				"mine",
				companyRole, 
				counterpartyRole, 
				pricingIndex);
		flush();
	}
	
	
	@Test
	public void createDealWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealRestController)
				.build();
		

		PhysicalDealSnapshot snapshot = DealFixture.createPhysicalDealSnapshot(
				"hht-3", 
				companyRole, 
				counterpartyRole, 
				pricingIndex);
		
		String jsonPayload = objectMapper.writeValueAsString(snapshot);
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/deals")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertTrue(transactionResult.isSuccessful());
		PhysicalDealSnapshot created = (PhysicalDealSnapshot) dealRestAdapter.load(transactionResult.getEntityId());
		assertEquals(snapshot.getDealDetail().getTicketNo(), created.getDealDetail().getTicketNo());
	}
	
	
	
	
	@Test
	public void fetchDealsWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/deals"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		DealSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, DealSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
		
		for (BaseDealSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}

	
	@Test
	@WithMockUser(username="test")
	public void testGetWithQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/deals?query=WHERE ticketNo = 'mine'"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		DealSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, DealSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
		
		for (BaseDealSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}


	
}
