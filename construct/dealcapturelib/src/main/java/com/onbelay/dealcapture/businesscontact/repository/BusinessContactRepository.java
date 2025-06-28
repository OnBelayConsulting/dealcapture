/*
 * Copyright (c) 2018-2019 OnBelay Consulting
 * All Rights Reserved
*/
package com.onbelay.dealcapture.businesscontact.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.businesscontact.model.BusinessContact;

import java.util.List;

public interface BusinessContactRepository {
	public static final String BEAN_NAME = "businessContactRepository";
	
	public BusinessContact load(EntityId entityId);

	public BusinessContact findByEmail(String email);

	public BusinessContact findByExternalReference(Integer externalId);

	public List<Integer> findBusinessContactIds(DefinedQuery definedQuery);
	
	public List<BusinessContact> fetchByIds(QuerySelectedPage querySelectedPage);

}
