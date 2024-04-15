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

import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.model.OrganizationRole;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class OrganizationRoleSnapshotAssemblerFactory {
	
	private static Map<OrganizationRoleType, Supplier<AbstractOrganizationRoleAssembler>> assemblersMap = 
			new HashMap<OrganizationRoleType, Supplier<AbstractOrganizationRoleAssembler>>();
	
	static {
		
		assemblersMap.put(OrganizationRoleType.COMPANY_ROLE, CompanyRoleAssembler::new);
		assemblersMap.put(OrganizationRoleType.COUNTERPARTY_ROLE, CounterpartyRoleAssembler::new);
		
	}

	public static AbstractOrganizationRoleAssembler newAssembler(OrganizationRoleType roleType) {
		return assemblersMap.get(roleType).get();
	}
	
	
	public static List<OrganizationRoleSnapshot> assemble(List<OrganizationRole> roles) {
		ArrayList<OrganizationRoleSnapshot> snapshots = new ArrayList<OrganizationRoleSnapshot>();
		
		for (OrganizationRole role : roles) {
			AbstractOrganizationRoleAssembler assembler = newAssembler(role.getOrganizationRoleType());
			snapshots.add(
					assembler.assemble(role));
		}
		
		return snapshots;
	}
	
	
	
}
