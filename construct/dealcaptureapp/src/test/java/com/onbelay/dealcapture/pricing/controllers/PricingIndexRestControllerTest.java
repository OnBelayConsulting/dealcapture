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
package com.onbelay.dealcapture.pricing.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.onbelay.core.entity.snapshot.TransactionResult;
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
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onbelay.core.entity.persistence.TransactionalSpringTestCase;
import com.onbelay.dealcapture.pricing.controller.PricingIndexRestController;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.PricingIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshotCollection;


@WithMockUser(username="test")
public class PricingIndexRestControllerTest extends DealCaptureSpringTestCase {
	private static final Logger logger = LogManager.getLogger(PricingIndexRestControllerTest.class);
	
	@Autowired
	private PricingIndexRestController pricingIndexRestController;
	
	@Autowired
	private PricingLocationRepository pricingLocationRepository;
	
	private PricingLocation pricingLocation;

	
	@Override
	public void setUp() {
		super.setUp();
		pricingLocation = PricingLocationFixture.createPricingLocation("west");
		
		PricingIndexFixture.createPricingIndex("AADC", pricingLocation);
		PricingIndexFixture.createPricingIndex("Bddd", pricingLocation);

		flush();
		clearCache();
		pricingLocation = pricingLocationRepository.findByName("west"); 
	}
	
	
	@Test
	public void createPricingIndexWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingIndexRestController)
				.build();

		PricingIndexSnapshot snapshot = PricingIndexFixture.createPricingIndexSnapshot(
				"EBEE", 
				pricingLocation);
		
		String jsonPayload = objectMapper.writeValueAsString(snapshot);
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/pricingIndices")
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
	public void createPricingIndexWithPostUsingJson() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingIndexRestController)
				.build();
		

		PricingIndexSnapshot snapshot = new PricingIndexSnapshot();
		snapshot.getDetail().setName("fred");
		String jsonPayloadG = objectMapper.writeValueAsString(snapshot);
		
//		logger.error(jsonPayloadG);

		
		
		String jsonPayload = "{\"detail\":{\"name\":\"fred\"},\"pricingLocationId\":{\"id\":" + pricingLocation.getId() + "}}";
//		String jsonPayloadFull = "{\"entityState\":\"NEW\",\"entityId\":null,\"version\":-1,\"detail\":{\"name\":\"EBEE\",\"description\":\"EBEE-Desc\",\"daysOffsetForExpiry\":4,\"indexTypeValue\":\"H\"},\"benchmarkIndexSlot\":null,\"baseIndexSlot\":null,\"pricingLocationSlot\":{\"entityId\":{\"id\":44,\"status\":\"VALID\"},\"version\":0,\"code\":\"west\"},\"links\":[]}";
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/pricingIndices")
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
	public void createPricingIndexWithPostUsingJsonProducing400Exception() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingIndexRestController)
				.build();

		String jsonPayload = "{\"detail\":{\"name\":\"fred\"}}";
		
		logger.debug(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/pricingIndices")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		logger.error(mvcResult.getResponse().getStatus());
		
		assertEquals(400, mvcResult.getResponse().getStatus());

		String jsonStringResponse = mvcResult.getResponse().getContentAsString();

		logger.debug("Json: " + jsonStringResponse);
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertEquals(PricingErrorCode.MISSING_PRICING_LOCATION.getCode(), transactionResult.getErrorCode());
	}
	
	
	
	
	@Test
	public void fetchPricingIndicesWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingIndexRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/pricingIndices"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		

		PricingIndexSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, PricingIndexSnapshotCollection.class);
		
		assertEquals(2, collection.getSnapshots().size());
		
		assertEquals(2, collection.getCount());
		
		assertEquals(2, collection.getTotalItems());
		
		for (PricingIndexSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}

	
	@Test
	public void fetchPricingIndicesWithGetUsingQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingIndexRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/pricingIndices?query=WHERE name like 'B%'"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		PricingIndexSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, PricingIndexSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
		
		for (PricingIndexSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}


	
}
