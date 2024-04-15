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
package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileDayRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository (value="powerProfileDayRepository")
@Transactional
public class PowerProfileDayRepositoryBean extends BaseRepository<PowerProfileDay> implements PowerProfileDayRepository {
	public static final String FETCH_POWER_PROFILE_DAYS = "PowerProfileDayRepository.FETCH_POWER_PROFILE_DAYS" ;


	@Override
	public PowerProfileDay load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return find(PowerProfileDay.class, entityId.getId());
		else
			return null;
	}

	@Override
	public List<PowerProfileDay> fetchPowerProfileDays(PowerProfile powerProfile) {
		return (List<PowerProfileDay>) executeQuery(
				FETCH_POWER_PROFILE_DAYS,
				"powerProfile",
				powerProfile);
	}

}
