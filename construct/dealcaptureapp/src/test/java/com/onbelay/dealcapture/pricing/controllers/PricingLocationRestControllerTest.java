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

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.controller.PricingLocationRestController;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshotCollection;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WithMockUser(username="test")
public class PricingLocationRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(PricingLocationRestControllerTest.class);
	
	@Autowired
	private PricingLocationRestController pricingLocationRestController;
	
	private PricingLocation pricingLocation;

	
	@Override
	public void setUp() {
		super.setUp();
		pricingLocation = PricingLocationFixture.createPricingLocation("west");
		
		PricingLocationFixture.createPricingLocation("AADC");
		PricingLocationFixture.createPricingLocation("Eddd");

		flush();
	}
	
	
	@Test
	public void createLocationWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingLocationRestController)
				.build();

		PricingLocationSnapshot snapshot = PricingLocationFixture.createPricingLocationSnapshot("EBEE");
		
		String jsonPayload = objectMapper.writeValueAsString(snapshot);
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/pricingLocations")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertTrue(transactionResult.isSuccessful());
	}


	@Test
	public void createLocationsWithPut() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingLocationRestController)
				.build();

		PricingLocationSnapshot snapshot = PricingLocationFixture.createPricingLocationSnapshot("EBEE");

		String jsonPayload = objectMapper.writeValueAsString(List.of(snapshot));

		logger.error(jsonPayload);

		ResultActions result = mvc.perform(put("/api/pricingLocations")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonPayload));

		MvcResult mvcResult = result.andReturn();

		String jsonStringResponse = mvcResult.getResponse().getContentAsString();

		logger.debug("Json: " + jsonStringResponse);

		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertTrue(transactionResult.isSuccessful());
	}


	@Test
	public void createLocationWithPostUsingJson() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingLocationRestController)
				.build();

		PricingLocationSnapshot snapshot = PricingLocationFixture.createPricingLocationSnapshot("EBEE");
		
		String jsonPayload = "{\"detail\":{\"name\":\"fred\"}}";
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/pricingLocations")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		assertTrue(transactionResult.isSuccessful());
		
	}

	@Test
	public void fetchLocationsWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingLocationRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/pricingLocations"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		PricingLocationSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, PricingLocationSnapshotCollection.class);
		
		assertEquals(3, collection.getSnapshots().size());
		
		assertEquals(3, collection.getCount());
		
		assertEquals(3, collection.getTotalItems());
		
		for (PricingLocationSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}

	@Test
	public void fetchLocationsWithGetAndQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(pricingLocationRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/pricingLocations?query=WHERE name like 'E%'"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);

		PricingLocationSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, PricingLocationSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
		
		for (PricingLocationSnapshot snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}


	
}
