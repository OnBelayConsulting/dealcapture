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
import com.onbelay.dealcapture.pricing.assembler.IndexPriceSnapshotAssembler;
import com.onbelay.dealcapture.pricing.assembler.PricingIndexSnapshotAssembler;
import com.onbelay.dealcapture.pricing.model.IndexPrice;
import com.onbelay.dealcapture.pricing.model.PricingIndex;
import com.onbelay.dealcapture.pricing.repository.IndexPriceRepository;
import com.onbelay.dealcapture.pricing.repository.PricingIndexRepository;
import com.onbelay.dealcapture.pricing.service.PricingIndexService;
import com.onbelay.dealcapture.pricing.snapshot.IndexPriceSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service(value = "pricingIndexService")
@Transactional
public class PricingIndexServiceBean extends BaseDomainService implements PricingIndexService {

	@Autowired
	private PricingIndexRepository pricingIndexRepository;
	
	@Autowired
	private IndexPriceRepository indexPriceRepository;


	@Override
	public QuerySelectedPage findPricingIndexIds(DefinedQuery definedQuery) {

		return new QuerySelectedPage(
				pricingIndexRepository.findPricingIndexIds(definedQuery),
				definedQuery.getOrderByClause());
	}

	@Override
	public List<PricingIndexSnapshot> findByIds(QuerySelectedPage selectedPage) {
		List<PricingIndex> indices = pricingIndexRepository.fetchByIds(selectedPage);
		PricingIndexSnapshotAssembler assembler = new PricingIndexSnapshotAssembler();
		return assembler.assemble(indices);
	}

	@Override
	public List<IndexPriceSnapshot> fetchPricesByIds(QuerySelectedPage querySelectedPage) {
		List<IndexPrice> prices = indexPriceRepository.fetchByIds(querySelectedPage);
		IndexPriceSnapshotAssembler assembler = new IndexPriceSnapshotAssembler();
		return assembler.assemble(prices);
	}

	@Override
	public BigDecimal fetchPrice(
			EntityId pricingIndexId,
			LocalDate currentDate) {

		IndexPrice indexPrice = indexPriceRepository.fetchCurrentPrice(new EntityId(pricingIndexId), currentDate);
		if (indexPrice != null)
			return indexPrice.getDetail().getPriceValue();
		else
			return null;
	}

	@Override
	public PricingIndexSnapshot load(EntityId entityId) {

		PricingIndex pricingIndex = pricingIndexRepository.load(entityId);
		
		PricingIndexSnapshotAssembler assembler = new PricingIndexSnapshotAssembler();
		return assembler.assemble(pricingIndex);
	}

	@Override
	public TransactionResult savePrices(
			EntityId pricingIndexId,
			List<IndexPriceSnapshot> prices) {

		PricingIndex pricingIndex = pricingIndexRepository.load(pricingIndexId);
		
		pricingIndex.updatePricesWith(prices);
		
		return new TransactionResult(pricingIndexId);
	}
	

	@Override
	public TransactionResult save(PricingIndexSnapshot snapshot) {

		PricingIndex pricingIndex;
		
		if (snapshot.getEntityState() == EntityState.NEW) {
			pricingIndex = new PricingIndex(snapshot);
			return new TransactionResult(pricingIndex.generateEntityId());
		} else if (snapshot.getEntityState() == EntityState.MODIFIED){
			pricingIndex = pricingIndexRepository.load(snapshot.getEntityId());
			pricingIndex.updateWith(snapshot);
			return new TransactionResult(pricingIndex.generateEntityId());
		} else if (snapshot.getEntityState() == EntityState.DELETE) {
			pricingIndex = pricingIndexRepository.load(snapshot.getEntityId());
			pricingIndex.delete();
			return new TransactionResult();
		}
		
		return new TransactionResult();
	}
}
