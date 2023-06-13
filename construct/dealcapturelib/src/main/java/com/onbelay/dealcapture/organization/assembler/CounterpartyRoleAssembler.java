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
package com.onbelay.dealcapture.organization.assembler;

import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRole;
import com.onbelay.dealcapture.organization.snapshot.CounterpartyRoleSnapshot;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;

public class CounterpartyRoleAssembler extends AbstractOrganizationRoleAssembler {

	@Override
	public OrganizationRoleSnapshot assemble(OrganizationRole role) {
		
		CounterpartyRole counterpartyRole = (CounterpartyRole) role;
		CounterpartyRoleSnapshot counterpartyRoleSnapshot = new CounterpartyRoleSnapshot();
		super.setCommonAttributes(role, counterpartyRoleSnapshot);
		
		counterpartyRole.getDetail().copyFrom(counterpartyRole.getDetail());
		return counterpartyRoleSnapshot;
	}

	@Override
	public OrganizationRole assembleEntity(OrganizationRoleSnapshot snapshot) {
		
		CounterpartyRole counterpartyRole = new CounterpartyRole();
		
		counterpartyRole.createWith((CounterpartyRoleSnapshot)snapshot);
		return counterpartyRole;
	}

}
