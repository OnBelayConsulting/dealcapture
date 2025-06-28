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

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.organization.enums.OrganizationRoleType;
import com.onbelay.dealcapture.organization.repository.OrganizationRoleRepository;
import com.onbelay.dealcapture.organization.snapshot.OrganizationRoleSummary;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository (value="organizationRoleRepository")
@Transactional

public class OrganizationRoleRepositoryBean extends BaseRepository<OrganizationRole> implements OrganizationRoleRepository {
	public static final String FIND_BY_SHORT_NAME = "OrganizationRoleRepository.FIND_BY_SHORT_NAME";
	public static final String GET_BY_SHORT_NAME_ROLE_TYPE = "OrganizationRoleRepository.GET_BY_SHORT_NAME_ROLE_TYPE";
	public static final String FIND_BY_ORGANIZATION_ID = "OrganizationRoleRepository.FIND_BY_ORGANIZATION_ID";
	public static final String FIND_SUMMARIES_LIKE_SHORT_NAME = "OrganizationRoleRepository.FIND_SUMMARIES_LIKE_SHORT_NAME";

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
	public List<OrganizationRoleSummary> findAllLikeShortName(String shortName, OrganizationRoleType organizationRoleType) {
		String searchName = shortName + "%";
		String[] names = {"shortName", "roleType"};
		Object[] parms = {searchName, organizationRoleType.getCode()};
		return (List<OrganizationRoleSummary>) executeReportQuery(
				FIND_SUMMARIES_LIKE_SHORT_NAME,
				names,
				parms);
	}


	@Override
	public List<OrganizationRole> findOrganizationRoles(DefinedQuery query) {
		return executeDefinedQuery(
				organizationRoleColumnDefinitions,
				query);
	}


	@Override
	public OrganizationRole getByShortNameAndRoleType(
			String organizationShortName,
			OrganizationRoleType organizationRoleType) {

		String[] names = {"shortName", "roleType"};
		Object[] parms = {organizationShortName, organizationRoleType.getCode()};

		return executeSingleResultQuery(GET_BY_SHORT_NAME_ROLE_TYPE, names, parms);
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
