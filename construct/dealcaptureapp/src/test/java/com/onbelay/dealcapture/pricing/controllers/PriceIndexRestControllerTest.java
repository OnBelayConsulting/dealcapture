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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
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

import com.onbelay.dealcapture.pricing.controller.PriceIndexRestController;
import com.onbelay.dealcapture.pricing.enums.PricingErrorCode;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshotCollection;

import java.util.List;


@WithMockUser(username="test")
public class PriceIndexRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(PriceIndexRestControllerTest.class);
	
	@Autowired
	private PriceIndexRestController priceIndexRestController;
	
	@Autowired
	private PricingLocationRepository pricingLocationRepository;
	
	private PricingLocation pricingLocation;

	
	@Override
	public void setUp() {
		super.setUp();
		pricingLocation = PricingLocationFixture.createPricingLocation("west");
		
		PriceIndexFixture.createPriceIndex("AADC", pricingLocation);
		PriceIndexFixture.createPriceIndex("Bddd", pricingLocation);

		flush();
		clearCache();
		pricingLocation = pricingLocationRepository.findByName("west"); 
	}
	
	
	@Test
	public void createPriceIndexWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(priceIndexRestController)
				.build();

		PriceIndexSnapshot snapshot = PriceIndexFixture.createPriceIndexSnapshot(
				"EBEE", 
				pricingLocation);
		
		String jsonPayload = objectMapper.writeValueAsString(snapshot);
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/priceIndices")
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
	public void createPriceIndicesWithPut() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(priceIndexRestController)
				.build();

		PriceIndexSnapshot snapshot = PriceIndexFixture.createPriceIndexSnapshot(
				"EBEE",
				pricingLocation);

		String jsonPayload = objectMapper.writeValueAsString(List.of(snapshot));

		logger.error(jsonPayload);

		ResultActions result = mvc.perform(put("/api/priceIndices")
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
	public void createPriceIndexWithPostUsingJson() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(priceIndexRestController)
				.build();
		

		PriceIndexSnapshot snapshot = new PriceIndexSnapshot();
		snapshot.getDetail().setName("fred");
		String jsonPayloadG = objectMapper.writeValueAsString(snapshot);
		
//		logger.error(jsonPayloadG);

		
		
		String jsonPayload = "{\"detail\":{\"name\":\"fred\",\"indexTypeValue\" : \"H\"},\"pricingLocationId\":{\"id\":" + pricingLocation.getId() + "}}";
//		String jsonPayloadFull = "{\"entityState\":\"NEW\",\"entityId\":null,\"version\":-1,\"detail\":{\"name\":\"EBEE\",\"description\":\"EBEE-Desc\",\"daysOffsetForExpiry\":4,\"indexTypeValue\":\"H\"},\"benchmarkIndexSlot\":null,\"baseIndexSlot\":null,\"pricingLocationSlot\":{\"entityId\":{\"id\":44,\"status\":\"VALID\"},\"version\":0,\"code\":\"west\"},\"links\":[]}";
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/priceIndices")
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
	public void createPriceIndexWithPostUsingJsonProducing400Exception() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(priceIndexRestController)
				.build();

		String jsonPayload = "{\"detail\":{\"name\":\"fred\", \"indexTypeValue\" : \"H\" }}";
		
		logger.debug(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/priceIndices")
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
	public void fetchPriceIndicesWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(priceIndexRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/priceIndices"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		

		PriceIndexSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, PriceIndexSnapshotCollection.class);
		
		assertEquals(2, collection.getSnapshots().size());
		
		assertEquals(2, collection.getCount());
		
		assertEquals(2, collection.getTotalItems());
		
		for (PriceIndexSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}

	
	@Test
	public void fetchPriceIndicesWithGetUsingQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(priceIndexRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/priceIndices?query=WHERE name like 'B%'"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		PriceIndexSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, PriceIndexSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
		
		for (PriceIndexSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}


	
}
