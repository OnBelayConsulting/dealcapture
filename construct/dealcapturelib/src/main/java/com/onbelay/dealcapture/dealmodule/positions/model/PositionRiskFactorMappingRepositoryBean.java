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
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.repository.PositionRiskFactorMappingRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository (value="positionRiskFactorMappingRepository")
@Transactional

public class PositionRiskFactorMappingRepositoryBean extends BaseRepository<PositionRiskFactorMapping> implements PositionRiskFactorMappingRepository {
	public static final String FIND_BY_DEAL_POSITION = "PositionRiskFactorMappingRepository.FIND_BY_DEAL_POSITION";
	public static final String FIND_MAPPING_SUMMARY = "PositionRiskFactorMappingRepository.FIND_MAPPING_SUMMARY";
	public static final String FIND_ALL_MAPPING_SUMMARIES = "PositionRiskFactorMappingRepository.FIND_ALL_MAPPING_SUMMARIES";

	@Override
	public PositionRiskFactorMapping load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return find(PositionRiskFactorMapping.class, entityId.getId());
		else
			return null;
	}

	@Override
	public List<PositionRiskFactorMapping> findByDealPosition(EntityId positionEntityId) {

		return executeQuery(
				FIND_BY_DEAL_POSITION,
				"positionId",
				positionEntityId.getId());
	}


	@Override
	public List<PositionRiskFactorMappingSummary> findMappingSummaries(
			EntityId positionEntityId,
			PriceTypeCode priceTypeCode) {
		String[] names = {"positionId", "priceTypeCode"};
		Object[] parms = {positionEntityId.getId(), priceTypeCode.getCode()};
		return (List<PositionRiskFactorMappingSummary>) executeReportQuery(
				FIND_MAPPING_SUMMARY,
				names,
				parms);
	}


	@Override
	public List<PositionRiskFactorMappingSummary> findAllMappingSummaries(List<Integer> positionIds) {
		if (positionIds.size() < 2000) {
			return (List<PositionRiskFactorMappingSummary>) executeReportQuery(
					FIND_ALL_MAPPING_SUMMARIES,
					"positionIds",
					positionIds);
		} else {
			ArrayList<PositionRiskFactorMappingSummary> summaries = new ArrayList<>(positionIds.size());
			SubLister<Integer> subLister = new SubLister<>(positionIds, 2000);
			while (subLister.moreElements()) {
				summaries.addAll(
						(List<PositionRiskFactorMappingSummary>) executeReportQuery(
								FIND_ALL_MAPPING_SUMMARIES,
								"positionIds",
								subLister.nextList()));
			}
			return summaries;
		}
	}


}
