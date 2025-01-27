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

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.adapter.DealRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.TradeTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDealFixture;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.VanillaOptionDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WithMockUser(username="test")
public class
VanillaOptionRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(VanillaOptionRestControllerTest.class);
	
	@Autowired
	private DealRestController dealRestController;

	@Autowired
	private DealRestAdapter dealRestAdapter;

	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;
	protected PricingLocation pricingLocation;

	protected PriceIndex monthlyOptionIndex;
	protected VanillaOptionDeal buyPutOptionDeal;
	protected VanillaOptionDeal buyCallOptionDeal;

	protected LocalDate startDate = LocalDate.of(2024, 1, 1);
	protected LocalDate endDate = LocalDate.of(2024, 1, 31);

	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);

		pricingLocation = PricingLocationFixture.createPricingLocation("west");

		monthlyOptionIndex = PriceIndexFixture.createPriceIndex(
				"MOPtIDX",
				FrequencyCode.MONTHLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		flush();


		VanillaOptionDealSnapshot snapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.NATGAS,
				TradeTypeCode.OTC,
				OptionTypeCode.PUT,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"buyPutOptDeal",
				companyRole,
				counterpartyRole,
				monthlyOptionIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.TEN,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		buyPutOptionDeal = VanillaOptionDeal.create(snapshot);

		snapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.NATGAS,
				TradeTypeCode.OTC,
				OptionTypeCode.CALL,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"buyCallOptDeal",
				companyRole,
				counterpartyRole,
				monthlyOptionIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.TEN,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		buyCallOptionDeal = VanillaOptionDeal.create(snapshot);

		flush();
	}
	
	
	@Test
	public void createDealWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealRestController)
				.build();


		VanillaOptionDealSnapshot snapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.NATGAS,
				TradeTypeCode.OTC,
				OptionTypeCode.CALL,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"NewOptDeal",
				companyRole,
				counterpartyRole,
				monthlyOptionIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.TEN,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		
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
		VanillaOptionDealSnapshot created = (VanillaOptionDealSnapshot) dealRestAdapter.load(transactionResult.getEntityId());
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
		
		assertEquals(2, collection.getSnapshots().size());
		
		assertEquals(2, collection.getCount());
		
		assertEquals(2, collection.getTotalItems());
		
		for (BaseDealSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}

	
	@Test
	@WithMockUser(username="test")
	public void testGetWithQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/deals?query=WHERE ticketNo contains 'Call'"));
		
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
