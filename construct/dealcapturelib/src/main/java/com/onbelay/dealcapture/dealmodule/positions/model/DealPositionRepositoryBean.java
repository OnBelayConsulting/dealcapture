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
package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionReport;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository (value="dealPositionsRepository")
@Transactional

public class DealPositionRepositoryBean extends BaseRepository<DealPosition> implements DealPositionRepository {
	public static final String FIND_BY_DEAL = "DealPositionsRepository.FIND_BY_DEAL";
    public static final String FIND_PHYSICAL_POSITION_REPORT_BY_DEAL ="DealPositionsRepository.FIND_PHYSICAL_POSITION_REPORT_BY_DEAL" ;
	public static final String FIND_PHYSICAL_POSITION_REPORTS ="DealPositionsRepository.FIND_PHYSICAL_POSITION_REPORTS" ;

    @Autowired
	private DealPositionColumnDefinitions dealPositionColumnDefinitions;

	@Override
	public DealPosition load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return find(DealPosition.class, entityId.getId());
		else
			return null;
	}

	@Override
	public List<DealPosition> findByDeal(EntityId dealEntityId) {

		return executeQuery(
				FIND_BY_DEAL,
				"dealId",
				dealEntityId.getId());
	}

	@Override
	public List<PhysicalPositionReport> findPhysicalPositionReportsByDeal(EntityId dealId) {
		return (List<PhysicalPositionReport>) executeReportQuery(
				FIND_PHYSICAL_POSITION_REPORT_BY_DEAL,
				"dealId",
				dealId.getId());
	}

	@Override
	public List<PhysicalPositionReport> findPhysicalPositionReports(List<Integer> positionIds) {
		return (List<PhysicalPositionReport>) executeReportQuery(
				FIND_PHYSICAL_POSITION_REPORTS,
				"positionIds",
				positionIds);
	}


	@Override
	public List<DealPosition> fetchByIds(QuerySelectedPage querySelectedPage) {
		return fetchEntitiesById(
				dealPositionColumnDefinitions,
				"DealPosition",
				querySelectedPage);
	}

	@Override
	public List<Integer> findPositionIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				dealPositionColumnDefinitions,
				definedQuery);

	}



}
