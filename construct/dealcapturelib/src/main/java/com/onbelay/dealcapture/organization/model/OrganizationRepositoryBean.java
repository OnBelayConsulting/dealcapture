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

import com.onbelay.core.entity.model.AbstractEntity;
import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.organization.repository.OrganizationRepository;
import com.onbelay.dealcapture.organization.repository.OrganizationRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository (value="organizationRepository")
@Transactional

public class OrganizationRepositoryBean extends BaseRepository<Organization> implements OrganizationRepository {
	public static final String FIND_BY_SHORT_NAME = "OrganizationRepository.FIND_BY_SHORT_NAME";
	public static final String FIND_BY_EXTERNAL_REFERENCE = "OrganizationRepository.FIND_BY_EXTERNAL_REFERENCE";

	@Autowired
	private OrganizationColumnDefinitions organizationColumnDefinitions;

	@Override
	public Organization load(EntityId entityId) {

		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());



		if (entityId.isSet())
			return find(Organization.class, entityId.getId());
		else if (entityId.getCode() != null)
			return findByShortName(entityId.getCode());
		else
			return null;
	}


	@Override
	public Organization findByExternalReference(Integer externalReferenceId) {
		return executeSingleResultQuery(
					FIND_BY_EXTERNAL_REFERENCE,
					"externalReferenceId",
				externalReferenceId);
	}


	@Override
	public Organization findByShortName(String shortName) {
		return executeSingleResultQuery(
				FIND_BY_SHORT_NAME,
				"shortName",
				shortName);
	}



	@Override
	public List<Integer> findOrganizationIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				organizationColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<Organization> fetchByIds(QuerySelectedPage querySelectedPage) {
		return fetchEntitiesById(
				organizationColumnDefinitions,
				"Organization",
				querySelectedPage);
	}
	
	
	

}
