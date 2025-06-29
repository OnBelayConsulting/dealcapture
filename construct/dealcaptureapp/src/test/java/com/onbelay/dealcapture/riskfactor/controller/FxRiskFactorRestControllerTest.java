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
package com.onbelay.dealcapture.riskfactor.controller;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactorFixture;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshotCollection;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@WithMockUser(username="test")
public class FxRiskFactorRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(FxRiskFactorRestControllerTest.class);
	
	@Autowired
	private FxRiskFactorRestController fxRiskFactorRestController;

	@Autowired
	private FxIndexRepository fxIndexRepository;
	@Autowired
	private FxRiskFactorService fxRiskFactorService;

	@Autowired
	private FxCurveRepository fxCurveRepository;

	private FxIndex cadUsdfxIndex;
	private FxIndex cadEurFxIndex;

	private List<FxRiskFactor> fxRiskFactors;

	private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
	private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

	@Override
	public void setUp() {
		super.setUp();

		cadUsdfxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.DAILY,
				CurrencyCode.USD,
				CurrencyCode.CAD);

		FxIndexFixture.generateDailyFxCurves(
				cadUsdfxIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(10, 1, 1, 1, 1));

		cadEurFxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				CurrencyCode.EURO);

		fxRiskFactors = FxRiskFactorFixture.createFxRiskFactors(
				cadUsdfxIndex,
				fromMarketDate,
				toMarketDate);

		FxIndexFixture.generateDailyFxCurves(
				cadUsdfxIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(2023, 10, 1, 0, 0));


		flush();
	}
	
	
	@Test
	public void updateRiskFactor() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(fxRiskFactorRestController)
				.build();

		FxRiskFactorSnapshot existing = fxRiskFactorService.load(fxRiskFactors.get(0).generateEntityId());
		existing.setEntityState(EntityState.MODIFIED);
		existing.getDetail().setValue(BigDecimal.TEN);

		String jsonPayload = objectMapper.writeValueAsString(List.of(existing));
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(put("/api/fxIndices/" + cadUsdfxIndex.getId() + "/riskFactors")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();

		logger.error(mvcResult.getResponse().getStatus());

		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertTrue(transactionResult.isSuccessful());
		
	}

	@Test
	public void fetchFxRiskFactorsWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(fxRiskFactorRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/fxIndices/riskFactors?query=WHERE marketDate gt '2020-01-01'"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		

		FxRiskFactorSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, FxRiskFactorSnapshotCollection.class);
		
		assertEquals(31, collection.getSnapshots().size());
		
		assertEquals(31, collection.getCount());
		
		assertEquals(31, collection.getTotalItems());
	}


}
