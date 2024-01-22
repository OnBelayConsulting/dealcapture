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
package com.onbelay.dealcapture.pricing.controller;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshotCollection;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WithMockUser(username="test")
public class FxIndexRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(FxIndexRestControllerTest.class);
	
	@Autowired
	private FxIndexRestController fxIndexRestController;

	@Autowired
	private FxIndexRepository fxIndexRepository;

	@Autowired
	private FxCurveRepository fxCurveRepository;

	private PricingLocation location;
	private FxIndex cadUsdfxIndex;
	private FxIndex cadEurFxIndex;

	private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
	private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

	@Override
	public void setUp() {
		super.setUp();
		location = PricingLocationFixture.createPricingLocation("West");

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


		FxIndexFixture.generateDailyFxCurves(
				cadUsdfxIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(2023, 10, 1, 0, 0));


		flush();
	}
	
	
	@Test
	public void createFxIndexWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(fxIndexRestController)
				.build();

		FxIndexSnapshot snapshot = FxIndexFixture.createFxIndexSnapshot(
				FrequencyCode.MONTHLY,
				CurrencyCode.CAD,
				CurrencyCode.EURO);
		
		String jsonPayload = objectMapper.writeValueAsString(snapshot);
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/fxIndices")
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
	public void createFxIndicesWithPut() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(fxIndexRestController)
				.build();


		FxIndexSnapshot snapshot = FxIndexFixture.createFxIndexSnapshot(
				FrequencyCode.MONTHLY,
				CurrencyCode.CAD,
				CurrencyCode.EURO);

		String jsonPayload = objectMapper.writeValueAsString(List.of(snapshot));

		logger.error(jsonPayload);

		ResultActions result = mvc.perform(put("/api/fxIndices")
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
	public void createFxIndexWithPostUsingJson() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(fxIndexRestController)
				.build();
		

		FxIndexSnapshot snapshot = new FxIndexSnapshot();
		snapshot.getDetail().setName("fred");
		String jsonPayloadG = objectMapper.writeValueAsString(snapshot);
		
//		logger.error(jsonPayloadG);

		
		
		String jsonPayload = "{\"detail\":{\"frequencyCodeValue\":\"M\",\"fromCurrencyCodeValue\":\"EURO\",\"toCurrencyCodeValue\" : \"CAD\"}}";

		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/fxIndices")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		logger.error(mvcResult.getResponse().getStatus());
		
		assertEquals(200, mvcResult.getResponse().getStatus());
		
		ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertTrue(transactionResult.isSuccessful());
		
	}
	
	
	@Test
	/*
	 * Should throw a missing location exception
	 */
	public void createFxIndexWithPostUsingJsonProducing400Exception() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(fxIndexRestController)
				.build();

		String jsonPayload = "{\"detail\":{\"name\":\"fred\", \"frequencyCodeValue\" : \"D\" }}";
		
		logger.debug(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/fxIndices")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		logger.error(mvcResult.getResponse().getStatus());
		
		assertEquals(400, mvcResult.getResponse().getStatus());

		String jsonStringResponse = mvcResult.getResponse().getContentAsString();

		logger.debug("Json: " + jsonStringResponse);
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertEquals(PricingErrorCode.MISSING_FX_INDEX_FREQUENCY.getCode(), transactionResult.getErrorCode());
	}
	
	
	
	
	@Test
	public void fetchFxIndicesWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(fxIndexRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/fxIndices"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		

		FxIndexSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, FxIndexSnapshotCollection.class);
		
		assertEquals(2, collection.getSnapshots().size());
		
		assertEquals(2, collection.getCount());
		
		assertEquals(2, collection.getTotalItems());
		
		for (FxIndexSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}

	
	@Test
	public void fetchFxIndicesWithGetUsingQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(fxIndexRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/fxIndices?query=WHERE toCurrency like 'C%'"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		FxIndexSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, FxIndexSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
		
		for (FxIndexSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}


	
}
