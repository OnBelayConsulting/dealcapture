/*
 * Copyright (c) 2018-2019 OnBelay Consulting
 * All Rights Reserved
*/
package com.onbelay.dealcapture.businesscontact.model;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.businesscontact.repository.BusinessContactRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository (value="businessContactRepository")
@Transactional
public class BusinessContactRepositoryBean extends BaseRepository<BusinessContact> implements BusinessContactRepository {
	public static final String FETCH_ALL_BUSINESS_CONTACTS = "BusinessContactRepository.FETCH_ALL_BUSINESS_CONTACTS";
	public static final String FIND_BY_EMAIL = "BusinessContactRepository.FIND_BY_EMAIL";
    public static final String FIND_BY_EXTERNAL_REFERENCE = "BusinessContactRepository.FIND_BY_EXTERNAL_REFERENCE";

    @Autowired
	private BusinessContactColumnDefinitions businessContactColumnDefinitions;
	
	public BusinessContact load(EntityId entityId) {

		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isSet())
			return (BusinessContact) find(BusinessContact.class, entityId.getId());
		else if (entityId.getCode() != null)
			return  findByEmail(entityId.getCode());
		else
			return null;
	}
	

	
	@Override
	public List<Integer> findBusinessContactIds(DefinedQuery definedQuery) {
		
		return executeDefinedQueryForIds(
				new BusinessContactColumnDefinitions(), 
				definedQuery);
	
	}

	@Override
	public BusinessContact findByExternalReference(Integer externalRefenceId) {

		return executeSingleResultQuery(
				FIND_BY_EXTERNAL_REFERENCE,
				"externalReferenceId",
				externalRefenceId);
	}

	@Override
	public BusinessContact findByEmail(String email) {
		return executeSingleResultQuery(
				FIND_BY_EMAIL,
				"email",
				email);
	}

	@Override
	public List<BusinessContact> fetchByIds(QuerySelectedPage querySelectedPage) {
		
		return fetchEntitiesById(
				businessContactColumnDefinitions,
				"BusinessContact",
				querySelectedPage);
	}



}
