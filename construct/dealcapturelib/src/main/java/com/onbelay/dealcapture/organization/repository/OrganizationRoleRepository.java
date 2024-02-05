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
package com.onbelay.dealcapture.organization.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRole;

import java.util.List;

public interface OrganizationRoleRepository {
	
	public static final String BEAN_NAME = "organizationRoleRepository";

	public OrganizationRole load(EntityId entityId);
	
	public List<OrganizationRole> fetchByIds(QuerySelectedPage selectedPage);
	
	public List<OrganizationRole> fetchByOrganizationId(Integer id);
	
	public List<Integer> findOrganizationRoleIds(DefinedQuery query);
	
	public List<OrganizationRole> findByShortName(String name);


    OrganizationRole getByShortNameAndRoleType(
			String organizationShortName,
			OrganizationRoleType organizationRoleType);
}
