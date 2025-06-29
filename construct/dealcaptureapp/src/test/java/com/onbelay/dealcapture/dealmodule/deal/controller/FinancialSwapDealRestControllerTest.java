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
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;
import com.onbelay.dealcapture.businesscontact.model.BusinessContactFixture;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.adapter.DealRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileFixture;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
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
FinancialSwapDealRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(FinancialSwapDealRestControllerTest.class);
	
	@Autowired
	private DealRestController dealRestController;

	@Autowired
	private DealRepository dealRepository;

	@Autowired
	private DealRestAdapter dealRestAdapter;
	private BusinessContact contact;
	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;

	protected PriceIndex receivesIndex;
	protected PriceIndex paysIndex;

	protected FinancialSwapDeal fixed4FloatSellDeal;
	protected FinancialSwapDeal fixed4FloatBuyDeal;
	protected FinancialSwapDeal float4FloatBuyDeal;
	protected FinancialSwapDeal float4FloatPlusBuyDeal;
	protected FinancialSwapDeal fixed4PowerProfileBuyDeal;

	protected PriceIndex settledHourlyIndex;
	protected PriceIndex onPeakDailyIndex;
	protected PriceIndex offPeakDailyIndex;

	protected PowerProfile powerProfile;

	private LocalDate startDate = LocalDate.of(2022, 1, 1);
	private LocalDate endDate = LocalDate.of(2022, 12, 31);

	protected PricingLocation pricingLocation;


	@Override
	public void setUp() {
		super.setUp();
		contact = BusinessContactFixture.createCompanyTrader("hans", "gruber", "gruber@terror.com");
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);
		pricingLocation = PricingLocationFixture.createPricingLocation("west");


		settledHourlyIndex = PriceIndexFixture.createPriceIndex(
				"SETTLE",
				FrequencyCode.HOURLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		onPeakDailyIndex = PriceIndexFixture.createPriceIndex(
				"ON PEAK",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		offPeakDailyIndex = PriceIndexFixture.createPriceIndex(
				"OFF PEAK",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		powerProfile = PowerProfileFixture.createPowerProfileAllDaysAllHours(
				"24By7",
				settledHourlyIndex,
				offPeakDailyIndex,
				onPeakDailyIndex);



		receivesIndex = PriceIndexFixture.createPriceIndex(
				"Receives",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		paysIndex = PriceIndexFixture.createPriceIndex(
				"Pays",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		flush();

		FinancialSwapDealSnapshot snapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"f4floatsell",
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
		snapshot.setCompanyTraderId(contact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

		fixed4FloatSellDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"f4floatbuy",
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
		snapshot.setCompanyTraderId(contact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

		fixed4FloatBuyDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createFloat4FloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"float4floatbuy",
				companyRole,
				counterpartyRole,
				paysIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				receivesIndex);
		snapshot.setCompanyTraderId(contact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		float4FloatBuyDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createFloat4FloatPlusSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"float4floatPlusbuy",
				companyRole,
				counterpartyRole,
				paysIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				receivesIndex,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
		snapshot.setCompanyTraderId(contact.generateEntityId());
		float4FloatPlusBuyDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createWithPowerProfileFinancialSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"float4powerprofbuy",
				companyRole,
				counterpartyRole,
				powerProfile,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));


		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		snapshot.setCompanyTraderId(contact.generateEntityId());
		fixed4PowerProfileBuyDeal = FinancialSwapDeal.create(snapshot);

		flush();
		clearCache();

		fixed4FloatSellDeal = (FinancialSwapDeal) dealRepository.load(fixed4FloatSellDeal.generateEntityId());
		float4FloatBuyDeal = (FinancialSwapDeal) dealRepository.load(float4FloatBuyDeal.generateEntityId());
		float4FloatPlusBuyDeal = (FinancialSwapDeal) dealRepository.load(float4FloatPlusBuyDeal.generateEntityId());
		fixed4PowerProfileBuyDeal = (FinancialSwapDeal) dealRepository.load(fixed4PowerProfileBuyDeal.generateEntityId());

	}
	
	
	@Test
	public void createDealWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealRestController)
				.build();


		FinancialSwapDealSnapshot snapshot = FinancialSwapDealFixture.createWithPowerProfileFinancialSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"Newfloat4ppbuy",
				companyRole,
				counterpartyRole,
				powerProfile,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
		snapshot.setCompanyTraderId(contact.generateEntityId());
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
		FinancialSwapDealSnapshot created = (FinancialSwapDealSnapshot) dealRestAdapter.load(transactionResult.getEntityId());
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
		
		assertEquals(5, collection.getSnapshots().size());
		
		assertEquals(5, collection.getCount());
		
		assertEquals(5, collection.getTotalItems());
		
		for (BaseDealSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}

	
	@Test
	@WithMockUser(username="test")
	public void testGetWithQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/deals?query=WHERE ticketNo contains 'float4float'"));
		
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


	
}
