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
package com.onbelay.dealcapture.organization.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.organization.snapshot.*;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onbelay.core.entity.persistence.TransactionalSpringTestCase;
import com.onbelay.dealcapture.organization.controller.OrganizationRestController;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;

@WithMockUser(username="test")
public class OrganizationRestControllerTest extends DealCaptureSpringTestCase {
	private static final Logger logger = LogManager.getLogger(OrganizationRestControllerTest.class);
	
	@Autowired
	private OrganizationRestController organizationRestController;
	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;

	
	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);
		
		flush();
	}
	
	
	@Test
	public void createOrganizationWithPost() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(organizationRestController)
				.build();

		OrganizationSnapshot snapshot = new OrganizationSnapshot();
		snapshot.getDetail().setShortName("mine");
		snapshot.getDetail().setLegalName("yours");

		String jsonPayload = objectMapper.writeValueAsString(snapshot);
		
		logger.error(jsonPayload);
		
		ResultActions result = mvc.perform(post("/api/organizations")
								.accept(MediaType.APPLICATION_JSON)
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonPayload));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		
		TransactionResult transactionResult = objectMapper.readValue(jsonStringResponse, TransactionResult.class);
		
	}

	@Test
	public void fetchOrganizationsWithGet() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(organizationRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/organizations"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);
		

		OrganizationSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, OrganizationSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
	}

	
	@Test
	@WithMockUser(username="test")
	public void fetchOrganizationsWithGetAndQuery() throws Exception {
		
		MockMvc mvc = MockMvcBuilders.standaloneSetup(organizationRestController)
				.build();

		
		ResultActions result = mvc.perform(get("/api/organizations?query=WHERE shortName contains 'a'"));
		
		MvcResult mvcResult = result.andReturn();
		
		String jsonStringResponse = mvcResult.getResponse().getContentAsString();
		
		logger.debug("Json: " + jsonStringResponse);


		OrganizationSnapshotCollection collection = objectMapper.readValue(jsonStringResponse, OrganizationSnapshotCollection.class);
		
		assertEquals(1, collection.getSnapshots().size());
		
		assertEquals(1, collection.getCount());
		
		assertEquals(1, collection.getTotalItems());
	}



	@Test
	public void fetchOrganizationRoleSummariesWithGet() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(organizationRestController)
				.build();


		ResultActions result = mvc.perform(get("/api/organizations/roleSummaries"));

		MvcResult mvcResult = result.andReturn();

		String jsonStringResponse = mvcResult.getResponse().getContentAsString();

		logger.debug("Json: " + jsonStringResponse);


		OrganizationRoleSummaryCollection collection = objectMapper.readValue(jsonStringResponse, OrganizationRoleSummaryCollection.class);

		assertEquals(2, collection.getSnapshots().size());

		assertEquals(2, collection.getCount());

		assertEquals(2, collection.getTotalItems());

		for (OrganizationRoleSummary snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}


	@Test
	@WithMockUser(username="test")
	public void fetchOrganizationRoleSummariesWithGetAndQuery() throws Exception {

		MockMvc mvc = MockMvcBuilders.standaloneSetup(organizationRestController)
				.build();


		ResultActions result = mvc.perform(get("/api/organizations/roleSummaries?query=WHERE organizationRoleType eq 'CO'"));

		MvcResult mvcResult = result.andReturn();

		String jsonStringResponse = mvcResult.getResponse().getContentAsString();

		logger.debug("Json: " + jsonStringResponse);


		OrganizationRoleSummaryCollection collection = objectMapper.readValue(jsonStringResponse, OrganizationRoleSummaryCollection.class);

		assertEquals(1, collection.getSnapshots().size());

		assertEquals(1, collection.getCount());

		assertEquals(1, collection.getTotalItems());

		for (OrganizationRoleSummary snapshot : collection.getSnapshots()) {
			logger.error(snapshot.toString());
		}
	}



}
