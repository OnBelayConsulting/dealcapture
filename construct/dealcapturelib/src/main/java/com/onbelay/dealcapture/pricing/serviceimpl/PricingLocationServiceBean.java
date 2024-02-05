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
package com.onbelay.dealcapture.pricing.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.pricing.assembler.PricingLocationSnapshotAssembler;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;
import com.onbelay.dealcapture.pricing.service.PricingLocationService;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(value = "pricingLocationService")
@Transactional
public class PricingLocationServiceBean extends BaseDomainService implements PricingLocationService {

	@Autowired
	private PricingLocationRepository pricingLocationRepository;
	

	@Override
	public PricingLocationSnapshot load(EntityId entityId) {
		

		PricingLocation pricingLocation = pricingLocationRepository.load(entityId);
		
		PricingLocationSnapshotAssembler assembler = new PricingLocationSnapshotAssembler();
		return assembler.assemble(pricingLocation);
	}

	@Override
	public TransactionResult save(PricingLocationSnapshot snapshot) {

		PricingLocation pricingLocation;
		
		if (snapshot.getEntityState() == EntityState.NEW) {
			pricingLocation = new PricingLocation(snapshot);
			return new TransactionResult(pricingLocation.getId());
		} else if (snapshot.getEntityState() == EntityState.MODIFIED) {
			pricingLocation = pricingLocationRepository.load(snapshot.getEntityId());
			pricingLocation.updateWith(snapshot);
			return new TransactionResult(pricingLocation.getId());
		} else if (snapshot.getEntityState() == EntityState.DELETE) {
			pricingLocation = pricingLocationRepository.load(snapshot.getEntityId());
			pricingLocation.delete();
			return new TransactionResult();
		} else {
			return new TransactionResult();
		}
	}

	@Override
	public TransactionResult save(List<PricingLocationSnapshot> snapshots) {
		TransactionResult overallResult = new TransactionResult();
		for (PricingLocationSnapshot snapshot : snapshots) {
			TransactionResult result = save(snapshot);
			if (result.getEntityId() != null)
				overallResult.getIds().add(result.getId());
		}
		return overallResult;
	}

	@Override
	public QuerySelectedPage findPricingLocationIds(DefinedQuery definedQuery) {
		return new QuerySelectedPage(
				pricingLocationRepository.findPricingLocationIds(definedQuery),
				definedQuery.getOrderByClause());
	}

	@Override
	public List<PricingLocationSnapshot> findByIds(QuerySelectedPage selectedPage) {
		List<PricingLocation> locations = pricingLocationRepository.fetchByIds(selectedPage);
		PricingLocationSnapshotAssembler assembler = new PricingLocationSnapshotAssembler();
		return assembler.assemble(locations);
	}
}
