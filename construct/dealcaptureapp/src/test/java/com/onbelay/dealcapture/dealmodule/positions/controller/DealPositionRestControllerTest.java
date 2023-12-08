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
package com.onbelay.dealcapture.dealmodule.positions.controller;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.adapter.DealPositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionsFixture;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshotCollection;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WithMockUser(username="test")
public class DealPositionRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(DealPositionRestControllerTest.class);
	
	@Autowired
	private DealPositionRestController dealPositionRestController;

	@Autowired
	private FxRiskFactorService fxRiskFactorService;

	@Autowired
	private PriceRiskFactorService priceRiskFactorService;

	@Autowired
	private DealPositionService dealPositionService;

	@Autowired
	private GeneratePositionsService generatePositionsService;

	private PricingLocation location;
	private PriceIndex priceIndex;
	private PriceRiskFactor priceRiskFactor;

	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;

	private PhysicalDeal physicalDeal;

	private FxIndex fxIndex;

	private FxRiskFactor fxRiskFactor;

	private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
	private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);
		location = PricingLocationFixture.createPricingLocation("West");

		fxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.MONTHLY,
				CurrencyCode.USD,
				CurrencyCode.CAD);

		FxIndexFixture.generateDailyFxCurves(
				fxIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(10, 1, 1, 1, 1));

		fxRiskFactor = FxRiskFactorFixture.createFxRiskFactor(fxIndex, fromMarketDate);

		priceIndex = PriceIndexFixture.createPriceIndex(
				"ACEE",
				FrequencyCode.MONTHLY,
				location);

		PriceIndexFixture.generateMonthlyPriceCurves(
				priceIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(2023, 10, 1, 0, 0));

		physicalDeal = DealFixture.createFixedPricePhysicalDeal(
				"5566",
				companyRole,
				counterpartyRole,
				priceIndex,
				fromMarketDate,
				toMarketDate,
				CurrencyCode.CAD,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.USD,
						UnitOfMeasureCode.GJ)
		);

		priceRiskFactor = PriceRiskFactorFixture.createPriceRiskFactor(
				priceIndex,
				fromMarketDate);

		flush();
	}

	@Test
	public void saveDealPositionWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealPositionRestController)
				.build();

		List<DealPositionSnapshot> snapshots = PhysicalPositionsFixture.createPositions(
				physicalDeal,
				priceRiskFactor,
				fxRiskFactor);

		String jsonPayload = objectMapper.writeValueAsString(snapshots.get(0));
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/positions")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertTrue(transactionResult.isSuccessful());

		List<DealPositionSnapshot> created = dealPositionService.findByDeal(physicalDeal.generateEntityId());
		assertTrue(created.size() > 0);

	}


	@Test
	public void saveDealPositionsWithPut() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealPositionRestController)
				.build();

		List<DealPositionSnapshot> snapshots = PhysicalPositionsFixture.createPositions(
				physicalDeal,
				priceRiskFactor,
				fxRiskFactor);

		String jsonPayload = objectMapper.writeValueAsString(snapshots);

		logger.error(jsonPayload);

		ResultActions result = mvc.perform(put("/api/positions")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonPayload));

		MvcResult mvcResult = result.andReturn();

		String jsonStringResponse = mvcResult.getResponse().getContentAsString();

		logger.debug("Json: " + jsonStringResponse);

		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertTrue(transactionResult.isSuccessful());

		List<DealPositionSnapshot> created = dealPositionService.findByDeal(physicalDeal.generateEntityId());
		assertTrue(created.size() > 0);

	}

	
	@Test
	public void fetchDealPositionsWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealPositionRestController)
				.build();

		generatePositionsService.generatePositions(
				EvaluationContext
						.build()
						.withCurrency(CurrencyCode.CAD)
						.withStartPositionDate(fromMarketDate)
						.withEndPositionDate(toMarketDate),
				List.of(physicalDeal.getId()));
		
		ResultActions result = mvc.perform(get("/api/positions"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		DealPositionSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, DealPositionSnapshotCollection.class);
		
		assertEquals(31, collection.getSnapshots().size());
		
		assertEquals(31, collection.getCount());
		
		assertEquals(31, collection.getTotalItems());
	}

	
	@Test
	@WithMockUser(username="test")
	public void valuePositions() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealPositionRestController)
				.build();

		
		ResultActions result = mvc.perform(post("/api/positions/value"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		
		assertEquals(true, transactionResult.isSuccessful());
		
	}

}
