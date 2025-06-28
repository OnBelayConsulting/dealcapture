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
package com.onbelay.dealcapture.job.model;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.job.repository.DealJobRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository (value="dealJobRepository")
@Transactional

public class DealJobRepositoryBean extends BaseRepository<DealJob> implements DealJobRepository {
	private static final Logger logger = LogManager.getLogger();
	public static final String FIND_MIN_START_DATE = "DealJobsRepository.FIND_MIN_START_DATE";
	public static final String FIND_MAX_START_DATE = "DealJobsRepository.FIND_MAX_START_DATE";

    @Autowired
	private DealJobColumnDefinitions dealJobColumnDefinitions;


	@Override
	public DealJob load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return find(DealJob.class, entityId.getId());
		else
			return null;
	}

	@Override
	public List<DealJob> fetchByIds(QuerySelectedPage querySelectedPage) {
		return fetchEntitiesById(
				dealJobColumnDefinitions,
				"DealJob",
				querySelectedPage);
	}


	@Override
	public List<Integer> findJobIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				dealJobColumnDefinitions,
				definedQuery);

	}



}
