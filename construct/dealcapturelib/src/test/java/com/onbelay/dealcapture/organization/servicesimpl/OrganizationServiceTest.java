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
package com.onbelay.dealcapture.organization.servicesimpl;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.snapshot.DefinedOrderByClause;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.service.OrganizationService;
import com.onbelay.dealcapture.organization.snapshot.*;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganizationServiceTest extends DealCaptureSpringTestCase {

	@Autowired
	private OrganizationService organizationService;


	public void setUp() {
		super.setUp();

		flush();
	}


	@Test
	public void createOrganization() {

		OrganizationSnapshot snapshot = new OrganizationSnapshot();
		snapshot.getDetail().setLegalName("DDD");
		snapshot.getDetail().setShortName("EEE");
		TransactionResult result  = organizationService.save(snapshot);
		flush();
		clearCache();
		
		assertTrue(result.isSuccessful());
	}

	@Test
	public void fetchOrganizationIds() {
		DefinedQuery definedQuery = new DefinedQuery("Organization");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression("shortName", ExpressionOperator.LIKE, "On%")
		);
		QuerySelectedPage selectedPage = organizationService.findOrganizationIds(definedQuery);
		assertEquals(1, selectedPage.getIds().size());
	}


	@Test
	public void createOrganizationRoles() {
		List<OrganizationRoleSnapshot> snapshots = new ArrayList<>();

		CompanyRoleSnapshot snapshot = new CompanyRoleSnapshot();
		snapshot.getDetail().setIsHoldingParent(false);
		snapshots.add(snapshot);

		CounterpartyRoleSnapshot counterpartyRoleSnapshot = new CounterpartyRoleSnapshot();
		counterpartyRoleSnapshot.getDetail().setSettlementCurrency(CurrencyCode.USD);
		snapshots.add(counterpartyRoleSnapshot);

		TransactionResult result = organizationService.saveOrganizationRoles(
				myOrganization.generateEntityId(),
				snapshots);
		flush();

		assertEquals(2, result.getIds().size());
	}


	@Test
	public void fetchOrganizationRoles() {
		List<OrganizationRoleSnapshot> snapshots = new ArrayList<>();

		CompanyRoleSnapshot snapshot = new CompanyRoleSnapshot();
		snapshot.getDetail().setIsHoldingParent(false);
		snapshots.add(snapshot);

		CounterpartyRoleSnapshot counterpartyRoleSnapshot = new CounterpartyRoleSnapshot();
		counterpartyRoleSnapshot.getDetail().setSettlementCurrency(CurrencyCode.USD);
		snapshots.add(counterpartyRoleSnapshot);

		TransactionResult result = organizationService.saveOrganizationRoles(
				myOrganization.generateEntityId(),
				snapshots);

		List<OrganizationRoleSnapshot> roleSnapshots = organizationService.fetchOrganizationRoles(myOrganization.generateEntityId());
		assertEquals(2, roleSnapshots.size());
	}


	@Test
	public void fetchOrganizationRoleSummaries() {
		List<OrganizationRoleSnapshot> snapshots = new ArrayList<>();

		CompanyRoleSnapshot snapshot = new CompanyRoleSnapshot();
		snapshot.getDetail().setIsHoldingParent(false);
		snapshots.add(snapshot);

		CounterpartyRoleSnapshot counterpartyRoleSnapshot = new CounterpartyRoleSnapshot();
		counterpartyRoleSnapshot.getDetail().setSettlementCurrency(CurrencyCode.USD);
		snapshots.add(counterpartyRoleSnapshot);

		TransactionResult result = organizationService.saveOrganizationRoles(
				myOrganization.generateEntityId(),
				snapshots);

		List<OrganizationRoleSummary> roleSnapshots = organizationService.findOrganizationRoleSummariesByIds(

				new QuerySelectedPage(
						result.getIds(),
						new DefinedOrderByClause()));

		assertEquals(2, roleSnapshots.size());
	}


	@Test
	public void fetchOrganizationRoleSummariesLikeShortName() {
		List<OrganizationRoleSnapshot> snapshots = new ArrayList<>();

		CompanyRoleSnapshot snapshot = new CompanyRoleSnapshot();
		snapshot.getDetail().setIsHoldingParent(false);
		snapshots.add(snapshot);

		CounterpartyRoleSnapshot counterpartyRoleSnapshot = new CounterpartyRoleSnapshot();
		counterpartyRoleSnapshot.getDetail().setSettlementCurrency(CurrencyCode.USD);
		snapshots.add(counterpartyRoleSnapshot);

		TransactionResult result = organizationService.saveOrganizationRoles(
				myOrganization.generateEntityId(),
				snapshots);
		flush();

		List<OrganizationRoleSummary> roleSnapshots = organizationService.findOrganizationRoleSummariesLikeShortName(
				"O",
				OrganizationRoleType.COMPANY_ROLE);

		assertEquals(1, roleSnapshots.size());
	}


}
