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

import java.util.List;

import javax.transaction.Transactional;

import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.organization.repository.OrganizationRoleRepository;

@Repository (value="organizationRoleRepository")
@Transactional

public class OrganizationRoleRepositoryBean extends BaseRepository<OrganizationRole> implements OrganizationRoleRepository {
	public static final String FIND_BY_SHORT_NAME = "FIND_DEAL_ORG_ROLE_BY_SHORT_NAME";
	public static final String FIND_BY_ORGANIZATION_ID = "FIND_DEAL_ORG_ROLE_BY_ORGANIZATION_ID";
	public static final String FETCH_SUMMARIES = "FETCH_ORG_ROLE_SUMMARIES";

	@Autowired
	private OrganizationRoleColumnDefinitions organizationRoleColumnDefinitions;

	@Override
	public OrganizationRole load(EntityId entityId) {

		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isSet())
			return find(OrganizationRole.class, entityId.getId());
		else
			return null;
	}


	@Override
	public List<OrganizationRole> findByShortName(String shortName) {
		return executeQuery(
					FIND_BY_SHORT_NAME,
					"shortName",
					shortName);
	}


	@Override
	public List<OrganizationRole> fetchByOrganizationId(Integer organizationId) {
		return executeQuery(
				FIND_BY_ORGANIZATION_ID, 
				"organizationId", 
				organizationId);
	}

	@Override
	public List<Integer> findOrganizationRoleIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				organizationRoleColumnDefinitions, 
				definedQuery);
	}

	@Override
	public List<OrganizationRole> fetchByIds(QuerySelectedPage querySelectedPage) {
		return fetchEntitiesById(
				organizationRoleColumnDefinitions,
				"OrganizationRole",
				querySelectedPage);
	}
	
	
	

}
