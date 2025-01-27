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
import com.onbelay.dealcapture.pricing.model.InterestIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshotCollection;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WithMockUser(username="test")
public class InterestIndexRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(InterestIndexRestControllerTest.class);
	
	@Autowired
	private InterestIndexRestController interestIndexRestController;
	
	@Override
	public void setUp() {
		super.setUp();

		InterestIndexFixture.createInterestIndex(
				"INTM",
				FrequencyCode.MONTHLY);

		flush();
		clearCache();
	}
	
	
	@Test
	public void createInterestIndexWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(interestIndexRestController)
				.build();

		InterestIndexSnapshot snapshot = InterestIndexFixture.createInterestIndexSnapshot("INTD");
		
		String jsonPayload = objectMapper.writeValueAsString(snapshot);
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/interestIndices")
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
	public void createInterestIndicesWithPut() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(interestIndexRestController)
				.build();

		InterestIndexSnapshot snapshot = InterestIndexFixture.createInterestIndexSnapshot("EBEE");

		String jsonPayload = objectMapper.writeValueAsString(List.of(snapshot));

		logger.error(jsonPayload);

		ResultActions result = mvc.perform(put("/api/interestIndices")
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
	public void createInterestIndexWithPostUsingJson() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(interestIndexRestController)
				.build();

		String jsonPayload = "{\"detail\":{\"name\":\"fred\",\"isRiskFreeRate\" : false, \"frequencyCodeValue\": \"D\"} }";

		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/interestIndices")
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
	public void createInterestIndexWithPostUsingJsonProducing400Exception() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(interestIndexRestController)
				.build();

		String jsonPayload = "{\"detail\":{ }}";
		
		logger.debug(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/interestIndices")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		logger.error(mvcResult.getResponse().getStatus());
		
		assertEquals(400, mvcResult.getResponse().getStatus());

		String jsonStringResponse = mvcResult.getResponse().getContentAsString();

		logger.debug("Json: " + jsonStringResponse);
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertEquals(PricingErrorCode.MISSING_INTEREST_INDEX_NAME.getCode(), transactionResult.getErrorCode());
	}
	
	
	
	
	@Test
	public void fetchInterestIndicesWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(interestIndexRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/interestIndices"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		

		InterestIndexSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, InterestIndexSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
		
		for (InterestIndexSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}

	
	@Test
	public void fetchInterestIndicesWithGetUsingQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(interestIndexRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/interestIndices?query=WHERE name like 'INT%'"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		InterestIndexSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, InterestIndexSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
		
		for (InterestIndexSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}


	
}
