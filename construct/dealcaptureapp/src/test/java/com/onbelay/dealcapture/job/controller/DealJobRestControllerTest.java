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
package com.onbelay.dealcapture.job.controller;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.job.model.DealJobFixture;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WithMockUser(username="test")
public class DealJobRestControllerTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(DealJobRestControllerTest.class);
	
	@Autowired
	private DealJobRestController dealJobRestController;

	@Override
	public void setUp() {
		super.setUp();
	}
	
	
	@Test
	public void createDealJobWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(dealJobRestController)
				.build();

		DealJobSnapshot snapshot = DealJobFixture.createDealJobSnapshot(
				"default",
				"ddd",
				LocalDateTime.now(),
				CurrencyCode.CAD,
				LocalDate.now(),
				LocalDate.now());
		
		String jsonPayload = objectMapper.writeValueAsString(snapshot);
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/jobs")
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


}
