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
package com.onbelay.dealcapture.organization.model;

import com.onbelay.dealcapture.organization.enums.OrganizationRoleStatus;
import com.onbelay.dealcapture.organization.snapshot.CompanyRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.CounterpartyRoleSnapshot;

public class OrganizationRoleFixture {
	
	
	public static CompanyRoleSnapshot createCompanyRoleSnapshot() {
		
		CompanyRoleSnapshot companyRoleSnapshot = new CompanyRoleSnapshot();
		
		companyRoleSnapshot.getRoleDetail().setStatus(OrganizationRoleStatus.VERIFIED);
		
		companyRoleSnapshot.getDetail().setIsHoldingParent(true);

		return companyRoleSnapshot;
	}
			
	
	
	public static CompanyRole createCompanyRole(
			Organization organization) {
		
		CompanyRole companyRole = new CompanyRole();

		companyRole.getRoleDetail().setDefaults();

		companyRole.getDetail().setIsHoldingParent(true);
		organization.addOrganizationRole(companyRole);

		return companyRole;
	}

	
	public static CounterpartyRole createCounterpartyRole(
			Organization organization) {

		CounterpartyRole counterpartyRole = new CounterpartyRole();
		counterpartyRole.getRoleDetail().setDefaults();

		counterpartyRole.getDetail().setSettlementCurrencyCodeValue("CAD");
		
		organization.addOrganizationRole(counterpartyRole);
		return counterpartyRole;
	}

	
	public static CounterpartyRoleSnapshot createCounterpartyRoleSnapshot() {

		CounterpartyRoleSnapshot counterpartyRole = new CounterpartyRoleSnapshot();
		counterpartyRole.getRoleDetail().setDefaults();

		counterpartyRole.getDetail().setSettlementCurrencyCodeValue("CAD");
		
		return counterpartyRole;
	}

}
