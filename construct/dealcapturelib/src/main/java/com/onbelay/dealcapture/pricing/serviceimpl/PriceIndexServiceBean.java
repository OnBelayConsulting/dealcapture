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
import com.onbelay.dealcapture.pricing.assembler.PriceCurveSnapshotAssembler;
import com.onbelay.dealcapture.pricing.assembler.PriceIndexSnapshotAssembler;
import com.onbelay.dealcapture.pricing.model.PriceCurve;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.repository.PriceCurveRepository;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service(value = "pricingIndexService")
@Transactional
public class PriceIndexServiceBean extends BaseDomainService implements PriceIndexService {

	@Autowired
	private PriceIndexRepository priceIndexRepository;
	
	@Autowired
	private PriceCurveRepository priceCurveRepository;


	@Override
	public QuerySelectedPage findPricingIndexIds(DefinedQuery definedQuery) {

		return new QuerySelectedPage(
				priceIndexRepository.findPriceIndexIds(definedQuery),
				definedQuery.getOrderByClause());
	}

	@Override
	public List<PriceIndexSnapshot> findByIds(QuerySelectedPage selectedPage) {
		List<PriceIndex> indices = priceIndexRepository.fetchByIds(selectedPage);
		PriceIndexSnapshotAssembler assembler = new PriceIndexSnapshotAssembler();
		return assembler.assemble(indices);
	}

	@Override
	public List<PriceCurveSnapshot> fetchPricesByIds(QuerySelectedPage querySelectedPage) {
		List<PriceCurve> prices = priceCurveRepository.fetchByIds(querySelectedPage);
		PriceCurveSnapshotAssembler assembler = new PriceCurveSnapshotAssembler();
		return assembler.assemble(prices);
	}

	@Override
	public BigDecimal fetchPrice(
			EntityId pricingIndexId,
			LocalDate currentDate) {

		PriceCurve priceCurve = priceCurveRepository.fetchCurrentPrice(new EntityId(pricingIndexId), currentDate);
		if (priceCurve != null)
			return priceCurve.getDetail().getPriceValue();
		else
			return null;
	}

	@Override
	public PriceIndexSnapshot load(EntityId entityId) {

		PriceIndex priceIndex = priceIndexRepository.load(entityId);
		
		PriceIndexSnapshotAssembler assembler = new PriceIndexSnapshotAssembler();
		return assembler.assemble(priceIndex);
	}

	@Override
	public TransactionResult savePrices(
			EntityId pricingIndexId,
			List<PriceCurveSnapshot> prices) {

		PriceIndex priceIndex = priceIndexRepository.load(pricingIndexId);
		
		priceIndex.updatePricesWith(prices);
		
		return new TransactionResult(pricingIndexId);
	}
	

	@Override
	public TransactionResult save(PriceIndexSnapshot snapshot) {

		PriceIndex priceIndex;
		
		if (snapshot.getEntityState() == EntityState.NEW) {
			priceIndex = new PriceIndex(snapshot);
			return new TransactionResult(priceIndex.generateEntityId());
		} else if (snapshot.getEntityState() == EntityState.MODIFIED){
			priceIndex = priceIndexRepository.load(snapshot.getEntityId());
			priceIndex.updateWith(snapshot);
			return new TransactionResult(priceIndex.generateEntityId());
		} else if (snapshot.getEntityState() == EntityState.DELETE) {
			priceIndex = priceIndexRepository.load(snapshot.getEntityId());
			priceIndex.delete();
			return new TransactionResult();
		}
		
		return new TransactionResult();
	}
}
